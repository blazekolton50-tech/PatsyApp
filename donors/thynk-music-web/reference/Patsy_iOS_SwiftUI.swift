import SwiftUI

// Reference-only iOS donor implementation for Patsy quick shrink.
// THyNK-IN! Android production remains Kotlin + Jetpack Compose + the native Patsy/Rive boundary.
//
// Asset contract:
// - patsy_shrink_00 ... patsy_shrink_11 are owner-approved transparent Patsy frames.
// - Frame 00 visually represents Big = 300 px / 2 thumbs.
// - Frame 11 visually represents Mini = 150 px / 1 thumb.
// - The frames encode the size change, so this view does not apply a second 1.0 -> 0.5 shrink during playback.
//
// Compile status: reference source only; Xcode compilation is not verified in this repository CI.
struct PatsyView: View {
    let onMissionStart: () -> Void

    @State private var frameIndex = 0
    @State private var showRainbow = false
    @State private var runOff: CGFloat = 0
    @State private var bounce: CGFloat = 0
    @State private var animationTask: Task<Void, Never>?

    init(onMissionStart: @escaping () -> Void = {}) {
        self.onMissionStart = onMissionStart
    }

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            if showRainbow {
                RainbowView(progress: CGFloat(frameIndex) / 11.0)
                    .frame(width: 720, height: 720)
            }

            Image("patsy_shrink_\(String(format: "%02d", frameIndex))")
                .resizable()
                .scaledToFit()
                .frame(width: 300, height: 300)
                .offset(x: runOff, y: bounce)
                .accessibilityLabel("Patsy")

            VStack(spacing: 12) {
                Spacer()

                Button("Shrink & Run to Mission (0.8s quick)") {
                    quickShrinkAndRun()
                }
                .padding(.horizontal, 22)
                .padding(.vertical, 12)
                .background(Color(red: 1.0, green: 0.31, blue: 1.0))
                .foregroundColor(.white)
                .clipShape(Capsule())

                Button("Expand to Big") {
                    expandToBig()
                }
                .padding(.horizontal, 18)
                .padding(.vertical, 10)
                .background(Color.gray.opacity(0.65))
                .foregroundColor(.white)
                .clipShape(Capsule())
                .padding(.bottom, 30)
            }
        }
        .onDisappear {
            animationTask?.cancel()
            animationTask = nil
        }
    }

    private func quickShrinkAndRun() {
        animationTask?.cancel()

        frameIndex = 0
        runOff = 0
        bounce = 0
        showRainbow = true

        animationTask = Task { @MainActor in
            // 12 frames at roughly 66.7 ms each ~= 0.8 seconds.
            for frame in 0..<12 {
                guard !Task.isCancelled else { return }

                frameIndex = frame
                bounce = sin(CGFloat(frame) * 1.2) * 6.0

                if frame < 11 {
                    try? await Task.sleep(nanoseconds: 67_000_000)
                }
            }

            guard !Task.isCancelled else { return }
            showRainbow = false
            bounce = 0

            withAnimation(.easeIn(duration: 0.4)) {
                runOff = 500
            }

            try? await Task.sleep(nanoseconds: 400_000_000)
            guard !Task.isCancelled else { return }
            onMissionStart()
        }
    }

    private func expandToBig() {
        animationTask?.cancel()
        animationTask = nil
        showRainbow = false

        withAnimation(.easeOut(duration: 0.6)) {
            runOff = 0
            bounce = 0
        }

        frameIndex = 0
    }
}

struct RainbowView: View {
    let progress: CGFloat

    var body: some View {
        Canvas { context, canvasSize in
            let colors: [Color] = [.pink, .orange, .yellow, .green, .blue, .purple, .pink]

            for k in 0..<20 {
                let angle = (CGFloat(k * 18) + progress * 200.0) * .pi / 180.0
                let particleProgress = (progress + CGFloat(k) * 0.05)
                    .truncatingRemainder(dividingBy: 1.0)
                let radius = 80.0 + particleProgress * 250.0
                let x = canvasSize.width / 2.0 + CGFloat(cos(Double(angle))) * radius
                let y = canvasSize.height / 2.0
                    + CGFloat(sin(Double(angle))) * radius * 0.8
                    - particleProgress * 100.0
                let particleRadius = 3.0 + particleProgress * 4.0

                let rect = CGRect(
                    x: x - particleRadius,
                    y: y - particleRadius,
                    width: particleRadius * 2.0,
                    height: particleRadius * 2.0
                )

                context.fill(
                    Circle().path(in: rect),
                    with: .color(colors[k % colors.count].opacity(1.0 - particleProgress * 0.5))
                )
            }
        }
    }
}
