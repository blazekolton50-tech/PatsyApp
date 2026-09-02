import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./app/**/*.{ts,tsx}', './components/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        studioBgMain: '#030305',
        studioBgSurface: '#07070c',
        studioBgCard: '#0f0f16',
        studioBgInput: '#141420',
        studioBorder: '#1a1a28',
        neonPink: '#ff007f',
        neonPurple: '#7f00ff',
        neonCyan: '#00f0ff',
        neonGreen: '#10b981',
        neonAmber: '#f59e0b',
        neonRed: '#ef4444',
        thynk: {
          bg: '#050508',
          card: '#0f0f14',
          pink: '#ec4899',
          violet: '#8b5cf6',
          cyan: '#06b6d4',
          green: '#22c55e',
          orange: '#f97316'
        }
      }
    }
  },
  plugins: []
};

export default config;