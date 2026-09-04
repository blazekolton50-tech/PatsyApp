import { NextRequest, NextResponse } from 'next/server';
import { db } from '@/lib/db';

export async function GET() {
  const projects = await db.project.findMany({ orderBy: { updatedAt: 'desc' } });
  return NextResponse.json(projects);
}

export async function POST(req: NextRequest) {
  const body = await req.json();
  const project = await db.project.create({
    data: {
      name: body.name || `THYNK_${Date.now()}.thynk`,
      bpm: body.bpm || 124,
      key: body.key || 'A Minor',
      clips: body.clips || [],
      mixer: body.mixer || { ch1: 68, ch2: 72, ch3: 65, ch4: 70 },
      effects: body.effects || { reverb: 72, echo: 45, delay: 30, flanger: 60 }
    }
  });
  return NextResponse.json(project);
}

export async function PUT(req: NextRequest) {
  const body = await req.json();
  const project = await db.project.update({
    where: { id: body.id },
    data: {
      clips: body.clips,
      mixer: body.mixer,
      effects: body.effects,
      bpm: body.bpm,
      updatedAt: new Date()
    }
  });
  return NextResponse.json(project);
}