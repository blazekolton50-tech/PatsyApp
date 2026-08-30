# API Provider Configuration Contract

This file records provider integration choices without wiring or changing the current UI.

## Client-safe

```text
VITE_SUPABASE_URL=
VITE_SUPABASE_PUBLISHABLE_KEY=
VITE_PATSY_API_BASE_URL=
VITE_ENABLE_DEV_BYPASS=false
```

## Server-only / Edge Function secrets

```text
SUPABASE_URL=
SUPABASE_SERVICE_ROLE_KEY=
GEMINI_API_KEY=
PATSY_TEXT_MODEL=
PATSY_IMAGE_MODEL=
PATSY_VIDEO_MODEL=
SMTP_HOST=
SMTP_USER=
SMTP_PASSWORD=
PUBLISHING_PROVIDER=
BUFFER_ACCESS_TOKEN=
```

## Rules

- Never commit real secrets.
- Never place `GEMINI_API_KEY`, service role, SMTP passwords or social tokens in `VITE_*` variables.
- Model IDs are server configuration, not screen constants.
- Provider failures return explicit unavailable/error states; never substitute fake success.
- API providers are adapters. Switching providers must not require redesigning THyNK Studio, Chat, Schedule, Login or other screens.

## Google AI Studio / Gemini

Current preferred JS SDK as of 2026-08-30: `@google/genai`.

Use an authenticated backend/Edge Function for production calls. Google AI Studio can be used to provision/test a Gemini API key, but the key must not be embedded in the shipped web/mobile client.

Current Google guidance should be rechecked immediately before implementation. As of this snapshot, image generation is moving to Gemini/Nano Banana-family models and video generation to current Veo 3.1-family endpoints; older Imagen 4 and older Veo IDs have deprecations/shutdowns.
