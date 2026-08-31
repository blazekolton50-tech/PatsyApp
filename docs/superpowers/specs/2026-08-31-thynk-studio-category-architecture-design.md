# THyNK Studio category architecture design

Date: 2026-08-31  
Status: Approved for implementation  
Base: `chatgpt/phone-preview-b-2026-08-30` at `11f19e9f4c343ec61344a4ee22a64a77593b1490`

## Goal

Add the complete THyNK Studio home and category hierarchy without redesigning the locked Patsy shell, changing the five primary navigation destinations, replacing the existing Studio/editor foundation, or pretending unfinished providers are live.

The required flow is:

`THyNK Home → Main Category → Subcategory → Blank or Template/Starter → Existing editor/provider/library destination`

## Fixed product constraints

- Keep the Preview C primary navigation exactly as it exists on the base: HOME, THyNK, the large Camera plus, PATSY DMs, PROFILE.
- Keep the approved dark charcoal/black Patsy and THyNK shell, white controls with restrained rainbow treatment, and the exact saved brand assets.
- Keep the professional Design Space: dark shell, light-neutral workspace, white editable canvas.
- Reuse the existing Studio modes, state reducer, Media3 player/timeline, and tool catalogue from the existing THyNK Studio work.
- AI Video duration is a single contract constant of exactly 10,000 milliseconds.
- Do not fabricate AI, storage, community, camera, music, publishing, or Rive provider success.
- Reserve contextual Patsy help targets, but do not invent a production `.riv` asset or simulate continuous animation with GIFs, sprites, or pose swaps.
- Do not merge to `main` without explicit owner instruction.

## Catalog model

Add a pure Kotlin catalog with stable identifiers and display labels:

- `ThynkCategory`: id, label, subcategories, audience policy.
- `ThynkSubcategory`: id, label, terminal type, optional canvas preset.
- `ThynkTerminal`: editor mode, provider action, or Studio library destination.
- `ThynkStarterKind`: blank or template/starter.
- `ThynkLaunchRequest`: category, subcategory, starter kind, editor mode/destination, and canvas preset.
- `ThynkAudience`: standard or under-16 protected.

Display labels remain separate from stable route ids so wording can be tested without using display copy as navigation state.

## Exact category catalog

1. **Design & Templates** — Posters, Flyers, Invitations, Cards, Menus, Price Lists, Signs, Certificates, Brochures, Labels, Blank Designs, Custom Size, Templates.
2. **Social & Content** — Square Post, Portrait/Story, Carousel, Quote, Meme, Announcement, Before/After, Creator Tip, Stat Card, Infographic, Thumbnail, Banner, Promo Graphic.
3. **Photo & Image** — Edit Photo, Crop, Resize, Rotate, Flip, Cutout, Remove Background, Remove Object, Restore, Blur, Filters, Adjustments, Frames, Overlays, Images, Graphics, Backgrounds, Shapes, Patterns, Textures.
4. **Video & Camera** — Blank Video, Portrait, Landscape, Square, Short Video, Slideshow, Promo, Tutorial, Memories, Photo-to-Video, Clip Editor, Camera Studio.
5. **Documents & Business** — CVs, Cover Letters, Letters, Letterheads, Reports, Guides, Notes, Checklists, Forms, Schedules, Rotas, Project Plans, Business Cards, Brand Boards, Service Guides, Product Sheets, Training Guides.
6. **Homework & Study** — Homework Planner, Revision Timetable, Assignment Tracker, Worksheets, School Projects, Flashcards, Notes, Study Guides, Reading Logs, Timetables, School Presentations, Blank Homework Page.
7. **Presentations & Planning** — School Presentation, Business Presentation, Training, Portfolio, Educational, Photo Presentation, Cleaning Schedule, Meal Planner, Shopping List, Chore Chart, Weekly Planner, Monthly Planner, Budget, Pet Planner, Event Planner, Calendars.
8. **Collage & Creative** — Photo Grid, Freeform Collage, Memory Page, Mood Board, Scrapbook, Before/After, Storyboard, Frames, Stickers, Illustrations, Shapes, Borders, Patterns, Textures, Doodles.
9. **Music & Audio** — Original Music, Sound Effects, Audio Editing, Voice Recording, Background Music, Video Soundtrack.
10. **AI & My Studio** — AI Image, AI Video, Patsy Assistant, My Projects, Continue Designing, Recent Projects, Favourites, Personal Templates, Community Templates, Brand Kit, Inspiration, Saved Assets, Folders.

## Navigation and state

THyNK remains one verified shell destination. Inside it, use a nested, saveable Compose state machine with typed destinations:

- `Home`
- `Category(categoryId)`
- `Subcategory(categoryId, subcategoryId)`
- `Starter(categoryId, subcategoryId)`
- `Editor(launchRequest)`
- `Provider(providerAction)`
- `Library(libraryDestination)`

Route ids are deterministic:

- `thynk`
- `thynk/category/{categoryId}`
- `thynk/category/{categoryId}/subcategory/{subcategoryId}`
- `thynk/category/{categoryId}/subcategory/{subcategoryId}/starter`
- `thynk/editor/{mode}`
- `thynk/provider/{providerId}`
- `thynk/library/{libraryId}`

Back navigation returns one level at a time. Invalid or disallowed ids resolve to the THyNK safe home rather than opening a privileged or provider route.

This nested state avoids expanding the application-wide `Screen` enum with every catalog item while preserving the existing account bootstrap and shell gate.

## Existing editor integration

Bring the reusable production Studio core from draft PR #23 onto the green Preview C base:

- `StudioEditorState`
- `StudioMode`
- `StudioAction` and reducer
- `StudioToolCatalog`
- `StudioVideoPlayer`
- the shared editor workspace

Use existing modes wherever possible: IMAGE, VIDEO, DOCUMENT, MEME, COLLAGE, CAMERA. Category launches map into these modes using explicit presets. Items that are not editors use a truthful provider or library terminal.

The unfinished single-clip RED test from PR #23 is not part of this category slice because its production media-state contract does not yet exist. Its omission is documented rather than hidden; the category work receives its own routing tests.

Shared tools remain available by mode: Undo, Redo, Select, Text, Media, Elements, Stickers, Draw, Crop, Resize, Rotate, Flip, Frames, Rulers/Guides, Layers, Position, Opacity, Filters, Adjust, Effects, Animate, Trim, Split, Speed, Transitions, Captions, Audio, Original Music, Save, and Export.

## Under-16 policy

The current verified `AccountBootstrap.ageState` remains authoritative.

- Standard users see all ten categories subject to existing provider restrictions.
- `UNDER_16_PROTECTED` users see only Homework & Study.
- Unknown/unverified protected accounts do not gain Studio access.
- Under-16 direct navigation to another category, provider action, community templates, social content, AI generation, or unrestricted library destination is rejected by the catalog policy.
- Homework launches only document/presentation-safe editor surfaces and excludes provider actions, community content, publishing, DMs, and unrestricted search.
- The shell route for THyNK may open for verified under-16 accounts, but the nested Studio policy enforces the safe catalog. Other existing shell restrictions stay fail-closed.

## Responsive UI

- THyNK Home presents ten clear tappable category cards for standard users and the single safe Homework & Study card for protected under-16 users.
- Category, subcategory, and starter screens use lazy responsive layouts and preserve readable touch targets on narrow phones.
- The editor outer chrome remains dark. Its active workspace is light neutral and its editable canvas is white.
- No category card redraws the THyNK logo or introduces new brand assets.
- Empty/provider-dependent terminals show honest availability states, never synthetic results.

## Patsy contextual-help boundary

Add stable help targets for:

- category grid
- subcategory list
- starter choice
- editor canvas
- editor tool rail
- Save
- Export

Expose the active target through a callback/slot compatible with the existing Patsy companion boundary. This slice reserves look/point destinations but does not claim or fake production Rive movement.

## Tests

Add JVM unit tests for:

- exact ten-category order and labels
- every exact subcategory list
- unique stable ids
- route construction and parsing
- Home → Category → Subcategory → Starter → terminal resolution
- representative mode and canvas mappings
- provider/library terminal mapping
- AI Video exactly 10,000 ms
- under-16 visible catalog and direct-route denial
- unknown route fail-closed behavior
- shared tool availability by Studio mode
- regression of the five locked primary navigation destinations

Verification command:

`./gradlew --no-daemon testDebugUnitTest assembleDebug --stacktrace`

GitHub Actions must run on the exact branch head. A successful older run is not carried forward.

## Delivery

Create a draft pull request against `main`; do not mark it ready or merge it. Report files changed, routes added, working behavior, provider-dependent placeholders, tests, and exact CI run.

Upload the generated debug APK as a workflow artifact. The APK will be supplied as a tap-to-download file. Android requires the owner to approve installation from the browser or file manager; this environment cannot remotely confirm that device security prompt.
