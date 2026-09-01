# THyNK-IN! — HOME BAR / NAVIGATION LOCK

**SAVE MAIN APP / SAVE LOCK IN / CODEX BINDING UPDATE — 2026-09-01**

This file supersedes older homebar/navigation semantics wherever they conflict with this lock.

## LOCKED BRAND / FEED DESTINATION

**THyNK-IN!** is both:
- the app name; and
- the social/news feed name.

The **centre button on the homebar is THyNK-IN!** and opens the THyNK-IN! social/news feed.

## LOCKED THyNK MUSIC POSITION

**THyNK Music** is positioned **immediately to the left of the centre THyNK-IN! button** on the homebar.

THyNK Music owns:
- the media player experience;
- music player;
- video player;
- music editing studio;
- video editing studio;
- combined music/video editing flows.

When the user taps **THyNK Music** on the homebar, show a choice popup with exactly the two primary destinations:
- **Player**
- **Editor**

`Player` routes into the THyNK Music media-player family. `Editor` routes into the THyNK Music music/video editing workspace.

All pages in this family use the approved **THyNK Music** logo according to `docs/codex/THYNK_BRAND_AND_STUDIO_LOGO_LOCK_2026-09-01.md`.

## LOCKED THyNK CHATS POSITION

**THyNK Chats** is positioned **immediately to the right of the centre THyNK-IN! button** on the homebar.

This lock currently defines its homebar name and position only. Do not invent or rename its internal chat behavior beyond the existing authenticated messaging contracts unless the owner explicitly defines a new behavior.

## LOCKED THyNK IT. POSITION

**THyNK IT.** is positioned on the **furthest right** of the homebar.

THyNK IT. remains the Design/general Editing workspace identity and uses the approved THyNK IT. logo according to `docs/codex/THYNK_BRAND_AND_STUDIO_LOGO_LOCK_2026-09-01.md`.

## CURRENT HOME BAR FACTS LOCKED SO FAR

From left to right, for the positions defined by the owner so far:
- Immediately left of centre: **THyNK Music** — tap opens **Player / Editor** popup.
- Centre: **THyNK-IN!** — app/social news feed destination.
- Immediately right of centre: **THyNK Chats**.
- Furthest right: **THyNK IT.** — Design/general Editing workspace.

The remaining furthest-left homebar destination is **not yet defined by this lock**. Do not invent, rename, reorder, or repurpose it until the owner defines it.

The former semantic centre Camera `+` rule is superseded by this owner lock. Preserve auth/age/Owner/RLS gates while changing navigation.

Keep PR #41 Draft and unmerged until explicit owner approval and physical-device QA.
