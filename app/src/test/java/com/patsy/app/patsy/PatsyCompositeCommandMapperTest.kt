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
        assertEquals(PatsyRigMotion.WAVE, plan.oneShotAction)
        assertEquals(PatsyRigExpression.EXCITED, plan.pose.expression)
        assertEquals(1f, plan.pose.expressionIntensity)
        assertTrue(plan.pose.talking)
        assertEquals("Heeeyy, you're back!", plan.speechText)
        assertTrue(plan.unsupportedSemantics.isEmpty())
    }
}
