package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigViseme

/**
 * Provider-neutral presentation states for the visible Patsy companion.
 *
 * AI/security policy remains upstream. This layer only converts an already-authorised assistant
 * lifecycle or reaction into typed companion intents; it never decides whether a request is safe.
 */
sealed interface PatsyAiPerformance {
    data object Idle : PatsyAiPerformance
    data object Thinking : PatsyAiPerformance
    data object Listening : PatsyAiPerformance
    data object Happy : PatsyAiPerformance
    data object Judgy : PatsyAiPerformance
    data object Concerned : PatsyAiPerformance
    data object Sleeping : PatsyAiPerformance

    data class Speaking(
        val viseme: PatsyRigViseme,
        val visemeIntensity: Float,
        val speechEnergy: Float,
    ) : PatsyAiPerformance
}

/**
 * Native bridge replacing the donor React/Lottie string-state examples.
 *
 * Speech amplitude is kept independent from body/expression state so the authored Rive rig can
 * eventually combine talking + gaze + ears + tail + body motion instead of swapping timelines.
 */
object PatsyAiCompanionBridge {
    fun toIntent(performance: PatsyAiPerformance): PatsyCompanionIntent = when (performance) {
        PatsyAiPerformance.Idle -> PatsyCompanionIntent.Settle
        PatsyAiPerformance.Thinking -> PatsyCompanionIntent.Think
        PatsyAiPerformance.Listening -> PatsyCompanionIntent.Listen
        PatsyAiPerformance.Happy -> PatsyCompanionIntent.React(PatsyCompanionReaction.HAPPY)
        PatsyAiPerformance.Judgy -> PatsyCompanionIntent.React(PatsyCompanionReaction.JUDGY)
        PatsyAiPerformance.Concerned -> PatsyCompanionIntent.React(PatsyCompanionReaction.CONCERNED)
        PatsyAiPerformance.Sleeping -> PatsyCompanionIntent.Sleep
        is PatsyAiPerformance.Speaking -> PatsyCompanionIntent.Speak(
            viseme = performance.viseme,
            visemeIntensity = performance.visemeIntensity,
            speechEnergy = performance.speechEnergy,
        )
    }

    /**
     * Converts a real 0..1 TTS/audio level into the current Rive speech contract.
     * A viseme can be supplied when phoneme timing is available; A is a safe generic open-mouth
     * shape for amplitude-only playback and is not a prerecorded talk animation.
     */
    fun speakingFromAmplitude(
        level: Float,
        viseme: PatsyRigViseme = PatsyRigViseme.A,
    ): PatsyAiPerformance.Speaking {
        val amplitude = level.coerceIn(0f, 1f)
        return PatsyAiPerformance.Speaking(
            viseme = viseme,
            visemeIntensity = amplitude,
            speechEnergy = amplitude,
        )
    }
}
