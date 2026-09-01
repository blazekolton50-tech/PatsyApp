-- Draft PR #41: safe authenticated PDM inbox projection.
--
-- Purpose:
-- - let an authenticated 16+ DM-capable thread member resolve the names/avatars of the
--   participants in threads they already belong to;
-- - derive direct-vs-group from actual membership count;
-- - return the real latest non-expired message metadata;
-- - keep unread/archive NULL because the current schema has no authoritative read/archive state.
--
-- This function must not turn Profiles into a generally searchable directory. The SECURITY
-- DEFINER boundary is deliberately narrow and re-checks DM capability + membership + participant
-- capability before reading participant profile rows.

create or replace function public.patsy_dm_inbox()
returns table (
  thread_id uuid,
  updated_at timestamptz,
  is_group boolean,
  participant_count integer,
  title text,
  avatar_path text,
  last_message_id uuid,
  last_message_body text,
  last_message_sender_id uuid,
  last_message_created_at timestamptz,
  last_message_expires_at timestamptz,
  unread_count integer,
  archived boolean
)
language plpgsql
security definer
set search_path = ''
as $$
declare
  caller uuid := auth.uid();
begin
  if caller is null then
    raise exception 'unauthorized' using errcode = '42501';
  end if;

  -- Protected/unknown/restricted accounts cannot use DMs. This mirrors the existing hard DM gate.
  if not exists (
    select 1
      from public.account_capabilities c
     where c.user_id = caller
       and c.verified_age_tier = '16_plus'
       and c.can_use_dms = true
  ) then
    raise exception 'dm unavailable' using errcode = '42501';
  end if;

  return query
  with caller_threads as (
    select t.id, t.updated_at
      from public.dm_threads t
      join public.dm_members mine
        on mine.thread_id = t.id
       and mine.user_id = caller
  ),
  member_rollup as (
    select ct.id as thread_id,
           ct.updated_at,
           count(m.user_id)::integer as member_count,
           array_agg(m.user_id order by m.joined_at, m.user_id) as member_ids
      from caller_threads ct
      join public.dm_members m on m.thread_id = ct.id
     group by ct.id, ct.updated_at
  ),
  -- A thread becomes invisible rather than leaking identity if any participant is no longer an
  -- active, DM-capable 16+ account. Existing message history stays protected by the base RLS.
  safe_threads as (
    select mr.*
      from member_rollup mr
     where not exists (
       select 1
         from unnest(mr.member_ids) participant_id
         left join public.account_capabilities pc on pc.user_id = participant_id
        where pc.user_id is null
           or pc.verified_age_tier <> '16_plus'
           or pc.can_use_dms <> true
     )
  ),
  visible_threads as (
    select st.*
      from safe_threads st
     where not exists (
       select 1
         from public.user_blocks b
        where b.blocker_id = caller
          and b.blocked_id = any(st.member_ids)
     )
     and not exists (
       select 1
         from public.user_blocks b
        where b.blocked_id = caller
          and b.blocker_id = any(st.member_ids)
     )
  )
  select
    vt.thread_id,
    vt.updated_at,
    (vt.member_count > 2) as is_group,
    vt.member_count as participant_count,
    case
      when vt.member_count = 2 then coalesce(other_profile.display_name, other_profile.username, 'Conversation')
      else coalesce(group_names.title, 'Group conversation')
    end as title,
    case when vt.member_count = 2 then other_profile.avatar_path else null end as avatar_path,
    latest.id as last_message_id,
    latest.body as last_message_body,
    latest.sender_id as last_message_sender_id,
    latest.created_at as last_message_created_at,
    latest.expires_at as last_message_expires_at,
    null::integer as unread_count,
    null::boolean as archived
  from visible_threads vt
  left join lateral (
    select p.username, p.display_name, p.avatar_path
      from unnest(vt.member_ids) member_id
      join public.profiles p on p.user_id = member_id
     where member_id <> caller
     order by p.display_name nulls last, p.username
     limit 1
  ) other_profile on true
  left join lateral (
    select string_agg(coalesce(p.display_name, p.username), ', ' order by coalesce(p.display_name, p.username)) as title
      from unnest(vt.member_ids) member_id
      join public.profiles p on p.user_id = member_id
     where member_id <> caller
  ) group_names on true
  left join lateral (
    select m.id, m.body, m.sender_id, m.created_at, m.expires_at
      from public.dm_messages m
     where m.thread_id = vt.thread_id
       and (m.expires_at is null or m.expires_at > now())
     order by m.created_at desc, m.id desc
     limit 1
  ) latest on true
  order by coalesce(latest.created_at, vt.updated_at) desc, vt.thread_id;
end;
$$;

revoke all on function public.patsy_dm_inbox() from public;
revoke all on function public.patsy_dm_inbox() from anon;
grant execute on function public.patsy_dm_inbox() to authenticated;

comment on function public.patsy_dm_inbox() is
  'Member-only 16+ PDM inbox projection. unread_count and archived intentionally remain NULL until authoritative server state exists.';
