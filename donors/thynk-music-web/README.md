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

## Local donor run

From this directory:

```bash
npm install
npx prisma db push
npm run build
npm run dev
```

The package is intentionally isolated from production Android source.