export const PATSY_V1 = {
  artboard: 'PatsyAssistant',
  stateMachine: 'PatsyAssistantMachine',
  viewModel: 'PatsyAssistantVM',
  instance: 'Default',
  motionValues: ['idle', 'walk', 'sit', 'lie', 'jump', 'wave', 'point'] as const,
  expressionValues: [
    'neutral',
    'cheeky',
    'excited',
    'curious',
    'confused',
    'concerned',
    'proud',
    'sleepy',
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

const clamp = (value: number, min: number, max: number) =>
  Math.min(max, Math.max(min, value));

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
