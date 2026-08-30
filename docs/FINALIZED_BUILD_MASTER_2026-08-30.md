# Patsy App / THyNK — Finalized Build Master

**Date:** 2026-08-30  
**Status:** MAIN APP / MASTER / SAVE + LOCK IN / implementation source of truth  
**Production path:** Native Android / Kotlin / Jetpack Compose  
**Repository:** `blazekolton50-tech/PatsyApp`

This document reconciles the current MAIN APP SAVE rules, SAVE/LOCK IN history, current Library master documents, the latest user-supplied code/files, current GitHub branches/CI, live Supabase schema, Replit prototype audit, and the current Rive Android/data-binding direction.

It is a build-control document, not evidence that every feature below is already implemented.

## 1. Authority and change control

Source precedence is locked as follows:

1. Newest explicit `MAIN APP SAVE`, `SAVE MAIN APP`, `SAVE`, or `LOCK IN` approval.
2. `PATSY_MASTER_EVERYTHING_LOCKED_2026-08-30` / CODEX master documents.
3. `PATSY_MASTER_PROJECT_INDEX` and pack-specific locked specifications.
4. Approved source assets: Patsy wordmark, THyNK logo, real Patsy references, exact branded paw, approved Studio assets.
5. Current verified GitHub/Supabase implementation state.
6. Older mockups, screenshots, generated boards, ZIPs, Replit, React/Vite and Google Studio output only as recovery/reference material when they do not conflict with newer MASTER rules.

A newer locked decision supersedes an older conflicting one. The older rule is marked SUPERSEDED rather than silently deleted.

No screen, mockup, metadata list, generated ZIP description or placeholder service is proof of production completion.

No merge to `main` without explicit approval.

## 2. Clear product vision

Patsy is an Android-first app built around one persistent realistic AI companion and one integrated creation environment.

The app combines:

- Patsy as a persistent AI/search/help companion;
- THyNK Studio for image, video, document, homework, collage, meme, camera and original-music creation;
- user projects, device-first/private storage and Remember Me memory;
- Patsy Social;
- direct and group messaging;
- profile, settings, privacy and accessibility;
- calendar, reminders, scheduling and publishing adapters;
- server-authoritative Owner tools;
- a separately protected under-16 account domain;
- a separate PawMoji Android keyboard.

Patsy is not a static mascot. She teaches, guides, reacts, notices context, points at controls, moves around safe UI regions, speaks through a real animation/speech contract when configured, and remains truthful about unavailable services.

## 3. Locked visual system

### Main app

- Black/charcoal background.
- White/light-grey primary text.
- White primary buttons with black/charcoal text.
- Restrained rainbow/neon accents and glow; never rainbow everywhere.
- Green Save treatment where applicable.
- Red Cancel/destructive/logout treatment where applicable.
- Rounded premium charcoal cards.
- Exact Patsy wordmark centred where specified.
- Exact tagline: `A LEGACY LED BY PAWS`.

### Shared design-token direction from the latest supplied Kotlin

Consolidate into one shared Android design system rather than duplicate per-feature `PatsyColors` objects:

- Background `#121212`
- Surface `#1A1A1A`
- Surface 2 `#242424`
- Surface 3 `#2E2E2E`
- Border `#3A3A3A`
- Primary text `#FFFFFF`
- Secondary text approximately `#A0A0A0`
- Tertiary text approximately `#6B6B6B`
- Patsy purple target `#8B5CF6`
- Success green target `#00C853`
- full restrained rainbow sequence: pink → orange → yellow → green → cyan → purple

CSS strings such as `box-shadow` are not Android Compose values and must be translated into real Compose brushes/elevation/glow behavior rather than copied literally.

### Patsy wordmark

The wordmark is supplied artwork, not a recreated font. Preserve exact brush shape, spacing, rough ends and the paw cutout in the lowercase `a`.

Do not use `DontWorryBePatsy` public-page branding inside the app as a substitute for the approved Patsy app branding.

### THyNK logo

- Use the approved TH/NK block treatment.
- The centre lowercase `y` is the exact approved white brushstroke `y` derived from the Patsy logo.
- Preserve the approved overlap/thin-outline treatment.
- Do not redraw, approximate or modernise it.
- Mockups containing an inaccurate THyNK logo are layout reference only.

## 4. Locked navigation

Primary bottom navigation remains exactly:

`HOME • THyNK • CREATE • PATSY_DMS • PROFILE`

The central app brand stays centred at the top where specified. `More` belongs outside the primary five-item bottom navigation.

## 5. Patsy identity and Rive production contract

### Visual identity

Production Patsy is the approved realistic/high-grade grey shaggy Patsy:

- small grey Maltipoo-type appearance;
- large white chest and white muzzle treatment;
- darker grey tail;
- darker, longer, narrower, straighter/lower-hanging ears;
- dark/brown expressive eyes;
- black nose;
- slightly scruffy top hair;
- natural slim proportions;
- transparent/unboxed presentation over the live UI.

Cartoon Patsy belongs to PawMoji/sticker contexts only. Never replace the main app companion with a cartoon, generic Maltipoo, fox/cat-like mascot or static avatar card.

### Rive ABI — current locked Android contract

- Artboard: `PatsyAssistant`
- State machine: `PatsyAssistantMachine`
- View Model: `PatsyAssistantVM`
- Default instance: `Default`

Required property families:

- `motion/*`
- `stage/*`
- `head/*`
- `ears/*`
- `tail/*`
- `face/*`
- `speech/*`

Required behavior includes:

- breathing/weight-shift idle;
- blink and independent gaze;
- head look/tilt/turn;
- independent ear drives and secondary motion;
- tail drive/energy;
- walk/sit/lie durable states;
- retriggerable jump/wave/point actions;
- target-aware notice/look/point;
- cheeky, excited, curious, confused, concerned, proud, sleepy and neutral expression families;
- timestamped speech visemes;
- shrink/expand;
- safe-screen reposition/return;
- reduced-motion behavior.

Display/interaction states also include MINI, GUIDE, FULL, PEEK, REACT, REST and HIDDEN at the app level.

### Current Rive truth

The Android rig ABI, runtime adapter, semantic companion controller and transparent fallback are real code. A genuine production `patsy_assistant.riv` is still not verified/present. Do not fake this with GIFs, sprites, video loops, PNG pose swaps or a fabricated `.riv`.

Current Rive guidance supports the chosen architecture: modern Android Compose integration can bind a View Model instance into the `Rive` composable, nested View Model properties can be addressed by slash-delimited paths, and Rive recommends data-bound View Model properties for new work rather than expanding legacy state-machine inputs.

### Replit comparison

The current Replit app is a useful Expo/React Native UX prototype only. It genuinely has a static Patsy PNG, a simple vertical bob, basic local onboarding/profile/draft state and honest `NOT_CONFIGURED` messages. It does not have Rive, real auth, DMs, Owner tools, THyNK editor, production storage, server age enforcement or a native Android architecture.

Preserve from Replit only:

- Patsy presence throughout the product;
- companion-led greeting/copy;
- private-draft language;
- truthful unavailable-service states;
- accessibility/haptic intent;
- useful information architecture.

Do not port Expo Router, AsyncStorage authentication, client-only age modes, circular avatar treatment, static bob as production animation, or the generic creation mockup.

## 6. Auth, onboarding and account UI

### First and returning greetings

First identity introduction is exactly:

`Hi, I’m Patsy! Your AI Pet Pal!`

Returning greetings should vary safely with context/familiarity without inventing sensitive intimacy. Approved examples include:

- `Heeeyy! You’re back! Need anything?`
- `Hi! What we doing today?`
- `Morning! What are we getting into?`
- `You’re back! Wanna carry on where we left off?`
- `Heyyy — got an idea or are we winging it today?`

### Auth Remember Me vs Patsy durable Remember Me

These are two distinct concepts and must not be conflated:

1. **Login-screen Remember Me** — session persistence only; never stores a password.
2. **Patsy Remember Me / Locked In memory** — durable per-user memory/keepsakes/preferences under explicit privacy controls.

Both use truthful result handling. A UI animation may not imply persistence if the real operation failed.

### Account menu — locked four items

Exact order/stable IDs:

1. `my_account` — My Account — Username, password, info
2. `security_privacy` — Security & Privacy — PIN, 2FA, devices, privacy
3. `patsy_settings` — Patsy Settings — Patsy's personality & prefs
4. `log_out` — Log Out — Sign out of your account

The larger pasted 13-item menu does not replace this compact account menu. Broader destinations can live in More/settings.

Logout must call the real auth boundary and only navigate/report success after sign-out is confirmed.

## 7. Exact branded master paw

Canonical Android resource target:

`R.drawable.patsy_master_paw`

This must be the exact approved branded paw derived from the Patsy wordmark/Remember Me design. Do not substitute a generic cream/fluffy Maltipoo paw or Unicode paw.

Approved interaction:

- small restrained scale pulse;
- slight tilt;
- restrained rainbow glow/fill acknowledgement;
- approximately 140 ms acknowledgement before navigation when motion is enabled;
- debounce repeated taps;
- reduced-motion path removes bounce/rotation/delay;
- no mandatory woof;
- no fake waveform icon;
- optional haptic/chime only through real preference controls.

Current blocker: this exact drawable is not yet verified in the production branch resources.

## 8. Profiles, Owner screens and DMs design direction

The newest approved dark Patsy screens are the visual source for Owner Toolbox, Owner Profile, normal User Profile, DM Inbox, individual chat, group chat, DM settings and chat info. Older purple/space/fox/cartoon or white-background generated boards are rejected as final design direction.

All these screens must retain:

- black/charcoal surface system;
- exact Patsy brand assets;
- realistic/unboxed Patsy where she appears;
- white controls and restrained rainbow accents;
- exact bottom navigation;
- truthful capability state;
- age/privacy/report/block controls.

No fabricated follower/project/media/analytics counts.

## 9. Server-authoritative Owner architecture

Owner authority is never granted by:

- username;
- local boolean;
- hidden route;
- modified APK state;
- profile field writable by the user;
- client-selected role.

The existing native `OwnerAuthorizationService`, `OwnerCapability`, `OwnerAuthorizationDecision` and `FailClosedOwnerAuthorizationGate` remain the Android contract.

Existing capabilities include:

- `VIEW_OWNER_PROFILE`
- `VIEW_OWNER_TOOLS`
- `MANAGE_CONTENT`
- `MANAGE_SCHEDULE`
- `VIEW_ANALYTICS`
- `VIEW_SECURITY_AUDIT`
- `MANAGE_PRIVACY`
- `MANAGE_BACKUPS`

Add a dedicated secure external-data-link capability before exposing that feature; do not reuse generic Owner-unlocked state.

### Proposed Supabase backing

A dedicated server-controlled `owner_capabilities`/equivalent record may back the provider implementation, but:

- do not add a client-writable `profiles.is_owner` authority flag;
- normal clients must never insert/update/delete Owner grants;
- server/service operations issue capability decisions;
- screens must request the specific capability;
- every privileged backend action rechecks authorization even after the screen route was opened.

Current live Supabase does **not** yet contain `owner_capabilities`; therefore the latest pasted SQL is design input, not an applied migration.

## 10. Under-16 security — hard domain separation

Under-16 is a separate security/account domain, not a different theme or hidden menu.

Locked rule:

- `UNDER_16` ordinary accounts and `STANDARD_16_PLUS` ordinary accounts are mutually isolated.
- Neither domain can ordinarily search, discover, view profiles/posts/comments, follow, message, receive messages from or otherwise access the other.
- Deep links, guessed IDs, cached data, forged requests, modified clients, realtime subscriptions, search indices, storage URLs and notification payloads must not bypass the boundary.
- Unknown/uncertain age receives the protected child treatment until an approved transition.
- Child-safe messaging/media restrictions apply inside the protected domain.
- Privileged safeguarding/Owner paths remain separately authorised.

The boundary must be enforced across database, storage, realtime, search, APIs, profiles, DMs, Social and notifications.

## 11. Live Supabase foundation

Live project: `tvtknwqcqbkecszvppub`.

Current table scan confirms RLS-enabled tables including:

- profiles / user_settings / patsy_memories;
- projects / albums / media_assets;
- posts / post_media / comments / post_likes;
- dm_threads / dm_members / dm_messages / dm_attachments;
- creator_jobs;
- calendar_items / scheduled_content / notifications;
- user_connections / user_blocks / safety_reports;
- account_capabilities / consents / requests / device registrations;
- studio_templates / studio_assets / studio_template_assets / studio_asset_usage / studio_presets / studio_project_state / studio_layers / studio_revisions / studio_user_favorites.

`studio_presets` currently has 48 rows; this is real preset metadata, not proof that the complete Studio/template programme exists.

Preserve the live backend rather than replacing it with a generic Express/Drizzle/Postgres stack.

## 12. Storage and retention

Locked production targets:

- feed/shared media: 90 days unless validly saved/locked/preserved;
- DMs: 3-day default, subject to age/safety policy;
- locked-profile allowance: 100 pictures + 30 videos;
- profile photo excluded from the quota where the established rule applies;
- device-first media storage where practical;
- private server storage for sync/sharing/server-required workflows;
- warning before expiry and options to save/download/export/lock;
- emailing a copy does not automatically lock it.

Studio-owned rotating catalogue assets have a separate 90-day retirement/refresh process. It must never delete user content, master brand assets, active template dependencies, legal/provenance records or saved/favourited/locked assets.

## 13. THyNK Studio — finalized vision

Use one shared editor engine with mode-specific capabilities.

Locked key product surfaces include:

1. Home Dashboard
2. Studio Home
3. Create New
4. Templates
5. Editor
6. AI Image Generator
7. AI Video Generator
8. My Projects
9. Brand Kit
10. Inspiration

Additional modes include Camera, Meme, Collage, Document/Homework and Music Creator.

### Canvas sizing

Neutral production presets:

- Square — 1080×1080 — 1:1
- Portrait — 1080×1350 — 4:5
- Full Vertical — 1080×1920 — 9:16
- Landscape — 1920×1080 — 16:9
- Wide — 1200×628 — 1.91:1
- Custom Size

Visible copy: `All social media sizing available.`

Do not use third-party platform branding/trade dress merely to name a canvas size.

### Shared editor requirements

Image/editor:

- add/remove/select/move/resize/rotate;
- crop/cutout/background removal;
- layers, opacity, grouping, ordering;
- frames/borders/masks;
- text/typography;
- uploads/photos;
- filters/adjustments;
- mirror/flip;
- pen/eraser;
- rulers/guides/grid/snapping/alignment/spacing;
- collage editing;
- undo/redo/reset;
- contextual animation where appropriate.

Video:

- real preview player;
- multitrack timeline;
- draggable playhead and timeline zoom;
- snapping;
- trim/split/delete/duplicate/reorder;
- crop/resize/speed;
- transitions;
- captions/text/overlays;
- filters/effects/adjustments/animation;
- audio tracks/waveform/volume/mute/fades;
- clip timing/markers;
- real export.

### AI creation

AI Image and AI Video use real asynchronous provider-backed server jobs only:

Android UI → authenticated Patsy backend/Edge Function → capability-authorised provider → validated private result → preview → user approval → save/export.

Provider/model identifiers remain server configuration.

The locked AI Video target is exactly 10 seconds.

### Original catalogue

Minimum production target:

- 100 editable image templates;
- 50 editable video templates;
- reusable original music/sound clips.

These are targets, not current completion claims. Prove real editable assets in a smaller validation batch before scaling.

The historical `1110` catalogue structure can be mined for categories/metadata ideas, but JSON count alone is not an editable production programme.

All Studio templates, effects, filters, transitions, elements, audio and supporting content must be original/licensed with provenance. Do not copy Canva or competitor packs.

## 14. Current THyNK GitHub status

PR #24 is an isolated draft verification branch and currently RED by design.

Current failing requirements:

- deterministic forward/backward editor commands;
- richer layer-state types;
- project/artifact separation;
- catalogue validator rejecting forbidden visible names and unsafe bundling.

Existing useful native pieces include basic sizing/reflow, editor history, core layer operations, basic timeline, provider-neutral project/media/export contracts, truthful catalogue-only effect states and fail-closed camera/export boundaries.

Do not add renderer/export layers until the current RED contract is satisfied and verified GREEN.

## 15. Renderer/export extraction from pasted code

Keep concepts, not the alternate package tree.

Useful ideas:

- deterministic z-order rendering;
- visibility/opacity/rotation/flip;
- text/image/sticker/PawMoji/shape branches;
- crop validation;
- render diagnostics;
- same renderer for thumbnails and image export;
- safe path handling;
- real PNG/JPEG compression;
- failure instead of fake export completion.

Required corrections:

- reuse existing `com.patsy.app.studio.*` contracts;
- real asset/media resolver;
- memory/dimension guard before bitmap allocation;
- no silent font substitution;
- transparent PNG support;
- JPEG opaque background rule;
- unresolved required layers prevent COMPLETE;
- cancellation/failure explicit;
- no placeholder files counted as exports.

## 16. PawMoji keyboard

Main companion remains realistic Patsy. PawMojis remain cartoon Patsy only.

Locked Android keyboard requirements:

- black keyboard;
- rainbow letters/outlines;
- normal text entry;
- normal emoji access;
- PawMoji picker;
- favourites/recents;
- normal keyboard category positions preserved;
- easy switch back to regular keyboard;
- optional younger-user wording/spelling/emoji assistance;
- correction suggestions are tap-to-accept and never auto-send.

PawMojis are custom assets/stickers, not fake Unicode emoji.

Red Race Car Patsy supersedes the retired blue race-car PawMoji.

## 17. Social, DMs and publishing

- Home is the Patsy Social feed in the established navigation model.
- DMs support individual/group flows, attachments and safety controls.
- Default DM expiry is 3 days.
- Block/report/privacy controls are first-class.
- Under-16/16+ domain isolation applies to discovery, profiles, Social and DMs.
- Scheduling/publishing requires explicit authenticated approval and real provider confirmation.
- Never fake OAuth, send, publish or schedule success.
- Only show a social platform name and logo when usage is permitted for the real integration; otherwise remove both and use generic `Share`, `Publish` or `Download to Share` wording.

## 18. Fonts and licence evidence

`FONT-LICENSE-EVIDENCE.pdf` records ownership assertions for:

- Patsy-Brush-Original;
- THyNK-Sans-ExtraBold-Original;
- THyNK-Script-Y-Original;
- Patsy-Poppins-Original;
- Patsy-Studio-Mono-Original;

and identifies Inter/Poppins/Montserrat as OFL/free sources plus other stated free fonts.

Current limitation: the evidence file references `.txt` descriptions, not verified `.ttf`/`.otf` binaries. Therefore custom fonts are not yet considered bundled/verified.

Before production font integration obtain/verify:

- actual `.ttf`/`.otf` binary;
- unique family/PostScript name;
- version;
- glyph coverage;
- SHA-256;
- source/design provenance;
- commercial-use evidence;
- successful font parse/load.

Avoid naming a new original font `Patsy-Poppins-Original`; use a unique original family name so it cannot be confused with the OFL Poppins family.

The exact Patsy logo remains artwork even if a brush font exists.

## 19. Archive/file truth

The supplied files named:

- `patsy_thynk_stage1_real.zip (1).html`
- `patsy_full_1110_real.zip (1).html`
- `Full_App_Backend_Frontend_Editor_1110_Bricks.zip.html`

are HTML preview/download-placeholder pages, not the ZIP archive bytes themselves. They cannot be treated as inspected archives. If the actual ZIP bytes become available, inspect them against this master before importing anything.

Do not trust archive names, status posters or `1110` counts as proof of implementation.

## 20. Current verified/observed build status

### VERIFIED / real foundation

- Native Android/Kotlin production repository exists.
- PR #15 Rive/UI foundation exists and latest checked head `5c8ae5bb62aece0963d5fcf01db94f84f3498e59` has successful Android CI runs.
- Rive ABI/runtime adapter/controller/fallback architecture exists.
- Fail-closed Owner authorization contracts exist.
- Live Supabase project exists with RLS-enabled product/Studio tables.
- 48 Studio preset rows currently exist in Supabase.
- Auth/account UI RED test was correctly observed on PR #25 before production implementation.
- Replit has been audited and classified as UX/reference only.

### PARTIAL

- Auth/session/account UI.
- profiles/settings/capabilities.
- Remember Me durable memory wiring.
- Owner server provider/grants.
- THyNK editor/timeline/project contracts.
- storage/sync/retention automation.
- Social and DMs.
- calendar/scheduling/publishing.
- Patsy AI/search provider gateway.
- safety/moderation/under-16 end-to-end enforcement.
- PawMoji Android IME.

### BLOCKED / NOT CONFIGURED

- genuine production `patsy_assistant.riv`;
- physical-device Rive acceptance;
- exact verified `R.drawable.patsy_master_paw` drawable;
- verified original font binaries;
- complete 100-image/50-video editable production asset programme;
- original music creator/library completion;
- real end-to-end AI image provider flow;
- real exact-10-second AI video provider flow;
- Owner capability backend table/provider if that is the chosen server implementation;
- Play Store release/signing/compliance gate.

## 21. Code/assets to keep sending into ChatGPT

Highest-value incoming work, in order:

1. **Exact master paw asset** — the approved paw extracted from the Patsy wordmark in an app-ready transparent/vector form.
2. **Genuine Rive source/export** — `.riv` or editable rig source using the existing ABI; no second contract.
3. **Rive behavior/rig authoring work** — bones, meshes, independent ears/tail, face/eyes/lids, point IK, state layers, visemes and transitions mapped to the existing properties.
4. **Auth/provider implementation** — code that implements the existing `AuthGateway`, not a new parallel auth architecture.
5. **Owner provider/server work** — implementation behind `OwnerAuthorizationService`; capability-specific, fail closed; no local Owner bool.
6. **THyNK RED fixes** — deterministic forward/backward commands, richer layer state, artifact separation and catalogue validator.
7. **Shared renderer/export** — target existing Studio models; no duplicate `StudioProject`/`StudioLayer` tree.
8. **Real media pipeline** — decode/resolve assets, safe image export, thumbnails, Media3/MediaCodec video, audio timeline.
9. **Room/local project persistence** — only with migration/round-trip/corruption/user-isolation tests and no duplicate authoritative cloud model.
10. **CameraX implementation** — native camera behind the current fail-closed camera contract.
11. **PawMoji IME** — real Android input method using approved assets.
12. **Font binaries/evidence** — real files and metadata, not just names/descriptions.
13. **DM/Profile/Owner screens** — Compose code matching the locked dark reference screens and using real capability state.
14. **Actual ZIP archives** — send the binary ZIP, not an `.html` download-placeholder page.

When code arrives, audit it for:

- package compatibility (`com.patsy.app` production tree);
- duplicate model/contracts;
- truthfulness of capability states;
- security/RLS implications;
- build dependencies;
- tests;
- locked visual compliance;
- asset/licence/provenance;
- whether it belongs in Android production, Supabase/backend, or reference-only material.

## 22. Implementation order from here

1. Finish PR #25 auth/account RED → GREEN.
2. Add/verify exact master paw asset, then implement account dropdown and Remember Me micro-interaction with reduced motion.
3. Keep Owner architecture fail closed; design/apply server capability backing only after security tests are specified.
4. Fix PR #24's four RED THyNK requirements and obtain GREEN CI.
5. Add Studio renderer and thumbnail pipeline using the existing project/layer contracts.
6. Add truthful PNG/JPEG image export.
7. Advance real video/audio pipeline and timeline.
8. Complete real Supabase Auth/account bootstrap/age-domain state.
9. Complete durable Remember Me/projects/media sync/retention.
10. Implement DMs/Social with mutual under-16/16+ isolation, block/report and expiry.
11. Implement calendar/notifications/scheduling/publishing adapters.
12. Implement authenticated Patsy AI/search gateway.
13. Complete AI Image and exact-10-second AI Video job flows.
14. Populate and verify the 100 image + 50 video editable template programme and original audio library.
15. Build original music creator.
16. Finish Camera/Meme/Collage/Documents/Homework/Brand Kit workflows on the shared Studio engine.
17. Complete PawMoji Android IME.
18. Integrate genuine Rive asset once supplied and pass full contract/device acceptance.
19. Complete security, under-16, privacy, accessibility, reduced-motion, offline/recovery, performance and provenance audits.
20. Run full Android device/regression/security tests and then the signed release/Play Store gate.

## 23. Definition of done

A feature is done only when:

- the real production path exists;
- required authenticated identity/capabilities are enforced;
- RLS/server authorization is correct;
- no privileged secrets ship in the client;
- unavailable/failure states are truthful;
- user project/content survives recoverable failures safely;
- retention/deletion/locking rules are verified;
- cross-device behavior is verified where required;
- critical tests pass;
- new commit/head CI has been rechecked;
- approved visual/assets remain unchanged;
- external/provider success is confirmed before the UI reports success;
- relevant device/release gates pass.

**This is the finalized build vision/source of truth. It does not mean the entire app is already finished.**
