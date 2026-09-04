# THyNK Music Web Donor

This directory preserves the owner-supplied THyNK Music React/Tailwind/Next.js studio prototype as a donor/reference package for the native THyNK-IN Android application.

## Important production boundary

- Production THyNK-IN remains Kotlin + Jetpack Compose.
- Do not embed this Next.js app as a production WebView/runtime.
- Do not replace Supabase/account-scoped production persistence with the SQLite/Prisma donor backend.
- The stems and export endpoints in this donor are mocks/placeholders and must never be represented as real separation, mastering, or export success.
- Use this package to mine layout, interaction, control grouping, timeline, mixer, DJ, effects, EQ, vocal, beat-pad, piano-roll, AI-tool and mastering UX into the native THyNK Music implementation.

## Locked production design — 2026-09-02

The owner has locked **exactly nine THyNK Music production workspaces**:

1. Mixer / Master Console
2. Effects Rack
3. Equaliser
4. Vocal Studio
5. DJ Studio
6. Beats Sampler
7. Piano Roll / MIDI
8. AI Tools Suite
9. Mastering & Export

The Arrangement/Timeline is shared across all nine workspaces and is **not** a tenth page. Create/Open/Import/Record are entry actions, not extra editor pages.

The nine supplied visual references are the design authority. Production should preserve the near-black console base, neon-rainbow perimeter lighting, hardware-style controls, dense professional mobile layout, waveform prominence and stable track colours (vocals purple, drums orange, bass green, synth cyan/blue).

Binding design spec:
`../../docs/superpowers/specs/2026-09-02-thynk-music-nine-workspace-locked-design.md`

Visual-source manifest:
`reference/locked-9-pages/LOCKED_9_PAGE_VISUAL_REFERENCE.md`

Repository contact sheet:
`reference/locked-9-pages/thynk-music-locked-nine-page-reference.webp`

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