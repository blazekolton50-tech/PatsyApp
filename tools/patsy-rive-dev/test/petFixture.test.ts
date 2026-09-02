import { describe, expect, it } from 'vitest';
import {
  PET_FIXTURE,
  buildPetFixture,
  exportPetFixture,
} from '../src/generator/petFixture';

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

  it('does not pretend generator 0.1.1 can author a state machine or View Model', () => {
    const riv = buildPetFixture() as unknown as Record<string, unknown>;
    expect(riv.addStateMachine).toBeUndefined();
    expect(riv.addViewModel).toBeUndefined();
  });
});
