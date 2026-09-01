import { createClient } from 'npm:@supabase/supabase-js@2.112.4'

const json = (body: unknown, status = 200) => new Response(JSON.stringify(body), {
  status,
  headers: { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' },
})

type CapabilityRow = {
  user_id: string
  verified_age_tier: string
  can_use_dms: boolean
}

type MemberRow = {
  thread_id: string
  user_id: string
  joined_at: string
}

type ProfileRow = {
  user_id: string
  username: string
  display_name: string | null
  avatar_path: string | null
}

type ThreadRow = {
  id: string
  updated_at: string
}

type MessageRow = {
  id: string
  thread_id: string
  sender_id: string
  body: string
  created_at: string
  expires_at: string | null
}

Deno.serve(async (req: Request) => {
  if (req.method !== 'GET') return json({ error: 'Method not allowed' }, 405)

  const authHeader = req.headers.get('Authorization')
  if (!authHeader?.startsWith('Bearer ')) return json({ error: 'Unauthorized' }, 401)

  const supabaseUrl = Deno.env.get('SUPABASE_URL')
  const anonKey = Deno.env.get('SUPABASE_ANON_KEY')
  const serviceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')
  if (!supabaseUrl || !anonKey || !serviceKey) return json({ error: 'Service unavailable' }, 503)

  const userClient = createClient(supabaseUrl, anonKey, {
    global: { headers: { Authorization: authHeader } },
    auth: { persistSession: false, autoRefreshToken: false },
  })
  const admin = createClient(supabaseUrl, serviceKey, {
    auth: { persistSession: false, autoRefreshToken: false },
  })

  const { data: userData, error: userError } = await userClient.auth.getUser()
  const user = userData.user
  if (userError || !user) return json({ error: 'Unauthorized' }, 401)

  const { data: callerCapability, error: capabilityError } = await admin
    .from('account_capabilities')
    .select('user_id,verified_age_tier,can_use_dms')
    .eq('user_id', user.id)
    .maybeSingle<CapabilityRow>()
  if (capabilityError) return json({ error: 'Could not verify messaging access' }, 500)
  if (!callerCapability || callerCapability.verified_age_tier !== '16_plus' || callerCapability.can_use_dms !== true) {
    return json({ error: 'Messaging is unavailable for this account' }, 403)
  }

  const { data: ownMemberships, error: ownMembershipError } = await admin
    .from('dm_members')
    .select('thread_id,user_id,joined_at')
    .eq('user_id', user.id)
    .order('joined_at', { ascending: false })
    .limit(100)
  if (ownMembershipError) return json({ error: 'Could not load conversations' }, 500)

  const threadIds = [...new Set((ownMemberships ?? []).map((row) => row.thread_id as string))]
  if (threadIds.length === 0) return json({ threads: [] })

  const [{ data: threads, error: threadError }, { data: members, error: memberError }] = await Promise.all([
    admin.from('dm_threads').select('id,updated_at').in('id', threadIds),
    admin.from('dm_members').select('thread_id,user_id,joined_at').in('thread_id', threadIds),
  ])
  if (threadError || memberError) return json({ error: 'Could not load conversations' }, 500)

  const memberRows = (members ?? []) as MemberRow[]
  const participantIds = [...new Set(memberRows.map((row) => row.user_id))]

  const [{ data: participantCapabilities, error: participantCapabilityError }, { data: blocks, error: blockError }] = await Promise.all([
    admin.from('account_capabilities').select('user_id,verified_age_tier,can_use_dms').in('user_id', participantIds),
    admin.from('user_blocks').select('blocker_id,blocked_id').or(`blocker_id.eq.${user.id},blocked_id.eq.${user.id}`),
  ])
  if (participantCapabilityError || blockError) return json({ error: 'Could not verify conversation safety' }, 500)

  const capabilityByUser = new Map<string, CapabilityRow>(
    ((participantCapabilities ?? []) as CapabilityRow[]).map((row) => [row.user_id, row]),
  )
  const blockedPairs = new Set<string>()
  for (const block of blocks ?? []) {
    const blocker = block.blocker_id as string
    const blocked = block.blocked_id as string
    if (blocker === user.id) blockedPairs.add(blocked)
    if (blocked === user.id) blockedPairs.add(blocker)
  }

  const membersByThread = new Map<string, MemberRow[]>()
  for (const member of memberRows) {
    const current = membersByThread.get(member.thread_id) ?? []
    current.push(member)
    membersByThread.set(member.thread_id, current)
  }

  const safeThreadIds = threadIds.filter((threadId) => {
    const threadMembers = membersByThread.get(threadId) ?? []
    if (!threadMembers.some((member) => member.user_id === user.id)) return false
    return threadMembers.every((member) => {
      const capability = capabilityByUser.get(member.user_id)
      return capability?.verified_age_tier === '16_plus' &&
        capability.can_use_dms === true &&
        !blockedPairs.has(member.user_id)
    })
  })
  if (safeThreadIds.length === 0) return json({ threads: [] })

  const safeParticipantIds = [...new Set(
    safeThreadIds.flatMap((id) => (membersByThread.get(id) ?? []).map((member) => member.user_id)),
  )]
  const { data: profiles, error: profileError } = await admin
    .from('profiles')
    .select('user_id,username,display_name,avatar_path')
    .in('user_id', safeParticipantIds)
  if (profileError) return json({ error: 'Could not load conversation profiles' }, 500)
  const profileByUser = new Map<string, ProfileRow>(
    ((profiles ?? []) as ProfileRow[]).map((profile) => [profile.user_id, profile]),
  )

  const latestByThread = new Map<string, MessageRow | null>()
  await Promise.all(safeThreadIds.map(async (threadId) => {
    const now = new Date().toISOString()
    const { data: latest, error } = await admin
      .from('dm_messages')
      .select('id,thread_id,sender_id,body,created_at,expires_at')
      .eq('thread_id', threadId)
      .or(`expires_at.is.null,expires_at.gt.${now}`)
      .order('created_at', { ascending: false })
      .limit(1)
      .maybeSingle<MessageRow>()
    if (error) throw error
    latestByThread.set(threadId, latest ?? null)
  })).catch(() => undefined)

  // Fail closed if any latest-message lookup failed rather than returning a partially misleading inbox.
  if (latestByThread.size !== safeThreadIds.length) return json({ error: 'Could not load recent messages' }, 500)

  const threadById = new Map<string, ThreadRow>(
    ((threads ?? []) as ThreadRow[]).map((thread) => [thread.id, thread]),
  )

  const result = safeThreadIds.flatMap((threadId) => {
    const thread = threadById.get(threadId)
    if (!thread) return []
    const threadMembers = (membersByThread.get(threadId) ?? [])
      .sort((a, b) => a.joined_at.localeCompare(b.joined_at) || a.user_id.localeCompare(b.user_id))
    const otherIds = threadMembers.map((member) => member.user_id).filter((id) => id !== user.id)
    const otherProfiles = otherIds.map((id) => profileByUser.get(id)).filter(Boolean) as ProfileRow[]
    const isGroup = threadMembers.length > 2
    const directProfile = !isGroup && otherProfiles.length === 1 ? otherProfiles[0] : null
    const groupTitle = otherProfiles
      .map((profile) => profile.display_name?.trim() || profile.username)
      .filter(Boolean)
      .sort((a, b) => a.localeCompare(b))
      .join(', ')
    const latest = latestByThread.get(threadId) ?? null

    return [{
      thread_id: threadId,
      updated_at: thread.updated_at,
      is_group: isGroup,
      participant_count: threadMembers.length,
      title: directProfile
        ? (directProfile.display_name?.trim() || directProfile.username || 'Conversation')
        : (groupTitle || 'Group conversation'),
      avatar_path: directProfile?.avatar_path ?? null,
      last_message_id: latest?.id ?? null,
      last_message_body: latest?.body ?? null,
      last_message_sender_id: latest?.sender_id ?? null,
      last_message_created_at: latest?.created_at ?? null,
      last_message_expires_at: latest?.expires_at ?? null,
      unread_count: null,
      archived: null,
    }]
  })

  result.sort((a, b) => {
    const aTime = Date.parse(a.last_message_created_at ?? a.updated_at)
    const bTime = Date.parse(b.last_message_created_at ?? b.updated_at)
    return bTime - aTime
  })

  return json({ threads: result })
})
