export const playFreq = (freq: number, type: OscillatorType = 'sine') => {
  const ctx = new (window.AudioContext || (window as any).webkitAudioContext)();
  const oscillator = ctx.createOscillator();
  const gain = ctx.createGain();

  oscillator.frequency.value = freq;
  oscillator.type = type;
  oscillator.connect(gain);
  gain.connect(ctx.destination);
  gain.gain.setValueAtTime(0.6, ctx.currentTime);
  gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.3);
  oscillator.start();
  oscillator.stop(ctx.currentTime + 0.35);
};