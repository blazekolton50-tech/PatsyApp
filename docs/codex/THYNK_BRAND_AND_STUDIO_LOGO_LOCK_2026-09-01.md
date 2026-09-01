# THyNK-IN! — BRAND + STUDIO LOGO LOCK

**SAVE MAIN APP / SAVE LOCK IN / CODEX BINDING UPDATE — 2026-09-01**

This file supersedes older app-name/header branding rules wherever they conflict with this lock.

## 1. APP BRAND

The app's locked brand name is now:

**THyNK-IN!**

The word **Patsy** is no longer the app name. Patsy is the in-app AI companion / pet pal only:

**Patsy the Pet Pal**

Do not use `Patsy` as the application title, global brand title, or default top-page brand mark after this migration.

Canonical owner-approved master asset stored in the persistent project Library:
- `THyNK-IN!/Brand Locks/2026-09-01/THyNK-IN_APP_BRAND_LOCKED.png`
- SHA-256: `41aff5e6335a17ae5394b0b5cf8366d7551519b8b4e361a35685b0e6c1a462b8`

The approved THyNK-IN! brand logo is the global app identity and should appear as the top brand on ordinary app pages unless a studio-specific rule below takes precedence.

## 2. THyNK IT. — DESIGN / GENERAL EDITING BRAND

The locked Design/general editing logo is:

**THyNK IT.**

Canonical owner-approved master asset stored in the persistent project Library:
- `THyNK-IN!/Brand Locks/2026-09-01/THyNK_IT_EDITING_LOCKED.png`
- SHA-256: `a8ef528f54ea60996eb960849d7a2883dfcef3611c610a8fd4eb32f8377d2700`

Use this logo:
- on the THyNK Design / general Editing landing area;
- at the top of all non-Music editing/design pages;
- as the THyNK Design/general Editing identity in the bottom homebar where that destination/logo treatment is shown.

Do not use THyNK IT. as the top logo on pages covered by the THyNK Music rule below.

## 3. THyNK MUSIC — MUSIC + VIDEO BRAND

The locked Music/video logo is:

**THyNK Music**

Canonical owner-approved master asset stored in the persistent project Library:
- `THyNK-IN!/Brand Locks/2026-09-01/THyNK_MUSIC_LOCKED.png`
- SHA-256: `1061bbbcfeda85baebb795fd2f720b3afadf291868ae31d158ea8819f114ce00`

THyNK Music owns the entire music/video workspace, including:
- music home and discovery;
- music player;
- video player;
- music editing;
- video editing;
- combined music/video editing flows;
- music creation, multitrack, mixer, EQ, effects, lyrics/vocals, mastering and export routes where those capabilities genuinely exist.

**Every page in this Music/video family must show the approved THyNK Music logo at the top.**

This is a deliberate override of older rules that excluded player/editor pages from the Music logo.

## 4. TWO-STUDIO SWAP

The existing design/editing and Music/video workspaces remain separate visual spaces while sharing the locked native app shell.

Add a clear workspace switch:
- on THyNK IT. editing pages: **Swap to Music**;
- on THyNK Music pages: **Swap to IT**.

The swap must route between the two existing native workspace families; it must not create a second React/WebView/editor runtime and must not discard the current project state. Preserve/autosave in-progress work before or as the user switches according to the existing THyNK persistence rules.

## 5. LOGO ROUTING PRECEDENCE

Use this order for top-page branding:

1. Music/video player/editor/music routes -> **THyNK Music**.
2. Other design/editing routes -> **THyNK IT.**.
3. Ordinary non-editor app pages -> **THyNK-IN!**.

Patsy remains the AI character and may appear/animate according to the Patsy companion contract, but her name/logo must not replace the THyNK-IN! application identity.

## 6. DO NOT REDESIGN THE ASSETS

The three supplied owner-approved raster logos are binding visual assets.

Do not:
- regenerate them from text;
- substitute a generic font;
- change the distinctive `y`;
- recolour or rebuild the rainbow treatment;
- reinterpret THyNK IT. or THyNK Music as plain text when the approved image asset is available;
- silently fall back to an older Patsy app wordmark.

Android implementation may create density-appropriate derivatives/crops from the locked masters, but must preserve the approved visible artwork.

## 7. CODEX NEXT-PASS REQUIREMENT

Before the next production code slice, Codex must read this file as a binding owner lock and reconcile any older `Patsy` app-brand/header references against it.

Do not weaken navigation/auth/Owner/age/RLS gates while applying the rebrand. Keep PR #41 Draft and unmerged until owner approval and physical-device QA.
