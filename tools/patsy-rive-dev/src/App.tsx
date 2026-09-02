import { useRef, useState } from 'react';
import {
  PetFullScreen,
  type PetFullScreenHandle,
} from './harness/PetFullScreen';
import './styles.css';

export default function App() {
  const pet = useRef<PetFullScreenHandle>(null);
  const target = useRef<HTMLButtonElement>(null);
  const [tiny, setTiny] = useState(false);
  const [reducedMotion, setReducedMotion] = useState(false);

  const runToTarget = () => {
    if (target.current) {
      void pet.current?.runToAndPoint(target.current);
    }
  };

  return (
    <main className="demo-page">
      <PetFullScreen ref={pet} />

      <section className="demo-copy">
        <p className="eyebrow">THyNK-IN development tooling</p>
        <h1>Patsy Rive interaction harness</h1>
        <p>
          This browser harness is development-only. The production Patsy runtime
          remains native Kotlin, Jetpack Compose and Rive.
        </p>
      </section>

      <button ref={target} className="demo-target" onClick={runToTarget}>
        Run here &amp; point
      </button>

      <div className="demo-controls" aria-label="Pet development controls">
        <button
          onPointerDown={() => pet.current?.setTalking(true)}
          onPointerUp={() => pet.current?.setTalking(false)}
          onPointerCancel={() => pet.current?.setTalking(false)}
        >
          Hold to talk
        </button>
        <button
          onClick={() => {
            const next = !tiny;
            setTiny(next);
            pet.current?.setTiny(next);
          }}
        >
          {tiny ? 'Normal size' : 'Tiny'}
        </button>
        <button
          onClick={() => {
            const next = !reducedMotion;
            setReducedMotion(next);
            pet.current?.setReducedMotion(next);
          }}
        >
          Reduced motion: {reducedMotion ? 'on' : 'off'}
        </button>
      </div>
    </main>
  );
}
