package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigExpression
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PatsyCompositeUnsupportedSemanticsTest {
    @Test
    fun unsupportedV1ActionsAreReportedInsteadOfSilentlyClaimed() {
        val peek = PatsyCompositeCommandMapper.map(
            PatsyCompositeCommand(actionName = "peek")
        )
        val coverEyes = PatsyCompositeCommandMapper.map(
            PatsyCompositeCommand(actionName = "covereyes")
        )

        assertNull(peek.oneShotAction)
        assertTrue("action:peek" in peek.unsupportedSemantics)
        assertNull(coverEyes.oneShotAction)
        assertTrue("action:covereyes" in coverEyes.unsupportedSemantics)
    }

    @Test
    fun unsupportedV1ExpressionsAreReportedWithNeutralFallback() {
        val focused = PatsyCompositeCommandMapper.map(
            PatsyCompositeCommand(expressionPreset = "focused", expressionIntensity = 0.9f)
        )
        val shy = PatsyCompositeCommandMapper.map(
            PatsyCompositeCommand(expressionPreset = "shy", expressionIntensity = 0.6f)
        )

        assertEquals(PatsyRigExpression.NEUTRAL, focused.pose.expression)
        assertTrue("expression:focused" in focused.unsupportedSemantics)
        assertEquals(PatsyRigExpression.NEUTRAL, shy.pose.expression)
        assertTrue("expression:shy" in shy.unsupportedSemantics)
    }
}
