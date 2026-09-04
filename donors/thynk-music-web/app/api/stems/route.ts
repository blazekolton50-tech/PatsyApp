export async function POST(req: Request) {
  const { projectId } = await req.json();

  // Donor/reference mock only. Production THyNK Music must use a real verified provider.
  const stems = ['vocals', 'drums', 'bass', 'other'].map(type => ({
    type,
    url: `/stems/${projectId}/${type}.wav`,
    isolated: true,
    progress: 100
  }));

  return Response.json({ stems, mock: true });
}