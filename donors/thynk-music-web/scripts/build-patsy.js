const fs = require('fs');
const path = require('path');

const root = path.resolve(__dirname, '..');
const publicDir = path.join(root, 'public');
const assetDir = path.join(publicDir, 'assets', 'patsy');
const outputPath = path.join(publicDir, 'patsy.json');

const frameNames = Array.from(
  { length: 12 },
  (_, index) => `patsy_shrink_${String(index).padStart(2, '0')}.png`,
);

const assets = {
  big: 'photo3491664879986803385.jpeg',
  frames: frameNames,
  rainbow: 'video4635308202773325454.mp4',
};

const requiredNames = [assets.big, ...assets.frames, assets.rainbow];
const missing = requiredNames.filter((name) => !fs.existsSync(path.join(assetDir, name)));

if (missing.length > 0) {
  console.error('Patsy donor build stopped: real source assets are missing.');
  console.error(`Expected local asset folder: ${assetDir}`);
  missing.forEach((name) => console.error(` - ${name}`));
  console.error('Stage the owner-approved Patsy files locally before running npm run build:patsy.');
  process.exit(1);
}

fs.mkdirSync(publicDir, { recursive: true });

const manifest = {
  schema: 'thynk.patsy.donor.v1',
  productionRuntime: false,
  note: 'Reference/donor manifest only. THyNK-IN Android production remains Kotlin + Jetpack Compose + the native Patsy/Rive boundary.',
  source: {
    ownerApproved: true,
    driveFolderReference: '1JPoF76jSWUNMdVsvIo0m4HAcTxskaKlU',
    localAssetRoot: '/assets/patsy',
    verifiedAtBuild: true,
  },
  artboard: {
    name: 'Patsy',
    width: 720,
    height: 720,
    transparent: true,
  },
  size: {
    Big: { visualPx: 300, scale: 1.0, thumbEquivalent: 2 },
    Mini: { visualPx: 150, scale: 0.5, thumbEquivalent: 1 },
  },
  transitions: {
    ShrinkQuick: {
      durationMs: 800,
      easing: 'easeOutCubic',
      frameCount: 12,
      frames: assets.frames.map((name) => `/assets/patsy/${name}`),
      effect: {
        kind: 'rising-rainbow-glitter',
        source: `/assets/patsy/${assets.rainbow}`,
      },
    },
    MissionRun: {
      durationMs: 400,
      targetScale: 0.5,
    },
  },
  states: {
    Body: ['Idle', 'Walking', 'Running', 'Sitting', 'Lying', 'Standing'],
    Voice: ['Silent', 'Listening', 'Thinking', 'Speaking', 'Laughing'],
    Action: ['None', 'Wave', 'Point', 'Jump', 'Peek', 'CoverEyes', 'Celebrate', 'Shrink', 'Expand'],
    Emotion: ['Neutral', 'Happy', 'Curious', 'Focused', 'Concerned', 'Shy', 'Judging', 'Baffled'],
    Attention: ['Neutral', 'User', 'Camera', 'UIControl', 'WorldTarget', 'AIExplicit'],
    Size: ['Big', 'Mini'],
  },
};

fs.writeFileSync(outputPath, `${JSON.stringify(manifest, null, 2)}\n`);
console.log(`Built verified Patsy donor manifest: ${outputPath}`);
