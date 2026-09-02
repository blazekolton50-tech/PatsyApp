import { createRef } from 'react';
import { act, render } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import {
  PetFullScreen,
  type PetFullScreenHandle,
} from '../src/harness/PetFullScreen';

const play = vi.fn();
const stop = vi.fn();

vi.mock('@rive-app/react-canvas', () => ({
  useRive: () => ({
    rive: { play, stop },
    RiveComponent: () => <canvas data-testid="rive-canvas" />,
  }),
}));

describe('PetFullScreen', () => {
  it('renders a full-screen Rive overlay and exposes imperative controls', async () => {
    const ref = createRef<PetFullScreenHandle>();
    const { getByTestId } = render(<PetFullScreen ref={ref} />);

    expect(getByTestId('pet-overlay')).toBeTruthy();
    expect(getByTestId('rive-canvas')).toBeTruthy();

    await act(async () => {
      ref.current?.setTalking(true);
      ref.current?.setTiny(true);
    });

    expect(play).toHaveBeenCalled();
  });

  it('can run to a measurable DOM element and point', async () => {
    const ref = createRef<PetFullScreenHandle>();
    render(<PetFullScreen ref={ref} />);

    const target = document.createElement('button');
    document.body.appendChild(target);
    Object.defineProperty(target, 'getBoundingClientRect', {
      value: () => ({ left: 700, top: 200, width: 100, height: 80 }),
    });

    const result = await act(async () => ref.current?.runToAndPoint(target));
    expect(result).toBe(true);
    expect(play).toHaveBeenCalledWith('point');
  });
});
