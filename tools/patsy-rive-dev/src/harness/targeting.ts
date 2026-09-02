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
