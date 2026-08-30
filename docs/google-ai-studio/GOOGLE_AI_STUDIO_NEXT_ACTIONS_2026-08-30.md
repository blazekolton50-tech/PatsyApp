# Google AI Studio — Patsy App Next Actions

Date: 2026-08-30

Use this only as a testing/prototyping workstream. Google AI Studio must not silently redesign locked Patsy/THyNK visuals, claim live integrations that are not configured, expose API keys, or replace the provider-neutral app architecture.

## Source of truth

Before making changes, use the latest project handoffs and current GitHub architecture as authority:

- `CODEX_START_HERE.md`
- `BUILD_STATUS.md`
- `docs/codex/PATSY_MEGA_MASTER_HANDOFF_2026-08-30.md`
- `docs/codex/PATSY_ARCHIVE_COMPARE_2026-08-30.md`
- latest locked THyNK/Rive/security/backend specifications

If an older imported ZIP conflicts with current GitHub or newer locked requirements, keep the current/newer requirement.

## ACTION 1 — Full imported-project audit

Copy/paste:

> You now have imported Patsy App / THyNK project material from multiple generations. Do not rewrite from scratch. Audit everything available and return four sections: IMPLEMENTED, PARTIAL, MISSING, CONFLICTING. Check: app shell/navigation, locked visual system, Patsy companion, Rive integration, authentication, owner authorization, age gating/under-16, DMs, feed/social, storage/retention, scheduling, THyNK Creation Studio, PawMoji keyboard, AI/search, image generation, video generation, original music, documents/homework, export/share/email/reload, and provider configuration. Mark unavailable services NOT_CONFIGURED. Do not invent completion.

## ACTION 2 — THyNK Creation Studio functional prototype

Copy/paste:

> Build or refine a testable THyNK Creation Studio prototype that follows the locked Patsy visual system and does not alter approved branding. Treat Studio as a real editable canvas rather than one generator page. Include: layers, add/remove objects, crop, resize, rotate, mirror/flip, frames, rulers/guides, drawing, eraser, text, collage layouts, undo/redo/reset, filters/adjustments, project continuation, preview, export/save, import/reload and Ask Patsy while creating. Add clear placeholder states for AI image and 10-second video providers when not configured. Do not expose API keys. Do not use Canva-owned templates/assets.

## ACTION 3 — Template system groundwork

Copy/paste:

> Create the data model and UI structure for a library target of 100 editable image templates and 50 editable video templates. Do not pretend all 150 are finished unless they truly are. Build categories, preview cards, editable object/layer metadata, aspect ratios, duration metadata for video, search/filter/favourites and a clean way to add more templates later. Include separate document/homework template categories for protected under-16 accounts where policy permits.

## ACTION 4 — Patsy companion behaviour test

Copy/paste:

> Test the Patsy companion integration without faking the production Rive asset. Patsy should be treated as one persistent AI companion that can blink, move eyes, tilt her head, point, react, change expression, listen, think and talk. Preserve the existing Rive host/ABI/fallback boundary. If there is no valid production .riv file, state that clearly and use the current fallback only. Do not create GIF/sprite pose switching and do not box Patsy permanently in a circle/card.

## ACTION 5 — Secure provider-neutral AI adapter design

Copy/paste:

> Design a provider-neutral server-side adapter for text/chat, image generation and 10-second video generation. The mobile app must never contain provider secrets. Define request/response contracts, NOT_CONFIGURED/LOADING/SUCCESS/DENIED/FAILURE states, request IDs, age-policy checks, rate-limit handling, content-safety handling and error messages. Keep Google/Gemini as one possible provider, not a hard dependency. Do not paste or request real secret keys in generated source.

## ACTION 6 — Under-16 security test pass

Copy/paste:

> Review the under-16 experience as a separate protected account tier. Verify that restrictions are enforced by backend/security rules as well as UI. Check: no adult social linking, restricted discovery/messaging, protected contact model, view-once/delete media where specified, homework/documents/colouring/games/brain-training, uncertain age defaults to safer protections, and no route/UI bypass that unlocks 16+ features. Produce a test matrix with PASS / FAIL / NOT_IMPLEMENTED and specific fixes.

## ACTION 7 — Storage and retention simulator

Copy/paste:

> Build/test the media-retention UX and service rules. Unlocked Patsy media should target auto-delete after 3 months, DMs should target 3-day deletion, and locked-profile targets are 100 photos plus 30 videos. Add expiry warnings and actions: Save to Device, Email Me a Copy, Lock in Patsy, Share, Import/Reload. Emailing a copy must not automatically lock the item. Use simulated timestamps/data if backend retention jobs are not configured and label simulation honestly.

## ACTION 8 — PawMoji keyboard audit

Copy/paste:

> Audit the PawMoji/custom keyboard material separately from the main Patsy companion. Determine what native Android IME code, manifests, services, permissions, assets and tests actually exist. Target a black keyboard with rainbow letters, normal typing, normal emoji access, PawMoji picker, recents and favourites. PawMojis are image/sticker assets, not fake Unicode codepoints. Return IMPLEMENTED / PARTIAL / MISSING and do not claim iOS support is complete unless a real iOS extension exists.

## ACTION 9 — Truthfulness + regression test

Copy/paste:

> Run a regression review across the imported project. Find any UI text, mocks or code that falsely says a service is working when it is not configured. Check AI generation, email, posting, Rive, auth, owner/admin, storage, DMs and scheduling. Replace fake success with truthful states without changing locked design. Also flag any historical file that could overwrite newer architecture if copied blindly.

## ACTION 10 — Return a clean handoff

Copy/paste:

> After completing the above work, return: (1) exact files changed, (2) what is now actually working, (3) what remains NOT_CONFIGURED, (4) any tests run and their results, (5) any code/files I should bring back to ChatGPT/Codex, and (6) a ZIP/export of only the changed/current files if the environment allows it. Do not include API keys, secrets or unrelated historical duplicates.

## Locked reminders

- Main Patsy = realistic grey shaggy companion; PawMoji art is separate.
- Exact tagline: `A LEGACY LED BY PAWS`.
- Dark/charcoal UI, white/light text, white main buttons with restrained rainbow/neon treatment.
- Do not redraw exact approved logo/paw/y assets.
- Do not use Canva-owned templates/assets.
- Do not claim production `.riv` exists until verified.
- Do not claim providers are live until verified.
- Do not hardwire Google/Gemini into the architecture.
