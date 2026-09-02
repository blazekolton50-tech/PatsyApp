# THyNK Music Web Donor

This directory preserves the owner-supplied THyNK Music React/Tailwind/Next.js studio prototype as a donor/reference package for the native THyNK-IN Android application.

## Important production boundary

- Production THyNK-IN remains Kotlin + Jetpack Compose.
- Do not embed this Next.js app as a production WebView/runtime.
- Do not replace Supabase/account-scoped production persistence with the SQLite/Prisma donor backend.
- The stems and export endpoints in this donor are mocks/placeholders and must never be represented as real separation, mastering, or export success.
- Use this package to mine layout, interaction, control grouping, timeline, mixer, DJ, effects, EQ, vocal, beat-pad, piano-roll, AI-tool and mastering UX into the native THyNK Music implementation.

## Donor stack

- Next.js 14.2.5
- React 18.3.1
- Tailwind CSS 3.4.4
- Prisma 6.2.x for SQLite Json-field compatibility
- Web Audio API demo tones

## Canonical THyNK neon palette

The Tailwind donor config now includes the owner-supplied studio tokens:

- `studioBgMain` `#030305`
- `studioBgSurface` `#07070c`
- `studioBgCard` `#0f0f16`
- `studioBgInput` `#141420`
- `studioBorder` `#1a1a28`
- `neonPink` `#ff007f`
- `neonPurple` `#7f00ff`
- `neonCyan` `#00f0ff`
- `neonGreen` `#10b981`
- `neonAmber` `#f59e0b`
- `neonRed` `#ef4444`

The existing `thynk.*` aliases remain for compatibility with the earlier donor UI.

## Additional owner references

`reference/THYNK_MUSIC_V6_UNIFIED_REACT_CORE_PARTIAL.jsx.txt` preserves the newer THyNK Music v6 React/Web Audio concept supplied in chat. It is intentionally stored as a non-runnable reference because the supplied paste ends mid-JSX and contains two incomplete declarations (`const freqMatrix =;` and `const notes =;`). Do not treat that file as buildable production source until the missing source is supplied or deliberately reconstructed and tested.

## Local donor run

From this directory:

```bash
npm install
npx prisma db push
npm run build
npm run dev
```

The package is intentionally isolated from production Android source.