# Patsy Source Intake — 2026-09-01

## Purpose
Record every newly reconciled source that may influence the Patsy Android app without allowing donor/reference material to overwrite the current native Kotlin/Compose source of truth.

## Source precedence
Use this order whenever sources disagree:
1. current code on `chatgpt/codex-ready-2026-09-01`, descended exactly from PR #39 head `78009aa63cdbdb0ae1d33a537ec14392f0b3380b`
2. checked-in design/navigation/auth/security locks
3. live Supabase backend/security truth
4. latest owner-supplied page/design requirements
5. verified owner-supplied files whose bytes/license/origin are known
6. Google Drive / AI Studio / Replit / Canva / archive donor material
7. older prototype/archive behavior only when it does not conflict with 1-6

No donor source may silently replace native architecture.

---

## A. Conversation uploads received 2026-09-01

### A1. `PatsyApp-AndroidStudio-THyNK-Music-artifact.zip`
Classification: `REFERENCE_ONLY` / native recovery snapshot.

Observed outer SHA-256:
`b7acc3da0c29b4052ca0ded2620aecbae709036f547cb17ca1dfde9b2cdee88f`

Observed outer size:
`50,253,038` bytes

The outer archive contains a nested project archive:
`PatsyApp-AndroidStudio-THyNK-Music.zip`

Nested archive SHA-256:
`226602593cadcb07aa468745d3f2e11de992c7751d920ae610d12c75f073b076`

Nested archive size:
`50,252,848` bytes

Useful content confirmed inside the nested project:
- native Android/Kotlin/Compose app structure
- FINAL screen/auth/account/Owner foundations
- THyNK ten-category catalogue
- THyNK Music ten-screen foundation
- Media3 video-player source
- Studio editor/tool state
- Rive host/runtime contract foundations
- Supabase functions/migrations
- locked design/navigation documents
- reference images/assets and existing build docs

Why it is not authoritative:
- its checked-in build/status docs identify an older verified milestone around `46cf3a...` / CI #138
- it predates the native Camera additions now present on PR #39
- it has no `CameraHubContract.kt` / `NativeCameraHub.kt` equivalent from the current native Camera branch

Decision:
- preserve as recovery/reference evidence
- never reset the live branch to this ZIP
- selectively recover only something proven missing from the newer branch
- any binary/reference asset promoted from it must still pass donor verification

### A2. `gemini-code-1788215509627.java`
Classification: `REJECTED` for production native port.

SHA-256:
`71c588b32a2286234ee398e5aa1015c11c4469ee09a426de587eaa5fc3a7901a`

Size:
`533` bytes

Observed behavior:
- `AppCompatActivity`
- full-screen Android `WebView`
- JavaScript enabled
- loads `file:///android_asset/MAIN_BUILT_MUSIC_PLAYER.html`

Decision:
Do not merge this activity into Patsy. THyNK Music remains native Kotlin/Compose. Browser HTML/WebAudio may be used only as behavior/reference material if independently useful, then reimplemented natively and tested.

### A3. `gemini-code-1788215511233.xml`
Classification: `REJECTED` for production native port.

SHA-256:
`dcc056e2754f80317dcaa1cae766300914f93a8431b6693f7a613aa8728194a3`

Size:
`386` bytes

Observed behavior:
- layout contains only a full-screen `WebView`
- root `LinearLayout` contains `android:layout_width="match_dirname"`, which is not a valid Android layout-width value

Decision:
Do not merge. Do not replace Compose screen architecture with this XML/WebView wrapper.

---

## B. Google Drive donor/recovery material
All items below remain `REFERENCE_ONLY` unless actual contained bytes, provenance/license, checksum and duplicate/supersession status are verified.

Known relevant sources found in connected Drive include:
- `patsy_full_1110_real.zip` wrapper
- `patsy_thynk_stage1_real.zip` wrappers
- `PDF_Reader_SETUP_THYNK_STAGE2_REAL.py.txt`
- `Full_App_Backend_Frontend_Editor_1110_Bricks.zip` wrappers
- `Icons_100_Illustrations_100_Logos_50_Original.zip` wrapper
- `Social_50_BusinessCards_25_Slides_50_Original.zip` wrapper
- `Video_Transitions_25_Text_Animations_25_Overlays_25_Original.zip` wrapper
- `Upscale_Studio_Canva_Snapchat_Video_Photo_Sound_Architecture.zip` wrapper
- `Stickers_100_Patterns_50_Mockups_25_Original_Bricks.zip` wrapper
- `Schedule_25_Todo_25_Tailwind_Bricks.zip` wrapper
- `Fonts_50_Free_Pack_Codex_Tailwind.zip` wrapper
- `Poster_Templates_25_HTML_Original.zip` wrapper
- `CV_React_Tailwind_Bricks_10.zip` wrapper
- `CV_Templates_10_Original_Editable.zip` wrapper

The Stage 2 setup text explicitly describes itself as a THyNK continuation/donor implementation and includes web-oriented material. It must not overwrite native Kotlin/Compose architecture.

Owner-requested names such as `CV_Templates_20.zip`, `Poster_Templates_30.zip`, `Business_Cards_30.zip`, `School_Sheets_20.zip` and `ALL_TEMPLATES_100.zip` are design-intake targets, but these exact names were not verified in the current Drive search. Do not claim they are available until found or re-uploaded.

### Drive intake rule
A Drive wrapper/title proves only that a reference exists. It does not prove:
- archive bytes are accessible
- the archive contains what its title suggests
- the contained asset is original/licensed for app distribution
- the asset is not a duplicate/superseded version
- the asset is safe/compatible with Android

Use the donor manifest/verifier before surfacing any item in production THyNK.

---

## C. Replit
Connected Patsy project inspected:
`Patsy Android Companion`

Classification: `REFERENCE_ONLY`.

Current artifact remains an Expo/React Native prototype with:
- outdated Home / Chat / Create / Social / More local navigation
- client-local onboarding rather than real auth
- AsyncStorage profile/draft state
- placeholder THyNK creation screen
- no native Android Camera implementation
- no Supabase client/backend wiring
- no native Kotlin/Compose project
- static PNG Patsy with simple bob movement, not production Rive
- honest provider-unavailable placeholder response

Safe donor value is limited to selected UX ideas such as dark hierarchy, restrained rainbow accents, haptic/accessibility intent, prompt-first entry and honest unavailable-state copy.

Do not port Replit navigation/storage/auth architecture.

---

## D. Notion / connected workspace search
Notion is connected, but current searchable workspace results are primarily Notion starter/onboarding pages rather than Patsy engineering source material.

Decision:
- create/use Notion as a human-readable mirror/status hub only
- GitHub remains authoritative for code/contracts/implementation order
- never allow a stale Notion note to override current GitHub code/security truth

---

## E. Canva
Current connected Canva search surfaced:
`Patsy_Personal_Creator_Assistant_Master_Pack.docx`

Classification: personal/reference material, not native Android production UI authority.

Do not substitute personal creator-assistant assets for the main app’s locked realistic Patsy, logo or THyNK design system.

---

## F. Live Supabase
Supabase is an authoritative runtime/security source, not donor material.

Fresh 2026-09-01 verification:
- project status `ACTIVE_HEALTHY`
- 37 public tables with RLS enabled
- 10 private tables with RLS enabled
- 119 policies
- 4 private storage buckets
- Security Advisor 0 lints
- 0 Auth users
- 48 Studio presets

Relevant active functions:
- `create-dm-thread` v5 / JWT verified
- `account-bootstrap` v4 / JWT verified
- `owner-authorize` v1 / JWT verified
- auth login/register/reset functions active

Do not change RLS/service authority from donor code.

---

## G. Locked design/page entries Codex must carry forward

### App chrome
- charcoal/black base
- white/light-grey text
- white primary controls with restrained rainbow/neon glow
- exact approved Patsy wordmark asset
- one realistic main Patsy companion per page
- no static/cartoon replacement for main app Patsy

### Primary bottom navigation
Semantic:
`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible:
`Home · [coloured THyNK only] · [large + only] · PDMs · Profile`

### Top-right account menu
- Account
- About
- Profile
- Settings
- Remember Me

### Profile
Retain/align toward:
- profile picture and identity/bio
- social links
- gallery/media
- recent projects
- saved projects
- Schedule and Calendar as secondary tools
- Remember Me
- server-gated Owner Profile and Owner Tools

### THyNK Design & Templates
Retain current category items and build through the generic canvas rather than a parallel editor.

First production design slice:
- presets/custom size
- layers/transforms
- undo/redo
- Android image import
- real PNG export
- verified template intake only
- autosave/restore when navigating away once project persistence is wired

### THyNK Music
Keep ten route IDs stable and refine the native screens rather than using a WebView wrapper.
Provider-backed generation/mastering/vocal processing/audio export remain unavailable until genuinely configured.

---

## H. Promotion criteria for any donor asset
No record becomes production-ready unless all required evidence exists:
- stable ID
- known origin
- category/subcategory/type
- actual bytes or a durable native resource reference
- approved licensing/origin status
- SHA-256 checksum when bytes exist
- duplicate/supersession decision
- Android compatibility check
- availability explicitly promoted to `VERIFIED`

Fail closed. Unknown or wrapper-only items stay `REFERENCE_ONLY`. Incompatible/browser-wrapper code may be marked `REJECTED`.
