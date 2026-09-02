import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./app/**/*.{ts,tsx}', './components/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
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