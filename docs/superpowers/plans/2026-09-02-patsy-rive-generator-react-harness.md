# Patsy Rive Generator + React Harness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add an isolated TypeScript Rive generator that exports a real `pet.riv` development fixture plus a full-screen React harness that can move the fixture toward a DOM element and point, while mirroring the existing Patsy V1 contract without changing production Android architecture.

**Architecture:** All new web/tooling code lives under `tools/patsy-rive-dev/`. `@stevysmith/rive-generator@0.1.1` is used only for documented artboard/vector/linear-animation/export APIs; it must not fake a state machine. A TypeScript mirror of the existing Kotlin V1 ABI prevents drift. The React harness uses `@rive-app/react-canvas` to play named fixture animations while browser-side layout code owns DOM target measurement and screen translation, matching the production rule that Compose owns real screen travel.

**Tech Stack:** TypeScript, Node.js, `@stevysmith/rive-generator@0.1.1`, React `19.2.8`, `@rive-app/react-canvas@4.33.1`, Vite `8.2.2`, `@vitejs/plugin-react@6.1.1`, Vitest `4.1.11`, jsdom, existing Kotlin/Jetpack Compose/Rive Android stack.

**Spec:** `docs/superpowers/specs/2026-09-02-patsy-rive-generator-react-harness-design.md`

## Global Constraints

- Preserve `PatsyRigContractV1.kt`, `PatsyRigRuntimePort.kt`, `PatsyRiveHost.kt`, `PatsyRiveRuntimeAdapter.kt`, and `PatsyAnimationContract.kt` as authoritative production interfaces.
- Production names remain exactly `PatsyAssistant`, `PatsyAssistantMachine`, `PatsyAssistantVM`, and `Default`.
- Production motion enum values remain exactly `idle`, `walk`, `sit`, `lie`, `jump`, `wave`, `point`.
- Production expression enum values remain exactly `neutral`, `cheeky`, `excited`, `curious`, `confused`, `concerned`, `proud`, `sleepy`.
- Production viseme enum values remain exactly `rest`, `a`, `e`, `i`, `o`, `u`, `mbp`, `fv`, `l`, `sz`.
- `@stevysmith/rive-generator@0.1.1` may create artboards, nodes/shapes, keyed linear animations, and export `.riv`; do not call undocumented state-machine/View Model APIs.
- The generated `Pet` / `pet.riv` asset is a development fixture only and must never replace `app/src/main/res/raw/patsy_assistant.riv`.
- Real safe-screen translation stays host-owned: browser CSS in the harness, Compose in Android production.
- Do not modify Camera, Media3, Supabase, auth/security, global navigation, THyNK Panel routing, or the THyNK-IN foundation.
- Missing/invalid production Rive must continue to fail closed; do not claim production Patsy Rive is complete from this tooling slice.
- Use RED/GREEN TDD and commit after each independently testable task.

---

## File Structure

Create one isolated tooling workspace so generator and harness can share the same contract mirror without introducing root-level JavaScript build behavior:

- `tools/patsy-rive-dev/package.json` — pinned tool dependencies and scripts.
- `tools/patsy-rive-dev/tsconfig.json` — strict TS config.
- `tools/patsy-rive-dev/vite.config.ts` — React + Vitest/jsdom configuration.
- `tools/patsy-rive-dev/index.html` — development harness entry page.
- `tools/patsy-rive-dev/src/patsyV1.ts` — exact TypeScript mirror of the Kotlin ABI plus clamps/mappings.
- `tools/patsy-rive-dev/src/generator/petFixture.ts` — pure `RiveFile` construction for `Pet` 500×500.
- `tools/patsy-rive-dev/src/generator/generatePet.ts` — CLI export to `public/pet.riv` or supplied path.
- `tools/patsy-rive-dev/src/harness/targeting.ts` — DOM measurement/normalized coordinate math.
- `tools/patsy-rive-dev/src/harness/PetController.ts` — deterministic movement/action orchestration independent of React.
- `tools/patsy-rive-dev/src/harness/PetFullScreen.tsx` — full-screen Rive renderer and imperative target API.
- `tools/patsy-rive-dev/src/App.tsx` — small manual demo with a target element.
- `tools/patsy-rive-dev/src/main.tsx` — Vite entry.
- `tools/patsy-rive-dev/src/styles.css` — full-screen overlay/demo styling.
- `tools/patsy-rive-dev/test/*.test.ts(x)` — contract, generator, targeting, controller, and component tests.
- `tools/patsy-rive-dev/public/pet.riv` — generated binary; add only after deterministic generator succeeds.

No production Kotlin source file should be modified in Tasks 1–4.

---

### Task 1: Isolated Tooling Workspace + V1 Contract Mirror

**Files:**
- Create: `tools/patsy-rive-dev/package.json`
- Create: `tools/patsy-rive-dev/tsconfig.json`
- Create: `tools/patsy-rive-dev/vite.config.ts`
- Create: `tools/patsy-rive-dev/src/patsyV1.ts`
- Test: `tools/patsy-rive-dev/test/patsyV1.test.ts`

**Interfaces:**
- Consumes: authoritative constants from `app/src/main/java/com/patsy/app/patsy/rig/PatsyRigContractV1.kt`.
- Produces: `PATSY_V1`, `PatsyMotion`, `PatsyExpression`, `PatsyViseme`, `PatsyDevPose`, `normalisePose()`.

- [ ] **Step 1: Create the package manifest and strict TypeScript config**

Use exactly these dependencies initially:

```json
{
  "name": "@thynk-in/patsy-rive-dev",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "generate:pet": "tsx src/generator/generatePet.ts public/pet.riv",
    "test": "vitest run",
    "test:watch": "vitest",
    "dev": "vite",
    "build": "tsc -b && vite build"
  },
  "dependencies": {
    "@rive-app/react-canvas": "4.33.1",
    "@stevysmith/rive-generator": "0.1.1",
    "react": "19.2.8",
    "react-dom": "19.2.8"
  },
  "devDependencies": {
    "@testing-library/react": "16.3.0",
    "@types/node": "24.3.0",
    "@types/react": "19.1.12",
    "@types/react-dom": "19.1.9",
    "@vitejs/plugin-react": "6.1.1",
    "jsdom": "26.1.0",
    "tsx": "4.20.5",
    "typescript": "5.9.2",
    "vite": "8.2.2",
    "vitest": "4.1.11"
  }
}
```

`tsconfig.json`:

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "useDefineForClassFields": true,
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "allowJs": false,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "forceConsistentCasingInFileNames": true,
    "module": "ESNext",
    "moduleResolution": "Bundler",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx"
  },
  "include": ["src", "test", "vite.config.ts"]
}
```

`vite.config.ts`:

```ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
  },
});
```

- [ ] **Step 2: Write the failing V1 mirror tests**

```ts
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import { PATSY_V1, normalisePose } from '../src/patsyV1';

const kotlinPath = resolve(
  process.cwd(),
  '../../app/src/main/java/com/patsy/app/patsy/rig/PatsyRigContractV1.kt',
);
const kotlin = readFileSync(kotlinPath, 'utf8');

describe('Patsy V1 mirror', () => {
  it('matches authoritative production names', () => {
    expect(kotlin).toContain(`ARTBOARD = "${PATSY_V1.artboard}"`);
    expect(kotlin).toContain(`STATE_MACHINE = "${PATSY_V1.stateMachine}"`);
    expect(kotlin).toContain(`VIEW_MODEL = "${PATSY_V1.viewModel}"`);
    expect(kotlin).toContain(`DEFAULT_VIEW_MODEL_INSTANCE = "${PATSY_V1.instance}"`);
  });

  it('keeps exact motion/expression/viseme spellings', () => {
    for (const value of PATSY_V1.motionValues) expect(kotlin).toContain(`"${value}"`);
    for (const value of PATSY_V1.expressionValues) expect(kotlin).toContain(`"${value}"`);
    for (const value of PATSY_V1.visemeValues) expect(kotlin).toContain(`"${value}"`);
  });

  it('clamps the same normalized ranges used by Kotlin', () => {
    const pose = normalisePose({
      pointX: 3,
      pointY: -2,
      stageScale: 9,
      lookX: 2,
      lookY: -4,
      headTilt: 8,
      reducedMotion: false,
    });
    expect(pose.pointX).toBe(1);
    expect(pose.pointY).toBe(0);
    expect(pose.stageScale).toBe(1.4);
    expect(pose.lookX).toBe(1);
    expect(pose.lookY).toBe(-1);
    expect(pose.headTilt).toBe(1);
  });
});
```

- [ ] **Step 3: Run the contract test and verify RED**

Run:

```bash
cd tools/patsy-rive-dev
npm install
npm test -- patsyV1.test.ts
```

Expected: FAIL because `src/patsyV1.ts` does not exist.

- [ ] **Step 4: Implement the minimal exact V1 mirror**

```ts
export const PATSY_V1 = {
  artboard: 'PatsyAssistant',
  stateMachine: 'PatsyAssistantMachine',
  viewModel: 'PatsyAssistantVM',
  instance: 'Default',
  motionValues: ['idle', 'walk', 'sit', 'lie', 'jump', 'wave', 'point'] as const,
  expressionValues: [
    'neutral', 'cheeky', 'excited', 'curious',
    'confused', 'concerned', 'proud', 'sleepy',
  ] as const,
  visemeValues: ['rest', 'a', 'e', 'i', 'o', 'u', 'mbp', 'fv', 'l', 'sz'] as const,
} as const;

export type PatsyMotion = (typeof PATSY_V1.motionValues)[number];
export type PatsyExpression = (typeof PATSY_V1.expressionValues)[number];
export type PatsyViseme = (typeof PATSY_V1.visemeValues)[number];

export interface PatsyDevPose {
  motion: PatsyMotion;
  pointX: number;
  pointY: number;
  stageX: number;
  stageY: number;
  stageScale: number;
  lookX: number;
  lookY: number;
  headTilt: number;
  talking: boolean;
  viseme: PatsyViseme;
  expression: PatsyExpression;
  reducedMotion: boolean;
  actionSequence: number;
}

const clamp = (value: number, min: number, max: number) => Math.min(max, Math.max(min, value));

export function normalisePose(input: Partial<PatsyDevPose>): PatsyDevPose {
  const pose: PatsyDevPose = {
    motion: input.motion ?? 'idle',
    pointX: clamp(input.pointX ?? 0.5, 0, 1),
    pointY: clamp(input.pointY ?? 0.5, 0, 1),
    stageX: clamp(input.stageX ?? 0.5, 0, 1),
    stageY: clamp(input.stageY ?? 0.75, 0, 1),
    stageScale: clamp(input.stageScale ?? 1, 0.45, 1.4),
    lookX: clamp(input.lookX ?? 0, -1, 1),
    lookY: clamp(input.lookY ?? 0, -1, 1),
    headTilt: clamp(input.headTilt ?? 0, -1, 1),
    talking: input.talking ?? false,
    viseme: input.viseme ?? 'rest',
    expression: input.expression ?? 'cheeky',
    reducedMotion: input.reducedMotion ?? false,
    actionSequence: Math.max(0, Math.floor(input.actionSequence ?? 0)),
  };

  if (!pose.reducedMotion) return pose;
  return {
    ...pose,
    motion: pose.motion === 'sit' || pose.motion === 'lie' ? pose.motion : 'idle',
    stageScale: clamp(pose.stageScale, 0.8, 1.1),
  };
}
```

- [ ] **Step 5: Run tests GREEN and commit**

```bash
npm test -- patsyV1.test.ts
git add tools/patsy-rive-dev
git commit -m "test: mirror Patsy V1 contract in dev tooling"
```

Expected: PASS.

---

### Task 2: Generate `Pet` 500×500 with `breathe`, `run`, `talk`, `point`

**Files:**
- Create: `tools/patsy-rive-dev/src/generator/petFixture.ts`
- Create: `tools/patsy-rive-dev/src/generator/generatePet.ts`
- Test: `tools/patsy-rive-dev/test/petFixture.test.ts`
- Generate: `tools/patsy-rive-dev/public/pet.riv`

**Interfaces:**
- Consumes: `RiveFile`, `PropertyKey`, `hex` from `@stevysmith/rive-generator`.
- Produces: `PET_FIXTURE`, `buildPetFixture(): RiveFile`, `exportPetFixture(): Uint8Array`.

- [ ] **Step 1: Write failing generator metadata/API-boundary tests**

```ts
import { describe, expect, it } from 'vitest';
import { PET_FIXTURE, buildPetFixture, exportPetFixture } from '../src/generator/petFixture';

describe('Pet Rive fixture', () => {
  it('locks requested fixture names and dimensions', () => {
    expect(PET_FIXTURE).toEqual({
      artboard: 'Pet',
      width: 500,
      height: 500,
      animations: ['breathe', 'run', 'talk', 'point'],
    });
  });

  it('exports real non-empty Rive bytes', () => {
    const bytes = exportPetFixture();
    expect(bytes).toBeInstanceOf(Uint8Array);
    expect(bytes.byteLength).toBeGreaterThan(32);
  });

  it('does not pretend generator 0.1.1 can author the production state machine', () => {
    const riv = buildPetFixture() as unknown as Record<string, unknown>;
    expect(riv.addStateMachine).toBeUndefined();
    expect(riv.addViewModel).toBeUndefined();
  });
});
```

- [ ] **Step 2: Run generator test RED**

```bash
npm test -- petFixture.test.ts
```

Expected: FAIL because the generator module does not exist.

- [ ] **Step 3: Implement the fixture using only documented generator APIs**

Use a simple abstract pet made from vector primitives; it is intentionally a development fixture and not Patsy's production artwork.

```ts
import { PropertyKey, RiveFile, hex } from '@stevysmith/rive-generator';

export const PET_FIXTURE = {
  artboard: 'Pet',
  width: 500,
  height: 500,
  animations: ['breathe', 'run', 'talk', 'point'],
} as const;

function addDoubleTrack(
  riv: RiveFile,
  animation: unknown,
  target: unknown,
  property: PropertyKey,
  frames: Array<{ frame: number; value: number }>,
) {
  const keyedObject = riv.addKeyedObject(animation as never, target as never);
  const keyedProperty = riv.addKeyedProperty(keyedObject, property);
  for (const key of frames) {
    riv.addKeyFrameDouble(keyedProperty, {
      ...key,
      interpolation: 'cubic',
    });
  }
}

export function buildPetFixture(): RiveFile {
  const riv = new RiveFile();
  const artboard = riv.addArtboard({
    name: PET_FIXTURE.artboard,
    width: PET_FIXTURE.width,
    height: PET_FIXTURE.height,
  });

  const pet = riv.addNode(artboard, { name: 'PetRoot', x: 250, y: 285 });

  const body = riv.addShape(pet, { name: 'Body', x: 0, y: 0 });
  riv.addEllipse(body, { width: 210, height: 150 });
  const bodyFill = riv.addFill(body);
  riv.addSolidColor(bodyFill, hex('#777777'));

  const head = riv.addShape(pet, { name: 'Head', x: 0, y: -115 });
  riv.addEllipse(head, { width: 150, height: 135 });
  const headFill = riv.addFill(head);
  riv.addSolidColor(headFill, hex('#888888'));

  const mouth = riv.addShape(pet, { name: 'Mouth', x: 0, y: -80 });
  riv.addEllipse(mouth, { width: 36, height: 12 });
  const mouthFill = riv.addFill(mouth);
  riv.addSolidColor(mouthFill, hex('#222222'));

  const paw = riv.addShape(pet, { name: 'PointPaw', x: 82, y: -5 });
  riv.addRectangle(paw, { width: 70, height: 22, cornerRadius: 11 });
  const pawFill = riv.addFill(paw);
  riv.addSolidColor(pawFill, hex('#888888'));

  const breathe = riv.addLinearAnimation(artboard, {
    name: 'breathe', fps: 60, duration: 120, loop: 'pingPong',
  });
  addDoubleTrack(riv, breathe, pet, PropertyKey.scaleY, [
    { frame: 0, value: 1 },
    { frame: 60, value: 1.05 },
    { frame: 120, value: 1 },
  ]);

  const run = riv.addLinearAnimation(artboard, {
    name: 'run', fps: 60, duration: 60, loop: 'pingPong',
  });
  addDoubleTrack(riv, run, pet, PropertyKey.x, [
    { frame: 0, value: -200 },
    { frame: 60, value: 200 },
  ]);

  const talk = riv.addLinearAnimation(artboard, {
    name: 'talk', fps: 60, duration: 30, loop: 'pingPong',
  });
  addDoubleTrack(riv, talk, mouth, PropertyKey.scaleY, [
    { frame: 0, value: 1 },
    { frame: 15, value: 2.2 },
    { frame: 30, value: 1 },
  ]);

  const point = riv.addLinearAnimation(artboard, {
    name: 'point', fps: 60, duration: 36, loop: 'pingPong',
  });
  addDoubleTrack(riv, point, paw, PropertyKey.rotation, [
    { frame: 0, value: 0 },
    { frame: 18, value: -0.55 },
    { frame: 36, value: 0 },
  ]);

  return riv;
}

export function exportPetFixture(): Uint8Array {
  return buildPetFixture().export();
}
```

If the installed TypeScript declarations show a different exact option name for rounded rectangles, remove `cornerRadius` rather than casting around an invalid API. Do not change animation/state-machine scope.

- [ ] **Step 4: Add the CLI exporter**

```ts
import { mkdirSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { exportPetFixture } from './petFixture';

const output = resolve(process.argv[2] ?? 'public/pet.riv');
mkdirSync(dirname(output), { recursive: true });
writeFileSync(output, exportPetFixture());
console.log(`Generated ${output}`);
```

- [ ] **Step 5: Run tests, export the file, verify bytes, commit**

```bash
npm test -- petFixture.test.ts
npm run generate:pet
node -e "const fs=require('fs'); const s=fs.statSync('public/pet.riv'); if(s.size<32) process.exit(1); console.log(s.size)"
git add tools/patsy-rive-dev/src/generator tools/patsy-rive-dev/test/petFixture.test.ts tools/patsy-rive-dev/public/pet.riv
git commit -m "feat: generate Pet Rive development fixture"
```

Expected: tests PASS and `public/pet.riv` is non-empty.

---

### Task 3: DOM Target Math + V1-Compatible Action Controller

**Files:**
- Create: `tools/patsy-rive-dev/src/harness/targeting.ts`
- Create: `tools/patsy-rive-dev/src/harness/PetController.ts`
- Test: `tools/patsy-rive-dev/test/targeting.test.ts`
- Test: `tools/patsy-rive-dev/test/PetController.test.ts`

**Interfaces:**
- Produces: `measureTarget(element, viewport)`, `PetController.runToAndPoint(element)`, `PetController.setTalking(boolean)`, `PetController.setTiny(boolean)`, `PetController.setReducedMotion(boolean)`.
- Browser translation result: `{ xPx, yPx, normalizedX, normalizedY, lookX, lookY }`.
- Rive playback port: `play(names: string | string[]): void`, `stop(names?: string | string[]): void`.

- [ ] **Step 1: Write failing target normalization tests**

```ts
import { describe, expect, it } from 'vitest';
import { measureTarget } from '../src/harness/targeting';

describe('measureTarget', () => {
  it('uses target centre and normalizes to viewport', () => {
    const element = {
      isConnected: true,
      getBoundingClientRect: () => ({ left: 800, top: 300, width: 200, height: 100 }),
    } as unknown as HTMLElement;
    expect(measureTarget(element, { width: 1000, height: 500 })).toMatchObject({
      normalizedX: 0.9,
      normalizedY: 0.7,
    });
  });

  it('clamps off-screen coordinates and rejects detached/zero-size targets', () => {
    const offscreen = {
      isConnected: true,
      getBoundingClientRect: () => ({ left: -400, top: 900, width: 100, height: 100 }),
    } as unknown as HTMLElement;
    expect(measureTarget(offscreen, { width: 1000, height: 500 })).toMatchObject({
      normalizedX: 0,
      normalizedY: 1,
    });

    const detached = { isConnected: false } as HTMLElement;
    expect(measureTarget(detached, { width: 1000, height: 500 })).toBeNull();
  });
});
```

- [ ] **Step 2: Run targeting test RED, then implement**

```ts
export interface ViewportSize { width: number; height: number }
export interface TargetMeasurement {
  xPx: number;
  yPx: number;
  normalizedX: number;
  normalizedY: number;
  lookX: number;
  lookY: number;
}

const clamp01 = (value: number) => Math.min(1, Math.max(0, value));

export function measureTarget(
  element: HTMLElement,
  viewport: ViewportSize,
): TargetMeasurement | null {
  if (!element?.isConnected || viewport.width <= 0 || viewport.height <= 0) return null;
  const rect = element.getBoundingClientRect();
  if (rect.width <= 0 || rect.height <= 0) return null;

  const centerX = rect.left + rect.width / 2;
  const centerY = rect.top + rect.height / 2;
  const normalizedX = clamp01(centerX / viewport.width);
  const normalizedY = clamp01(centerY / viewport.height);

  return {
    xPx: centerX,
    yPx: centerY,
    normalizedX,
    normalizedY,
    lookX: normalizedX * 2 - 1,
    lookY: normalizedY * 2 - 1,
  };
}
```

Run:

```bash
npm test -- targeting.test.ts
```

Expected: PASS.

- [ ] **Step 3: Write failing controller ordering/reduced-motion tests**

```ts
import { describe, expect, it, vi } from 'vitest';
import { PetController } from '../src/harness/PetController';

const target = {
  isConnected: true,
  getBoundingClientRect: () => ({ left: 800, top: 300, width: 100, height: 100 }),
} as unknown as HTMLElement;

describe('PetController', () => {
  it('prepares gaze/point data before run and point playback', async () => {
    const events: string[] = [];
    const controller = new PetController({
      play: (name) => events.push(`play:${Array.isArray(name) ? name.join('+') : name}`),
      stop: () => undefined,
      viewport: () => ({ width: 1000, height: 500 }),
      moveHost: async (_, __, ___) => events.push('moveHost'),
      delay: async () => undefined,
      onPose: (pose) => events.push(`pose:${pose.motion}:${pose.actionSequence}`),
    });

    await controller.runToAndPoint(target);
    expect(events.indexOf('pose:point:1')).toBeLessThan(events.indexOf('play:point'));
    expect(events).toContain('play:run');
    expect(controller.pose.pointX).toBeCloseTo(0.85);
  });

  it('does not cross-screen run when reduced motion is on', async () => {
    const moveHost = vi.fn(async () => undefined);
    const play = vi.fn();
    const controller = new PetController({
      play, stop: vi.fn(), viewport: () => ({ width: 1000, height: 500 }),
      moveHost, delay: async () => undefined,
    });
    controller.setReducedMotion(true);
    await controller.runToAndPoint(target);
    expect(moveHost).not.toHaveBeenCalled();
    expect(play).not.toHaveBeenCalledWith('run');
  });
});
```

- [ ] **Step 4: Implement the controller**

```ts
import { normalisePose, type PatsyDevPose } from '../patsyV1';
import { measureTarget, type ViewportSize } from './targeting';

export interface RivePlaybackPort {
  play(names: string | string[]): void;
  stop(names?: string | string[]): void;
}

export interface PetControllerDeps extends RivePlaybackPort {
  viewport: () => ViewportSize;
  moveHost: (xPx: number, yPx: number, scale: number) => Promise<void>;
  delay?: (ms: number) => Promise<void>;
  onPose?: (pose: PatsyDevPose) => void;
}

const sleep = (ms: number) => new Promise<void>((resolve) => setTimeout(resolve, ms));

export class PetController {
  pose: PatsyDevPose = normalisePose({});
  private readonly delay: (ms: number) => Promise<void>;

  constructor(private readonly deps: PetControllerDeps) {
    this.delay = deps.delay ?? sleep;
  }

  private commit(patch: Partial<PatsyDevPose>) {
    this.pose = normalisePose({ ...this.pose, ...patch });
    this.deps.onPose?.(this.pose);
  }

  setTalking(value: boolean) {
    this.commit({ talking: value, viseme: value ? 'a' : 'rest' });
    value ? this.deps.play(['breathe', 'talk']) : this.deps.stop('talk');
  }

  setTiny(value: boolean) {
    this.commit({ stageScale: value ? 0.45 : 1 });
  }

  setReducedMotion(value: boolean) {
    this.commit({ reducedMotion: value });
  }

  async runToAndPoint(target: HTMLElement): Promise<boolean> {
    const measurement = measureTarget(target, this.deps.viewport());
    if (!measurement) return false;

    this.commit({
      pointX: measurement.normalizedX,
      pointY: measurement.normalizedY,
      lookX: measurement.lookX,
      lookY: measurement.lookY,
    });

    if (!this.pose.reducedMotion) {
      this.commit({ motion: 'walk' });
      this.deps.play('run');
      await this.deps.moveHost(measurement.xPx, measurement.yPx, this.pose.stageScale);
      this.deps.stop('run');
    }

    this.commit({ motion: 'point', actionSequence: this.pose.actionSequence + 1 });
    this.deps.play('point');
    await this.delay(650);
    this.commit({ motion: 'idle' });
    this.deps.play('breathe');
    return true;
  }
}
```

- [ ] **Step 5: Run controller tests and commit**

```bash
npm test -- targeting.test.ts PetController.test.ts
git add tools/patsy-rive-dev/src/harness tools/patsy-rive-dev/test/targeting.test.ts tools/patsy-rive-dev/test/PetController.test.ts
git commit -m "feat: add DOM targeting and Patsy-compatible action controller"
```

Expected: PASS.

---

### Task 4: Full-Screen React Renderer + Imperative `runToAndPoint`

**Files:**
- Create: `tools/patsy-rive-dev/index.html`
- Create: `tools/patsy-rive-dev/src/harness/PetFullScreen.tsx`
- Create: `tools/patsy-rive-dev/src/App.tsx`
- Create: `tools/patsy-rive-dev/src/main.tsx`
- Create: `tools/patsy-rive-dev/src/styles.css`
- Test: `tools/patsy-rive-dev/test/PetFullScreen.test.tsx`

**Interfaces:**
- Produces `PetFullScreenHandle` with `runToAndPoint(target: HTMLElement): Promise<boolean>`, `setTalking(value: boolean)`, `setTiny(value: boolean)`, `setReducedMotion(value: boolean)`.
- The component renders a pointer-events-none full-screen overlay; only the demo target/button layer receives pointer events.

- [ ] **Step 1: Write a failing component contract test with mocked Rive runtime**

```tsx
import { createRef } from 'react';
import { act, render } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { PetFullScreen, type PetFullScreenHandle } from '../src/harness/PetFullScreen';

const play = vi.fn();
const stop = vi.fn();

vi.mock('@rive-app/react-canvas', () => ({
  useRive: () => ({
    rive: { play, stop },
    RiveComponent: () => <canvas data-testid="rive-canvas" />,
  }),
}));

describe('PetFullScreen', () => {
  it('renders full-screen and exposes safe imperative controls', async () => {
    const ref = createRef<PetFullScreenHandle>();
    const { getByTestId } = render(<PetFullScreen ref={ref} />);
    expect(getByTestId('pet-overlay')).toBeTruthy();

    await act(async () => {
      ref.current?.setTalking(true);
      ref.current?.setTiny(true);
    });
    expect(play).toHaveBeenCalled();
  });
});
```

- [ ] **Step 2: Run component test RED**

```bash
npm test -- PetFullScreen.test.tsx
```

Expected: FAIL because the component does not exist.

- [ ] **Step 3: Implement the full-screen component**

```tsx
import {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useMemo,
  useRef,
  useState,
} from 'react';
import { useRive } from '@rive-app/react-canvas';
import { PetController } from './PetController';

export interface PetFullScreenHandle {
  runToAndPoint(target: HTMLElement): Promise<boolean>;
  setTalking(value: boolean): void;
  setTiny(value: boolean): void;
  setReducedMotion(value: boolean): void;
}

export const PetFullScreen = forwardRef<PetFullScreenHandle>(function PetFullScreen(_, ref) {
  const hostRef = useRef<HTMLDivElement>(null);
  const [scale, setScale] = useState(1);
  const { rive, RiveComponent } = useRive({
    src: '/pet.riv',
    artboard: 'Pet',
    animations: ['breathe'],
    autoplay: true,
  });

  const controller = useMemo(() => new PetController({
    play: (names) => rive?.play(names),
    stop: (names) => rive?.stop(names),
    viewport: () => ({ width: window.innerWidth, height: window.innerHeight }),
    moveHost: async (xPx, yPx, nextScale) => {
      setScale(nextScale);
      const host = hostRef.current;
      if (!host) return;
      host.style.transform = `translate3d(${xPx}px, ${yPx}px, 0) translate(-50%, -50%) scale(${nextScale})`;
      await new Promise<void>((resolve) => setTimeout(resolve, 550));
    },
  }), [rive]);

  useEffect(() => {
    const host = hostRef.current;
    if (!host) return;
    host.style.transform = `translate3d(50vw, 75vh, 0) translate(-50%, -50%) scale(${scale})`;
  }, []);

  useImperativeHandle(ref, () => ({
    runToAndPoint: (target) => controller.runToAndPoint(target),
    setTalking: (value) => controller.setTalking(value),
    setTiny: (value) => {
      controller.setTiny(value);
      setScale(controller.pose.stageScale);
      if (hostRef.current) hostRef.current.style.scale = String(controller.pose.stageScale);
    },
    setReducedMotion: (value) => controller.setReducedMotion(value),
  }), [controller]);

  return (
    <div className="pet-overlay" data-testid="pet-overlay" aria-hidden="true">
      <div className="pet-host" ref={hostRef}>
        <RiveComponent />
      </div>
    </div>
  );
});
```

During implementation, if `rive.play`/`rive.stop` signatures in `@rive-app/react-canvas@4.33.1` reject `undefined`, branch before calling them instead of using `as any`.

- [ ] **Step 4: Add the demo app and full-screen CSS**

`App.tsx`:

```tsx
import { useRef } from 'react';
import { PetFullScreen, type PetFullScreenHandle } from './harness/PetFullScreen';
import './styles.css';

export default function App() {
  const pet = useRef<PetFullScreenHandle>(null);
  const target = useRef<HTMLButtonElement>(null);

  return (
    <main className="demo-page">
      <PetFullScreen ref={pet} />
      <button
        ref={target}
        className="demo-target"
        onClick={() => target.current && void pet.current?.runToAndPoint(target.current)}
      >
        Run here & point
      </button>
      <div className="demo-controls">
        <button onMouseDown={() => pet.current?.setTalking(true)} onMouseUp={() => pet.current?.setTalking(false)}>Talk</button>
        <button onClick={() => pet.current?.setTiny(true)}>Tiny</button>
        <button onClick={() => pet.current?.setTiny(false)}>Normal</button>
      </div>
    </main>
  );
}
```

`main.tsx`:

```tsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';

createRoot(document.getElementById('root')!).render(
  <StrictMode><App /></StrictMode>,
);
```

`styles.css`:

```css
html, body, #root { width: 100%; min-height: 100%; margin: 0; }
body { min-height: 100vh; background: #111; color: white; font-family: system-ui, sans-serif; }
.demo-page { min-height: 100vh; position: relative; overflow: hidden; }
.pet-overlay { position: fixed; inset: 0; z-index: 50; pointer-events: none; overflow: hidden; }
.pet-host {
  position: absolute;
  width: min(42vw, 300px);
  aspect-ratio: 1;
  left: 0;
  top: 0;
  transform-origin: center;
  transition: transform 550ms cubic-bezier(.2,.8,.2,1);
}
.pet-host canvas { width: 100%; height: 100%; }
.demo-target { position: absolute; right: 7vw; top: 18vh; z-index: 5; }
.demo-controls { position: fixed; left: 16px; bottom: 16px; display: flex; gap: 8px; z-index: 60; }
```

`index.html` must contain `<div id="root"></div>` and a module script for `/src/main.tsx`.

- [ ] **Step 5: Run component tests and production build, then commit**

```bash
npm test -- PetFullScreen.test.tsx
npm run build
git add tools/patsy-rive-dev
git commit -m "feat: add full-screen React Rive targeting harness"
```

Expected: tests PASS and Vite build succeeds.

---

### Task 5: Full Verification + Native Regression Gate

**Files:**
- Modify only if needed: `docs/superpowers/specs/2026-09-02-patsy-rive-generator-react-harness-design.md` for factual correction discovered during implementation.
- Do not modify production Android files merely to make the tooling pass.

**Interfaces:**
- Consumes all previous tasks.
- Produces verification evidence for the branch and confirms no native architectural drift.

- [ ] **Step 1: Run the entire TypeScript tooling suite**

```bash
cd tools/patsy-rive-dev
npm ci
npm run generate:pet
npm test
npm run build
```

Expected: all tests PASS, `public/pet.riv` exists and build succeeds.

- [ ] **Step 2: Verify exact generated fixture and forbidden API boundary**

```bash
node -e "const fs=require('fs'); const p='public/pet.riv'; if(!fs.existsSync(p)||fs.statSync(p).size<32) process.exit(1); console.log('pet.riv bytes', fs.statSync(p).size)"
grep -R "addStateMachine\|addViewModel" -n src || true
```

Expected: binary exists; grep must find no implementation call that pretends the package authors state machines/View Models. The explicit negative test may mention those strings.

- [ ] **Step 3: Confirm implementation did not touch forbidden native areas**

From repository root:

```bash
git diff --name-only 08202373ec2162cb528c0aab73f11fbf7e029ba5...HEAD
```

Allowed implementation paths are `tools/patsy-rive-dev/**` plus the approved design/plan docs. Any changed path under Camera, Supabase, navigation, Media3, auth/security, or THyNK Panel code is a failure and must be reverted.

- [ ] **Step 4: Run existing native Android regression suite**

From repository root:

```bash
./gradlew test
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```

Expected: all unit tests PASS; debug and release APK builds complete successfully.

- [ ] **Step 5: Inspect V1 source-of-truth files are byte-for-byte unchanged from the branch base**

```bash
git diff 08202373ec2162cb528c0aab73f11fbf7e029ba5...HEAD -- \
  app/src/main/java/com/patsy/app/patsy/PatsyAnimationContract.kt \
  app/src/main/java/com/patsy/app/patsy/rig/PatsyRigContractV1.kt \
  app/src/main/java/com/patsy/app/patsy/rig/PatsyRigRuntimePort.kt \
  app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveHost.kt \
  app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveRuntimeAdapter.kt
```

Expected: no diff.

- [ ] **Step 6: Final commit for verification-only fixes, if any**

If verification required only tooling/doc corrections:

```bash
git add tools/patsy-rive-dev docs/superpowers/specs/2026-09-02-patsy-rive-generator-react-harness-design.md
git commit -m "test: verify Patsy Rive generator harness"
```

If there were no changes after verification, do not create an empty commit.

---

## Plan Self-Review

- Spec coverage: generator fixture, requested four named animations, `.riv` export, React full-screen rendering, DOM-target movement + point sequence, V1 mirror, reduced motion, safe missing target behavior, forbidden fake state-machine boundary, and native regression checks are all assigned to tasks.
- Scope: generator and React harness are tightly coupled development tooling sharing one V1 contract mirror, so one plan is appropriate; production Rive authoring remains explicitly outside this slice.
- Type consistency: `PatsyDevPose`, `PetControllerDeps`, `PetFullScreenHandle`, `measureTarget()`, and action names are defined once and reused consistently.
- No production state machine is claimed. `Pet`/`pet.riv` remains a fixture, while `PatsyAssistant`/`PatsyAssistantMachine`/`PatsyAssistantVM`/`Default` remain production names.
