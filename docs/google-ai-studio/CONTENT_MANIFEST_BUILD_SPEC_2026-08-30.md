# THyNK Content Manifest — Build Specification and Truthfulness Gate

Date: 2026-08-30
Status: LOCKED execution rule for imported content manifests

Treat any supplied JSON catalog as a **CONTENT MANIFEST AND BUILD SPECIFICATION**, not as proof that assets already exist.

Do **not** claim that the 1,110 listed items are complete merely because folders, paths, names, IDs or counts exist.

## Required real asset/template contract

For every listed item that is claimed complete, create a real usable asset or template file with:

- a unique stable ID
- category and subcategory
- editable text and object layers where applicable
- `data-block`, `data-category`, `data-subcategory` and `data-editable` metadata
- canvas position, dimensions, rotation and z-index support
- a preview thumbnail
- original or properly licensed content
- no copied Canva, Snapchat, unDraw, Feather, Heroicons or trademarked assets

A manifest record, empty folder, filename, shell document, generated count, duplicated placeholder, or stub preview is **not** a completed asset.

## Validation report required

Every content-generation pass must output a validation report containing:

1. Expected item count
2. Actual files created
3. Valid files
4. Missing or placeholder files
5. Duplicate-content checks
6. Broken preview or metadata checks

Do not create hundreds of identical placeholder files and count them as finished content.

## Testable implementation stages

### Stage 1 — Structure, loader and validation

- project structure
- manifest loader
- schema validation
- stable-ID validation
- required-metadata checks
- real-file existence checks
- duplicate-content detection
- preview existence/validity checks
- implemented-vs-placeholder reporting

### Stage 2 — Core canvas editing

Implement and test:

- add
- select
- move
- resize
- rotate
- layer/z-index
- delete

### Stage 3 — Browsers

Implement usable browsers for:

- templates
- elements
- media
- styles

### Stage 4 — Project state

Implement:

- save project
- load project
- undo
- redo

### Stage 5 — Video timeline

Implement and test:

- timeline
- clip selection
- trimming
- playback

### Stage 6 — Camera and original effects

Implement camera support plus original filters/effects. Do not clone proprietary branded effects or assets.

### Stage 7 — Export

Implement reliable export using an Android-compatible encoding strategy. Do not claim export works until real output files are produced and verified.

### Stage 8 — Quality gate

Run:

- automated tests
- accessibility review
- performance review
- security review

## Locked Patsy / THyNK visual system

Preserve the approved system:

- charcoal backgrounds
- white buttons with black/charcoal text and soft rainbow glow
- white main text
- white icons with rainbow outlines
- exact approved Patsy and THyNK logos only
- do not redraw or approximate either logo
- do not use Canva assets or Canva-derived styling
- do not invent a completed Patsy animation or production `.riv` file

## First delivery gate

Before any claim of broad catalog completion, return all of the following:

1. The actual project file tree
2. All relevant `package.json` files where a Node/JS package actually exists; if the working project is not Node-based, state that truthfully instead of inventing one
3. Build and run instructions
4. A truthful implemented-versus-placeholder report
5. The first working editor slice containing **10 genuinely editable templates**
6. Automated tests for manifest counts and required metadata

## Definition of a genuinely editable template

Each of the first 10 templates must be materially distinct and must load in the editor with real editable structure. At minimum, where applicable, text and objects must be selectable and editable; object geometry must persist; layer order must persist; metadata must validate; and the preview must represent the actual template rather than a generic placeholder.

Ten copies of one structure with renamed IDs do not satisfy this gate.

## Completion language

Use evidence-based states only:

- `IMPLEMENTED_AND_VALIDATED`
- `IMPLEMENTED_NOT_YET_VALIDATED`
- `PARTIAL`
- `PLACEHOLDER`
- `MISSING`
- `NOT_CONFIGURED`
- `BLOCKED`

Do not describe planned features as operational.

## Source-of-truth rule

If imported historical files conflict with the current repository or a newer SAVE / LOCK IN requirement, preserve the newer approved requirement. Historical archives are reference/recovery material, not automatic authority.

## Handoff requirement

After each stage, return:

- exact files changed/created
- tests run and results
- actual asset/template counts by validation state
- known blockers
- anything that must be brought back to GitHub/Codex

Do not include API keys, tokens, private credentials, or unlicensed third-party assets in the handoff.
