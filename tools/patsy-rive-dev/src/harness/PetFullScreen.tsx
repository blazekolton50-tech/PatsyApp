import {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useMemo,
  useRef,
} from 'react';
import { useRive } from '@rive-app/react-canvas';
import { PetController } from './PetController';

export interface PetFullScreenHandle {
  runToAndPoint(target: HTMLElement): Promise<boolean>;
  setTalking(value: boolean): void;
  setTiny(value: boolean): void;
  setReducedMotion(value: boolean): void;
}

function setHostPosition(
  host: HTMLDivElement,
  xPx: number,
  yPx: number,
  scale: number,
) {
  host.style.left = `${xPx}px`;
  host.style.top = `${yPx}px`;
  host.style.setProperty('--pet-scale', String(scale));
}

export const PetFullScreen = forwardRef<PetFullScreenHandle>(
  function PetFullScreen(_, ref) {
    const hostRef = useRef<HTMLDivElement>(null);
    const { rive, RiveComponent } = useRive({
      src: '/pet.riv',
      artboard: 'Pet',
      animations: ['breathe'],
      autoplay: true,
    });

    const controller = useMemo(
      () =>
        new PetController({
          play: (names) => {
            rive?.play(names);
          },
          stop: (names) => {
            if (!rive) return;
            if (names === undefined) {
              rive.stop();
            } else {
              rive.stop(names);
            }
          },
          viewport: () => ({
            width: window.innerWidth,
            height: window.innerHeight,
          }),
          moveHost: async (xPx, yPx, scale) => {
            const host = hostRef.current;
            if (!host) return;
            setHostPosition(host, xPx, yPx, scale);
            await new Promise<void>((resolve) => setTimeout(resolve, 550));
          },
        }),
      [rive],
    );

    useEffect(() => {
      const host = hostRef.current;
      if (!host) return;
      setHostPosition(
        host,
        window.innerWidth * 0.5,
        window.innerHeight * 0.75,
        controller.pose.stageScale,
      );
    }, [controller]);

    useImperativeHandle(
      ref,
      () => ({
        runToAndPoint: (target) => controller.runToAndPoint(target),
        setTalking: (value) => controller.setTalking(value),
        setTiny: (value) => {
          controller.setTiny(value);
          const host = hostRef.current;
          if (host) {
            host.style.setProperty(
              '--pet-scale',
              String(controller.pose.stageScale),
            );
          }
        },
        setReducedMotion: (value) => controller.setReducedMotion(value),
      }),
      [controller],
    );

    return (
      <div
        className="pet-overlay"
        data-testid="pet-overlay"
        aria-hidden="true"
      >
        <div className="pet-host" ref={hostRef}>
          <RiveComponent />
        </div>
      </div>
    );
  },
);
