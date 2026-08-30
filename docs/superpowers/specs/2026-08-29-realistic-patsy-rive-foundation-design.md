# Realistic Patsy Rive Foundation Design

## Decision
Preserve the existing Patsy 3.3.8 Android/Rive integration and evolve it incrementally. The existing `PatsyAssistant` / `PatsyAssistantMachine` / `PatsyAssistantVM` / `Default` ABI remains stable. The generated transparent fallback stays active whenever the production `.riv` file is absent, loading, invalid, or incompatible.

## Character boundary
Main Patsy is the realistic grey shaggy companion, rendered without a visible square, circle, halo, or mascot frame. Cartoon Patsy remains isolated to PawMojis, keyboard stickers, and reactions. Compose owns screen travel and safe placement; the Rive rig owns internal body motion, expression, gaze, ears, tail, pointing, blinking, and speech animation.

## Visual system
Principal pages use the approved centred white Patsy wordmark with the small tagline `A LEGACY LED BY PAWS`. The shell uses black/charcoal backgrounds, white/light text, white primary buttons with dark labels, and restrained rainbow/neon accents.

## Verification strategy
First lock the existing Rive coordinator and runtime fallback behavior with JVM unit tests. Then extract shared visual tokens/header. MainActivity migration follows after tests are green, so no broad visual rewrite is required to validate the foundation.

## External asset state
A finished production `patsy_assistant.riv` is not assumed. Until a validated export exists and passes the rig contract and device smoke tests, the app must report/use fallback behavior rather than pretending Rive authoring is complete.
