export async function POST(req: Request) {
  const { projectId, format } = await req.json();

  // Donor/reference mock only. Production must not report export success without a real mixdown.
  return Response.json({
    url: `/exports/${projectId}.${format || 'wav'}`,
    lufs: -14.2,
    peak: -0.8,
    ready: false,
    mock: true
  });
}