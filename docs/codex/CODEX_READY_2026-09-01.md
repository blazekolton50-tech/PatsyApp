# CODEX READY — PATSY / THyNK MASTER HANDOFF — 2026-09-01

Work branch: `chatgpt/codex-ready-brand-ui-lock-2026-09-01`
Base: green Camera foundation `78009aa63cdbdb0ae1d33a537ec14392f0b3380b` from Draft PR #39.

KEEP ALL WORK DRAFT. DO NOT MERGE.
DO NOT REBUILD THE APP FROM SCRATCH.
DO NOT WEAKEN AUTH, OWNER GATES, UNDER-16 RULES, RLS, PROVIDER BOUNDARIES OR THE REAL CAMERA FOUNDATION.

## 1. OFFICIAL BRAND ASSETS — MUST USE REAL PNG FILES

Two owner-supplied production logo files are the source of truth and must be copied into Android resources before replacing current fallbacks:

1. `THYNK_badge_sticker_transparent (1)(3).png`
   - SHA-256: `be8310ca168ff43aa79a609f1a06f73165ac3cbb0ff694ce2fb8132adb2e362d`
   - target path: `app/src/main/res/drawable-nodpi/thynk_logo_official.png`

2. `THYNK_Music_rainbow_matched_transparent(1).png`
   - SHA-256: `5f666b51c93e475be0511aaaec4d11e1744e9194b331f84a4bb4c2386dcf3423`
   - target path: `app/src/main/res/drawable-nodpi/thynk_music_logo_official.png`

Do NOT redraw these logos.
Do NOT recreate them with text, SVG text, system fonts, gradients or CSS.
Do NOT add duplicate text below them.

The existing official Patsy logo remains:
`app/src/main/res/drawable-nodpi/patsy_logo_official_white.png`

### Header branding rule
- Patsy app pages: official Patsy logo.
- Every THyNK page by default: official THyNK logo.
- Music editing pages: THyNK Music logo replaces THyNK.
- Video editing pages: THyNK Music logo replaces THyNK.
- No other area uses THyNK Music unless explicitly changed by owner.

## 2. GLOBAL HOME BAR — EXACT LOCK

One shared homebar on every authenticated page and every THyNK/editor page.

Visible order:
`HOME · [official THyNK logo] · large + CAMERA · PDMs · PROFILE`

Rules:
- Do not display a second `THyNK` caption below the THyNK image.
- Centre `+` is Camera and must route to the existing real native Camera hub.
- Keep the large white circle with rainbow ring/glow.
- Rainbow separator geometry is EXACT:
  - straight line from left edge toward centre,
  - smooth raised arch only around/over the centre Camera +,
  - returns to the same baseline,
  - straight line to right edge.
- It is NOT a full-width wave.
- Editors must not replace, hide or duplicate this bar.

Current branch already introduces `FinalHomebarRainbowLine()` and uses it from `FinalPrimaryNavigationBar`.
`FinalHomeDestination.CREATE` is currently a legacy internal enum name that maps to the real Camera hub. Do not expose the word CREATE to users for this destination. A later internal rename to CAMERA is allowed only if all route/auth tests are updated safely.

## 3. PDMs — FINAL PAGE STRUCTURE

PDMs is one messaging workspace, not an inbox page followed by a separate full-screen message page.

On phone/tablet layout use the supplied split-view design adapted responsively:
- left pane: Inbox / conversation list,
- right pane: currently selected conversation.

Left pane:
- Search PDMs
- All
- Unread
- Groups
- Archived
- avatar
- name
- last-message preview
- timestamp
- unread badge

Right pane header:
- selected avatar
- display name
- presence/status
- Voice Call button
- Video Call button
- Conversation Info button

Conversation body:
- incoming/outgoing bubbles
- timestamps
- delivery/read state where supported
- photo/video/document previews where genuinely available
- reactions later

Composer:
- attachment +
- text field
- emoji/PawMoji entry
- Send

Voice/video calls:
- keep visible buttons and UI entry points,
- actual call transport remains `NOT_CONFIGURED` until a real WebRTC/calling/signalling implementation exists,
- never show a simulated `Calling...` or `Connected` state as production success.

DM retention target remains 3 days by default, subject to account/age/settings rules and backend enforcement.

Relevant new donor supplied in chat:
`Pasted text(20260901-014630).txt`
It already contains split view, call/video controls and message composition logic, but it is React donor code, not native Android production code.

## 4. PROFILE PAGE

Profile is a real primary destination reached from the permanent homebar.
Use official Patsy logo centred at top.
Do not place THyNK branding at the top of Profile.

Profile page should include the approved creator/profile foundations:
- profile image
- display name / handle / bio
- Edit Profile
- social/profile stats where backed by real data
- About Me
- recent projects
- saved projects/folders
- quick creator/post entry points where real
- schedule/calendar remain secondary destinations
- Remember Me section uses the locked paw behaviour

Owner Profile and Owner Tools remain owner-only protected areas. Do not weaken authorization.
Dropdown/account menu is a separate UI task; do not merge it visually into the Profile page unless requested.

Relevant latest profile donor supplied in chat:
`Pasted text(20260901-012930).txt`
Use layout ideas only; replace fake counts/URLs/theme claims with real state or truthful placeholders.

## 5. THyNK DESIGN / CREATOR WORKSPACE

THyNK is a high-spec creator environment, not a generic dashboard.
Official THyNK logo at the top.
Permanent app homebar stays visible.

Main editor must grow toward:
- central editable canvas
- horizontal ruler
- vertical ruler
- draggable guides
- grids / margins / safe area / bleed where applicable
- snapping
- multi-layer editing
- selection / drag / resize / rotate
- duplicate / delete / lock / hide
- bring forward / send backward
- align / distribute
- group / ungroup
- undo / redo
- zoom / fit
- layers panel
- contextual properties panel

Tool rail target:
- Templates
- Upload
- Photos
- Elements
- Text
- AI Generate
- Stickers / PawMojis
- Draw
- Shapes
- Frames
- Background
- Layers
- Rulers & Guides
- More

Template route:
`THyNK → category → subcategory → template gallery → preview → Use Template → real editor`

High-spec publication/magazine mode must share the real editor engine and support multi-page professional layout: page thumbnails, spreads, master pages, linked text frames, overflow, columns, gutters, margins, baseline grid, bleed/crop marks, page numbers, headers/footers, image frames and text wrap.

Do not fake asset counts, AI availability, cloud save or professional export.

## 6. THyNK MUSIC — 10 SCREEN FOUNDATION

Use official THyNK Music logo on every Music page.
Permanent Patsy homebar remains visible.

Stable route IDs MUST remain:
- `music-home`
- `create-music`
- `ai-music-generator`
- `track-editor`
- `mixer`
- `equalizer`
- `effects`
- `lyrics-vocals`
- `mastering`
- `export`

Visual/functional target:
1. Music Home
2. Create Music
3. AI Music Generator
4. Track Editor
5. Mixer
6. Equalizer
7. Effects
8. Lyrics & Vocals
9. Mastering
10. Export

Keep advanced DAW direction for real creators: recording, multitrack timeline, vocals, beat, bass, keys, guitar/other instruments, FX, automation, meters, pan, EQ, effects, mastering and export.

Truth rules:
- no fake waveform bars presented as real audio,
- no fake AI percentage,
- no fake stems,
- no WAV payload renamed `.mp3`,
- no fake master/export success.
- AI music stays `NOT_CONFIGURED` until provider connected.

Local Web Audio donor code supplied earlier is useful as a behaviour reference but must not be claimed as integrated into native Android until equivalent native implementation exists.

The 100 WAV clip pack supplied in chat is valid sample material; provenance/rights wording must remain conservative until documented.

## 7. VIDEO EDITING

Video editing uses the THyNK Music logo under the current owner branding rule.

Keep the real Media3 / Camera handoff already present.
Grow the editor toward:
- preview
- timeline ruler
- playhead
- V1/V2 tracks
- A1/A2 audio tracks
- trim
- split
- delete/move
- crop/resize/rotate
- speed
- volume/fades
- text/image overlays
- captions
- transitions
- filters
- audio sync
- timeline zoom
- truthful export capability

Do not regress the real native Camera work in Draft PR #39.

## 8. CAPABILITY TRUTH MODEL

Use three states consistently:
- `WORKING` — real implementation exists and is connected.
- `LOCAL` — real local state/processing works without provider.
- `NOT_CONFIGURED` — provider/engine/backend is not connected.

Never substitute timers, alerts, decorative percentages or placeholder media for successful production operations.

## 9. PATSY APP BRANDING

Normal Patsy pages include Login, Password, Home, Newsfeed, PDMs, Profile, Camera/account/settings/Owner surfaces where applicable.
Use official Patsy logo on these surfaces.
THyNK/THyNK Music branding does not replace the Patsy app identity outside THyNK workspaces.

Main Patsy companion remains realistic and single-instance on a screen. Do not replace with cartoon Patsy; cartoon versions are PawMojis/stickers only.

## 10. EXECUTION ORDER FOR CODEX

1. Verify this branch is checked out and clean.
2. Copy the two owner-supplied PNG logo files to their exact resource paths and verify SHA-256.
3. Replace typed/recreated THyNK nav/header marks with `painterResource` using official PNGs.
4. Make THyNK header route-aware: THyNK default, THyNK Music for Music and Video Editor routes only.
5. Preserve/fix the shared homebar on every authenticated/protected/THyNK/editor route.
6. Verify centre + reaches the existing native Camera route.
7. Implement/port the PDM split-view screen using the native DM service boundaries; keep calls NOT_CONFIGURED.
8. Implement/refine the Profile screen separately; preserve Owner gates.
9. Continue the existing THyNK Design and 10-screen Music plans; do not restart them.
10. Run unit tests + debug build + release build.
11. Keep Draft. Do not merge.
12. Report exact files changed, tests, remaining NOT_CONFIGURED boundaries and device-only checks.

## 11. DO NOT DO

- Do not merge PR #38 or #39.
- Do not create a replacement app/repo.
- Do not remove auth/Owner/age gates.
- Do not swap official PNG logos for typed text.
- Do not put Patsy logo on THyNK editor headers.
- Do not use THyNK Music outside Music + Video editing under the current lock.
- Do not hide the permanent homebar in editors.
- Do not turn the homebar separator into a general wave.
- Do not fake AI, cloud, audio, calling or export functionality.
