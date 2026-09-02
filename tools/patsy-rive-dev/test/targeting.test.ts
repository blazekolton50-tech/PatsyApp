import { describe, expect, it } from 'vitest';
import { measureTarget } from '../src/harness/targeting';

describe('measureTarget', () => {
  it('uses the target centre and normalizes it to the viewport', () => {
    const element = {
      isConnected: true,
      getBoundingClientRect: () => ({
        left: 800,
        top: 300,
        width: 200,
        height: 100,
      }),
    } as unknown as HTMLElement;

    expect(measureTarget(element, { width: 1000, height: 500 })).toMatchObject({
      normalizedX: 0.9,
      normalizedY: 0.7,
      lookX: 0.8,
      lookY: 0.4,
    });
  });

  it('clamps off-screen coordinates', () => {
    const element = {
      isConnected: true,
      getBoundingClientRect: () => ({
        left: -400,
        top: 900,
        width: 100,
        height: 100,
      }),
    } as unknown as HTMLElement;

    expect(measureTarget(element, { width: 1000, height: 500 })).toMatchObject({
      normalizedX: 0,
      normalizedY: 1,
      lookX: -1,
      lookY: 1,
    });
  });

  it('rejects detached and zero-sized targets', () => {
    const detached = { isConnected: false } as HTMLElement;
    expect(measureTarget(detached, { width: 1000, height: 500 })).toBeNull();

    const zeroSized = {
      isConnected: true,
      getBoundingClientRect: () => ({ left: 0, top: 0, width: 0, height: 0 }),
    } as unknown as HTMLElement;
    expect(measureTarget(zeroSized, { width: 1000, height: 500 })).toBeNull();
  });
});
