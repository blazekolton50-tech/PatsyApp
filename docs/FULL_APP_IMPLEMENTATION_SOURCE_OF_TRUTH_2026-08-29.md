# Patsy App — Full-App Implementation Source of Truth

Status: IMPLEMENTATION-READY PLAN  
Date: 29 August 2026

## Authority order
1. MASTER / APPROVED requirements and locked design packs.
2. Newer explicit approved decisions supersede older requirements.
3. Current source/build/backend evidence describes implementation status only and does not override MASTER.
4. Never claim a provider call, deployment, build, test, email, generation, publish, search or moderation action unless it actually completed.

## Locked product shell
Persistent authenticated navigation: **Home • Chat • Create • Social • More**. Schedule remains a Home/Create workflow and nested destination, not a primary tab.

Visual system: App Black `#050505`, raised surface `#101010`, secondary surface `#171717`, white/grey typography, restrained rainbow gradient/glow for active states, Creation emphasis and approved special controls. Official app logo is white Patsy logo option 4.

## Patsy companion
Production target is a fully rigged Rive character, not pose swapping. Runtime must support MINI, GUIDE, FULL, PEEK, REACT, REST and HIDDEN presentation modes; movement across usable screen space; keyboard/control avoidance; eye/head/ear/tail/mouth/paw/body control; pointing/waving/jumping/talking triggers; continuous transitions and no silent user-content changes. Current generated transparent fallback is development-only until a validated `patsy_assistant.riv` exists.

## Onboarding, authentication and recovery
Flow: Welcome → experience mode → display name/username → email → password → consent/verification → account setup → Home. Login supports username or email. Required: real case-insensitive username uniqueness; email verification state; password reset; session restore/revocation; useful offline/provider errors; secure credential handling; no plaintext secrets in logs/storage. Protected Mode is the fail-safe when age is unknown/unverified.

## Profiles and OWNER
OWNER is a server-authorized role, never a client boolean, username check or hidden-screen flag. Ordinary profile switching cannot grant privileged access. OWNER routes and controls must fail closed and should require recent authentication/MFA for high-impact actions in production. Audit all OWNER/moderation/security actions. Never embed privileged keys in the APK.

## Age/capability model
Server truth is a verified age tier plus capabilities. Under-16 and unknown/protected users receive safer defaults. Capability enforcement must cover Creation Studio, Social, DMs, social-account linking, media persistence, discovery, generation and search—not just UI visibility. Under-16 messaging/discovery must remain restricted to approved peer rules, with view-once media where required.

## Patsy AI/search
Patsy is the single conversational AI/search entry point. Required states: idle, thinking, searching, tool-running, generating, success, unavailable, offline, rate-limited and failed. Backend proxies hold provider secrets. Tool/result claims must be evidence-based. Consequential actions require explicit approval. Per-user memory must be isolated and permission-aware.

## Creation Studio
Major product surface: import, canvas/editor, image generation, 10-second video generation, crop/resize/rotate/position/scale, brightness/contrast/saturation/exposure, filters/effects, text/stickers/shapes/overlays, layers/order, undo/redo/reset, preview/export/save, templates and Patsy natural-language commands. Provider-neutral coordinators must return honest unavailable/denied/failure states.

## Patsy Social
Feed supports statuses, images, questions and templates; likes/comments; profile controls; reporting/blocking; media retention; connection/privacy rules. Public/community visibility must be implemented with RLS that excludes blocked/restricted/underage-incompatible interactions. Current private-by-default tables are a safe foundation but are not yet the finished Social feed.

## DMs
Authenticated thread/member/message model, message/attachment states, block/capability checks, under-16 restrictions, download rules and configurable retention. Default DM expiry is 3 days. View-once media must be enforced where capabilities require it. Message delivery must not be represented as successful before persistence/provider confirmation.

## Scheduling/calendar/reminders
Calendar items and scheduled content are separate but linked workflows. Store timezone explicitly; default client presentation uses the user's locale/timezone. Publishing connectors must clearly identify destination account/platform and require final approval. Reminders, pet dates and local notifications should work independently of social publishing where possible.

## Storage/retention
Device-first for generated/user media where practical. Server metadata tracks object ownership, expiry and locks. Feed media default retention: 90 days. DMs default: 3 days. Profile saved-media limit: 100 images and 30 videos. Expiry jobs must remove database references and queue external-object deletion safely. Backup is opt-in and encrypted.

## Notifications
In-app notifications plus push/email delivery queue. User-level notification preferences and device registration are authoritative. Failed delivery retries must be bounded and observable. Never expose push tokens publicly.

## Moderation/safety
Reports, moderation cases/actions, account restriction/suspension, age review, blocks and audit logs are server-side concerns. Moderation interfaces are privileged; regular users only see their own reports/status where appropriate. Content generation/search/chat require age-aware safety policy.

## Backend/API boundaries
Android should depend on interfaces/adapters rather than provider SDKs in UI code. Core boundaries: Auth, Profile/Capabilities, Patsy AI/Search, Media/Creation, Social, DMs, Scheduling, Notifications, Storage/Retention, Moderation/OWNER. Supabase publishable keys may be client-side; service-role/secret/provider keys must remain server-side.

## Offline/error handling
Every network feature must distinguish loading, offline, timeout, denied, unauthenticated, rate-limited, unavailable/provider-not-configured and server failure. Cached/local drafts must be clearly labeled. Retry must be idempotent for writes where possible.

## Accessibility
Minimum 48dp touch targets where practical; semantic labels/content descriptions; dynamic text support; logical TalkBack order; sufficient contrast; reduced-motion option; animation must not block input; do not encode meaning only by rainbow color.

## Android architecture target
Split the current app into feature modules/packages or clear feature boundaries: `core/ui`, `core/network`, `core/auth`, `core/model`, `feature/onboarding`, `feature/home`, `feature/chat`, `feature/create`, `feature/social`, `feature/messages`, `feature/schedule`, `feature/profile`, `feature/owner`, `feature/settings`, `patsy/rig`. Use ViewModels/state holders, repositories, provider interfaces and coroutine/Flow-based async state. MainActivity should become a shell rather than the product implementation container.

## Testing release gate
Required before production: clean assemble + release build, unit tests for validators/coordinators/capability rules, instrumentation/navigation tests, RLS tests with anon/authenticated/owner scenarios, age-tier matrix, offline/timeouts, auth recovery, direct-route OWNER attacks, block/DM/feed tests, retention jobs, low-storage behavior, process death/session restore, accessibility checks, target OUKITEL device and emulator smoke tests. No release claim while lint/tests or provider integrations remain unverified.

## Play Store/privacy launch gate
Privacy Policy and Terms URLs; account deletion in-app; Data Safety declaration; child/teen safety and target audience review; content reporting/blocking; notification permission rationale; photo/media permissions minimized; encryption-in-transit; deletion/export process; support contact; versioning/signing; release notes; staged rollout and crash monitoring.

## Current implementation snapshot
Android `3.3.8-patsy1` has a verified debug compile and earlier device launch evidence, a Rive runtime contract/fallback, secure auth/OWNER boundaries and provider-neutral coordinators. It is not production-complete because the final `.riv`, production auth/provider adapters, social publishing/generation/search services, full tests/lint after latest wiring and final release verification remain incomplete.

Supabase already contains substantial foundations for auth-linked profiles, settings, memories, projects/media, Social, DMs, creation jobs, calendar, notifications, connections/blocks, reports, saved media, consent, device registrations, scheduling, capability enforcement and private moderation/audit/processing queues. The remaining work is integration, hardening, Social visibility semantics, provider configuration, retention workers, OWNER operations and exhaustive verification.
