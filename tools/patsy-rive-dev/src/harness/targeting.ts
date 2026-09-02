export interface ViewportSize {
  width: number;
  height: number;
}

export interface TargetMeasurement {
  xPx: number;
  yPx: number;
  normalizedX: number;
  normalizedY: number;
  lookX: number;
  lookY: number;
}

const clamp01 = (value: number) => Math.min(1, Math.max(0, value));
const stable = (value: number) => Math.round(value * 1_000_000) / 1_000_000;

export function measureTarget(
  element: HTMLElement,
  viewport: ViewportSize,
): TargetMeasurement | null {
  if (!element?.isConnected || viewport.width <= 0 || viewport.height <= 0) {
    return null;
  }

  const rect = element.getBoundingClientRect();
  if (rect.width <= 0 || rect.height <= 0) return null;

  const centerX = rect.left + rect.width / 2;
  const centerY = rect.top + rect.height / 2;
  const normalizedX = stable(clamp01(centerX / viewport.width));
  const normalizedY = stable(clamp01(centerY / viewport.height));

  return {
    xPx: centerX,
    yPx: centerY,
    normalizedX,
    normalizedY,
    lookX: stable(normalizedX * 2 - 1),
    lookY: stable(normalizedY * 2 - 1),
  };
}
