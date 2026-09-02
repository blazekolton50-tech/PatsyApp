import { mkdirSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { exportPetFixture } from './petFixture';

const output = resolve(process.argv[2] ?? 'public/pet.riv');
mkdirSync(dirname(output), { recursive: true });
const bytes = exportPetFixture();
writeFileSync(output, bytes);
console.log(`Generated ${output} (${bytes.byteLength} bytes)`);
