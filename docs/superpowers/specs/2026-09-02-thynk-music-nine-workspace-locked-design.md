# THyNK Music — Locked Nine-Workspace Production Design

Date: 2026-09-02  
Status: **OWNER LOCKED**  
Product: **THyNK-IN**  
Module: **THyNK Music**

## 1. Purpose

This document is the binding design contract for the THyNK Music editor inside THyNK-IN. It consolidates the strongest useful behaviour from the supplied React/Tailwind/Web Audio donors, the THyNK Music visual references, and the persistence/history lessons from the THyNK-IT Fashion v6 work, while preserving the current native Android production boundary.

Production THyNK Music remains native Kotlin + Jetpack Compose. React/Tailwind/Next.js/Vite assets in this repository are donor/reference material only unless a later owner-approved architecture change explicitly says otherwise.

## 2. Locked visual authority

The nine owner-supplied reference screens are the visual authority for THyNK Music. The production pages must look and feel like these references rather than being redesigned into a generic Android form, desktop DAW, or simplified card list.

Shared visual language:
- near-black professional studio base;
- continuous neon rainbow perimeter/rim lighting;
- white THyNK Music branding;
- metallic/hardware-style knobs, faders, meters and deck controls;
- compact high-density mobile production layout;
- track identity colours remain consistent across pages: vocals purple, drums orange, bass green, synth cyan/blue;
- additional tracks receive stable distinct colours;
- waveform/timeline content remains visually dominant;
- neon colour is used for state, meters, focus and controlled glow rather than random decoration.

The repository reference image is:
`donors/thynk-music-web/reference/locked-9-pages/thynk-music-locked-nine-page-reference.webp`

## 3. Exactly nine production workspaces

There are exactly nine THyNK Music production pages/workspaces:

1. **Mixer / Master Console**
2. **Effects Rack**
3. **Equaliser**
4. **Vocal Studio**
5. **DJ Studio**
6. **Beats Sampler**
7. **Piano Roll / MIDI**
8. **AI Tools Suite**
9. **Mastering & Export**

The Arrangement/Timeline is **not** a tenth page. It is the shared song layer and must remain accessible from all nine workspaces, either fully visible or through an immediate expand/collapse treatment that never creates a second project state.

Create, Open Project, Import, Record and similar launch actions are entry actions into THyNK Music and are not counted as extra editor pages.

## 4. Shared Arrangement / Timeline contract

All nine workspaces operate on the same arrangement and project state.

The arrangement supports an unlimited-track-capable architecture even if the first native slice renders a smaller set. At minimum it must model:
- stable track IDs;
- track name/type/colour;
- clip IDs and media references;
- clip start/end/duration;
- movable clips;
- selection;
- playhead position;
- BPM and time signature;
- key/scale where applicable;
- mute/solo;
- per-track settings access;
- waveform or genuine audio-derived preview when source audio exists.

Do not generate fake waveforms from arbitrary decorative data and present them as audio analysis.

## 5. Page contracts

### 5.1 Mixer / Master Console

Visual target: the locked mixer image.

Required direction:
- per-track channel strips;
- gain/volume fader;
- stereo pan;
- mute and solo;
- level meter from genuine signal data when audio is live;
- quick EQ controls;
- master gain/output;
- crossfader where the workflow requires it;
- transport/BPM area consistent with the locked reference;
- arrangement accessible above.

Architecture must not hard-code the engine to only four tracks.

### 5.2 Effects Rack

Visual target: the locked hardware-rack image.

Target effects include reverb, echo, delay, flanger, phaser, chorus, distortion, filter and bit crusher, with an XY performance control and master meters/spectrum when supported.

Each effect has explicit enabled/bypassed state and parameters. A control must not claim to process audio unless its underlying processor is connected.

### 5.3 Equaliser

Visual target: the locked Equaliser image.

Required direction:
- spectrum analyser from genuine analysis data;
- multi-band EQ editing;
- frequency, gain and Q/width controls;
- HPF/LPF;
- presets that write real EQ state;
- per-track target selection;
- reset/save-preset behaviour when persistence is implemented.

### 5.4 Vocal Studio

Visual target: the locked Vocal Autotune/Harmonies image.

Required direction:
- microphone recording;
- input level meter;
- pitch detection when implemented by a real detector;
- key/scale;
- pitch correction controls;
- harmony intervals;
- doubles/vibrato controls where supported;
- take lanes;
- comp selection;
- comp-to-track;
- stem export only when a genuine renderer exists.

A synthetic demo tone is not a microphone recording and must never be labelled as one in production.

### 5.5 DJ Studio

Visual target: the locked dual-deck image.

Required direction:
- Deck A / Deck B;
- genuine media assignment;
- cue/play/sync/loop controls;
- pitch/tempo;
- key lock where supported;
- slip mode where supported;
- per-deck waveform;
- per-channel gain/EQ/fader;
- crossfader;
- headphone cue;
- master meters;
- shared project BPM/transport rules.

### 5.6 Beats Sampler

Visual target: the locked 16-pad image.

Required direction:
- 16 responsive pads;
- assignable samples;
- loop length controls;
- quantise;
- swing;
- roll;
- pitch/tempo;
- slice/reverse/trim where genuine sample editing exists;
- velocity/filter/pitch/attack/decay style shaping;
- pad actions can create clips/sequences in the shared arrangement.

### 5.7 Piano Roll / MIDI

Visual target: the locked piano-roll image.

Required direction:
- piano keyboard/grid;
- note creation/move/resize/delete;
- scale/key awareness;
- octave controls;
- quantise and snap;
- record state;
- selection/draw/erase tools;
- velocity editor;
- instrument parameters such as cutoff, resonance, attack and release where connected;
- track volume/pan/send;
- notes write to the same MusicProject used by the arrangement.

### 5.8 AI Tools Suite

Visual target: the locked AI Tools Suite image.

The visual contract includes six tool areas:
- AI Beat Maker;
- AI Stem Separator;
- AI Mastering;
- AI Lyrics;
- AI Chord Progression;
- AI Vocal Generator.

These interfaces may exist before every provider is wired, but the result state must be truthful. No endpoint may fabricate provider success, stem separation, mastering, lyrics, chords or vocals and present it as a real completed operation. Unavailable/provider-pending states must be explicit.

### 5.9 Mastering & Export

Visual target: the locked Mastering/Export image.

Required direction:
- master level control;
- limiter;
- compressor;
- stereo imaging;
- loudness history when genuine measurements exist;
- LUFS;
- peak/true-peak where a genuine measurement path exists;
- spectrum;
- export format/quality controls;
- stems option;
- project save;
- share handoff;
- real rendered export only when the native/audio render pipeline completes successfully.

## 6. One authoritative MusicProject

All nine pages are views over one project, not separate editors.

The authoritative project state must be able to represent:
- project ID, owner/account scope, title;
- BPM, time signature, key/scale;
- playhead and loop/marker state;
- tracks and clips;
- audio/media asset references;
- mixer gain/pan/mute/solo;
- EQ;
- effects and routing;
- automation;
- sampler pad assignments and sequences;
- piano-roll notes and velocity;
- vocal recordings, takes and comp selections;
- lyrics/chord metadata where used;
- DJ cue/loop/project-relevant state;
- mastering state;
- export preferences;
- history/recovery metadata.

Switching workspaces must never duplicate the song or create another copy of audio state.

## 7. Persistence, autosave and recovery

The THyNK-IN host remains authoritative for account-scoped production persistence and cross-editor routing.

Rules:
- meaningful project edits queue an autosave using a short debounce;
- a local recovery snapshot may protect against process death/offline interruption;
- local recovery must not become a competing production database;
- leaving THyNK Music must flush or safely queue pending state before navigation;
- reopening the project restores the exact project identity and latest confirmed state;
- save UI must distinguish saved, saving, offline/recovery-only and failed states;
- never display a false saved-success state.

The donor Prisma/SQLite database remains reference-only and must not replace production Supabase/account-scoped persistence.

## 8. Undo / redo and nondestructive editing

Use a bounded history model with at least the existing 40-step behaviour as the initial target for editor operations.

Large audio bytes must not be duplicated on every undo snapshot. History stores project-state operations/references where practical.

Audio editing is nondestructive by default:
- source media has stable asset IDs;
- split/trim/fade/gain/pitch/time operations store edit state and references;
- irreversible rendering is explicit and produces a new asset/version where appropriate.

## 9. Real-vs-demo truth boundary

The following must never be faked in production:
- microphone recording;
- decoded waveforms represented as genuine audio data;
- input/output meters;
- stem separation;
- pitch correction/autotune;
- AI vocal generation;
- AI mastering;
- LUFS/true-peak measurements;
- rendered WAV/MP3/stem exports;
- provider/cloud save success;
- account authorization.

A visual control can be present before its engine is connected, but its state must say unavailable, demo/reference, or pending instead of returning a fabricated success.

## 10. Donor material to preserve

Useful behaviour to mine from the existing donor/reference material includes:
- draggable clips and shared playhead;
- Web Audio GainNode and BiquadFilter examples;
- mixer controls;
- working knobs and faders;
- procedural beat-pad demo interactions;
- DJ/deck layout concepts;
- vocal/harmony interaction concepts;
- effect-control grouping;
- Prisma project-shape ideas;
- THyNK-IT Fashion v6 history/autosave/restore lessons.

Donor browser persistence and mock APIs are not production authority.

## 11. THyNK-IN host boundary

THyNK-IN continues to own:
- authentication/account identity;
- global app navigation;
- THyNK Panel routing;
- native Camera handoff;
- app-level storage/project ownership;
- cross-editor autosave/restore routing;
- platform permissions and secure provider credentials.

THyNK Music must integrate with those host services rather than duplicating them.

## 12. Acceptance criteria for the design lock

The design is considered preserved only if:
- there are exactly nine THyNK Music production workspaces listed in Section 3;
- the shared Arrangement/Timeline is not counted as a tenth page;
- all nine pages visibly follow the supplied reference screens;
- the same MusicProject survives navigation across all nine pages;
- the arrangement remains immediately accessible;
- track colours remain consistent;
- native Android remains the production runtime;
- mock browser APIs are not promoted as real production services;
- save/export/AI/audio-analysis success is truthful;
- no implementation removes existing genuine audio functionality merely to match the visuals.

## 13. Reference source mapping

The GitHub visual reference is a presentation copy assembled from the nine owner-supplied source images. The original source hashes are recorded in the adjacent `LOCKED_9_PAGE_VISUAL_REFERENCE.md` manifest so future work can detect accidental replacement/drift.
