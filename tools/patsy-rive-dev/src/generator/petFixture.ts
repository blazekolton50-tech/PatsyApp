import {
  PropertyKey,
  RiveFile,
  hex,
} from '@stevysmith/rive-generator';

export const PET_FIXTURE = {
  artboard: 'Pet',
  width: 500,
  height: 500,
  animations: ['breathe', 'run', 'talk', 'point'],
} as const;

export function buildPetFixture(): RiveFile {
  const riv = new RiveFile();
  const artboard = riv.addArtboard({
    name: PET_FIXTURE.artboard,
    width: PET_FIXTURE.width,
    height: PET_FIXTURE.height,
  });

  const pet = riv.addNode(artboard, {
    name: 'PetRoot',
    x: 250,
    y: 285,
  });

  const body = riv.addShape(pet, { name: 'Body' });
  riv.addEllipse(body, { width: 210, height: 150 });
  const bodyFill = riv.addFill(body);
  riv.addSolidColor(bodyFill, hex('#777777'));

  const head = riv.addShape(pet, {
    name: 'Head',
    x: 0,
    y: -115,
  });
  riv.addEllipse(head, { width: 150, height: 135 });
  const headFill = riv.addFill(head);
  riv.addSolidColor(headFill, hex('#888888'));

  const mouth = riv.addShape(pet, {
    name: 'Mouth',
    x: 0,
    y: -80,
  });
  riv.addEllipse(mouth, { width: 36, height: 12 });
  const mouthFill = riv.addFill(mouth);
  riv.addSolidColor(mouthFill, hex('#222222'));

  const paw = riv.addShape(pet, {
    name: 'PointPaw',
    x: 82,
    y: -5,
  });
  riv.addRectangle(paw, { width: 70, height: 22 });
  const pawFill = riv.addFill(paw);
  riv.addSolidColor(pawFill, hex('#888888'));

  const breathe = riv.addLinearAnimation(artboard, {
    name: 'breathe',
    fps: 60,
    duration: 120,
    loop: 'pingPong',
  });
  const breatheObject = riv.addKeyedObject(breathe, pet);
  const breatheScaleY = riv.addKeyedProperty(breatheObject, PropertyKey.scaleY);
  riv.addKeyFrameDouble(breatheScaleY, {
    frame: 0,
    value: 1,
    interpolation: 'cubic',
  });
  riv.addKeyFrameDouble(breatheScaleY, {
    frame: 60,
    value: 1.05,
    interpolation: 'cubic',
  });
  riv.addKeyFrameDouble(breatheScaleY, {
    frame: 120,
    value: 1,
    interpolation: 'cubic',
  });

  const run = riv.addLinearAnimation(artboard, {
    name: 'run',
    fps: 60,
    duration: 60,
    loop: 'pingPong',
  });
  const runObject = riv.addKeyedObject(run, pet);
  const runX = riv.addKeyedProperty(runObject, PropertyKey.x);
  riv.addKeyFrameDouble(runX, {
    frame: 0,
    value: -200,
    interpolation: 'cubic',
  });
  riv.addKeyFrameDouble(runX, {
    frame: 60,
    value: 200,
    interpolation: 'cubic',
  });

  const talk = riv.addLinearAnimation(artboard, {
    name: 'talk',
    fps: 60,
    duration: 30,
    loop: 'pingPong',
  });
  const talkObject = riv.addKeyedObject(talk, mouth);
  const mouthScaleY = riv.addKeyedProperty(talkObject, PropertyKey.scaleY);
  riv.addKeyFrameDouble(mouthScaleY, {
    frame: 0,
    value: 1,
    interpolation: 'cubic',
  });
  riv.addKeyFrameDouble(mouthScaleY, {
    frame: 15,
    value: 2.2,
    interpolation: 'cubic',
  });
  riv.addKeyFrameDouble(mouthScaleY, {
    frame: 30,
    value: 1,
    interpolation: 'cubic',
  });

  const point = riv.addLinearAnimation(artboard, {
    name: 'point',
    fps: 60,
    duration: 36,
    loop: 'pingPong',
  });
  const pointObject = riv.addKeyedObject(point, paw);
  const pawRotation = riv.addKeyedProperty(pointObject, PropertyKey.rotation);
  riv.addKeyFrameDouble(pawRotation, {
    frame: 0,
    value: 0,
    interpolation: 'cubic',
  });
  riv.addKeyFrameDouble(pawRotation, {
    frame: 18,
    value: -0.55,
    interpolation: 'cubic',
  });
  riv.addKeyFrameDouble(pawRotation, {
    frame: 36,
    value: 0,
    interpolation: 'cubic',
  });

  return riv;
}

export function exportPetFixture(): Uint8Array {
  return buildPetFixture().export();
}
