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
