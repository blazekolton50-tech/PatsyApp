import { describe, expect, it, vi } from 'vitest';
import { PetController } from '../src/harness/PetController';

const target = {
  isConnected: true,
  getBoundingClientRect: () => ({
    left: 800,
    top: 300,
    width: 100,
    height: 100,
  }),
} as unknown as HTMLElement;

describe('PetController', () => {
  it('prepares gaze and point data before run/point playback', async () => {
    const events: string[] = [];
    const controller = new PetController({
      play: (name) => events.push(`play:${Array.isArray(name) ? name.join('+') : name}`),
      stop: (name) => events.push(`stop:${Array.isArray(name) ? name.join('+') : name ?? 'all'}`),
      viewport: () => ({ width: 1000, height: 500 }),
      moveHost: async () => { events.push('moveHost'); },
      delay: async () => undefined,
      onPose: (pose) => events.push(`pose:${pose.motion}:${pose.actionSequence}`),
    });

    const result = await controller.runToAndPoint(target);

    expect(result).toBe(true);
    expect(controller.pose.pointX).toBeCloseTo(0.85);
    expect(controller.pose.pointY).toBeCloseTo(0.7);
    expect(events).toContain('play:run');
    expect(events).toContain('moveHost');
    expect(events.indexOf('pose:point:1')).toBeLessThan(events.indexOf('play:point'));
    expect(controller.pose.motion).toBe('idle');
  });

  it('increments the one-shot sequence on each point', async () => {
    const controller = new PetController({
      play: vi.fn(),
      stop: vi.fn(),
      viewport: () => ({ width: 1000, height: 500 }),
      moveHost: async () => undefined,
      delay: async () => undefined,
    });

    await controller.runToAndPoint(target);
    await controller.runToAndPoint(target);
    expect(controller.pose.actionSequence).toBe(2);
  });

  it('suppresses cross-screen run when reduced motion is enabled', async () => {
    const moveHost = vi.fn(async () => undefined);
    const play = vi.fn();
    const controller = new PetController({
      play,
      stop: vi.fn(),
      viewport: () => ({ width: 1000, height: 500 }),
      moveHost,
      delay: async () => undefined,
    });

    controller.setReducedMotion(true);
    await controller.runToAndPoint(target);

    expect(moveHost).not.toHaveBeenCalled();
    expect(play).not.toHaveBeenCalledWith('run');
    expect(play).toHaveBeenCalledWith('point');
  });

  it('fails safely when the DOM target cannot be measured', async () => {
    const controller = new PetController({
      play: vi.fn(),
      stop: vi.fn(),
      viewport: () => ({ width: 1000, height: 500 }),
      moveHost: vi.fn(async () => undefined),
      delay: async () => undefined,
    });

    const detached = { isConnected: false } as HTMLElement;
    expect(await controller.runToAndPoint(detached)).toBe(false);
    expect(controller.pose.motion).toBe('idle');
  });
});
