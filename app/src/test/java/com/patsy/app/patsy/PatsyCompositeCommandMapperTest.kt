package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatsyCompositeCommandMapperTest {
    @Test
    fun supportedGreetingMapsToV1BodyActionEmotionAndSpeech() {
        val plan = PatsyCompositeCommandMapper.map(
            PatsyCompositeCommand(
                bodyState = "sitting",
                bodyTransitionSpeed = 2f,
                actionName = "wave",
                actionDurationSeconds = 1.8f,
                expressionPreset = "happy",
                expressionIntensity = 1.4f,
                attentionType = "user",
                attentionTargetName = "user",
                speechText = "Heeeyy, you're back!",
                audioClipId = "",
            )
        )

        assertEquals(PatsyRigMotion.SIT, plan.pose.motion)
        assertEquals(1f, plan.pose.motionSpeed)
        assertEquals(PatsyRigMotion.WAVE, plan.oneShotAction)
        assertEquals(PatsyRigExpression.EXCITED, plan.pose.expression)
        assertEquals(1f, plan.pose.expressionIntensity)
        assertTrue(plan.pose.talking)
        assertEquals("Heeeyy, you're back!", plan.speechText)
        assertTrue(plan.unsupportedSemantics.isEmpty())
    }

    @Test
    fun worldTargetAttentionMapsClampedCoordinatesIntoV1HeadControls() {
        val plan = PatsyCompositeCommandMapper.map(
            PatsyCompositeCommand(
                bodyState = "walking",
                bodyTransitionSpeed = 0.7f,
                attentionType = "worldTarget",
                attentionTargetName = "timeline-scrubber",
                attentionTarget = PatsyCompanionTarget(
                    normalizedX = 1.4f,
                    normalizedY = -0.2f,
                ),
            )
        )

        assertEquals(PatsyRigMotion.WALK, plan.pose.motion)
        assertEquals(0.7f, plan.pose.motionSpeed)
        assertEquals(1f, plan.pose.lookX)
        assertEquals(-1f, plan.pose.lookY)
        assertTrue(plan.pose.headTilt < 0f)
        assertTrue(plan.unsupportedSemantics.isEmpty())
    }

    @Test
    fun targetDependentAttentionWithoutCoordinatesIsDiagnosedTruthfully() {
        val plan = PatsyCompositeCommandMapper.map(
            PatsyCompositeCommand(
                attentionType = "uiControl",
                attentionTargetName = "export-button",
            )
        )

        assertTrue("attention:uicontrol:missing_target" in plan.unsupportedSemantics)
        assertEquals(0f, plan.pose.lookX)
        assertEquals(0f, plan.pose.lookY)
        assertEquals(0f, plan.pose.headTilt)
    }
}
