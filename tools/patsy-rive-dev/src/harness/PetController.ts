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

const sleep = (ms: number) =>
  new Promise<void>((resolve) => setTimeout(resolve, ms));

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
    this.commit({
      talking: value,
      viseme: value ? 'a' : 'rest',
    });

    if (value) {
      this.deps.play(['breathe', 'talk']);
    } else {
      this.deps.stop('talk');
      this.deps.play('breathe');
    }
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

    // Prepare target and gaze first so the character visually leads the gesture.
    this.commit({
      pointX: measurement.normalizedX,
      pointY: measurement.normalizedY,
      lookX: measurement.lookX,
      lookY: measurement.lookY,
    });

    if (!this.pose.reducedMotion) {
      this.commit({ motion: 'walk' });
      this.deps.play('run');
      await this.deps.moveHost(
        measurement.xPx,
        measurement.yPx,
        this.pose.stageScale,
      );
      this.deps.stop('run');
    }

    this.commit({
      motion: 'point',
      actionSequence: this.pose.actionSequence + 1,
    });
    this.deps.play('point');
    await this.delay(650);

    this.commit({ motion: 'idle' });
    this.deps.play('breathe');
    return true;
  }
}
