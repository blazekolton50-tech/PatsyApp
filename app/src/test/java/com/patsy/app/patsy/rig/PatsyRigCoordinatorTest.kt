package com.patsy.app.patsy.rig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatsyRigCoordinatorTest {
    private class RecordingRuntime : PatsyRigRuntimePort {
        override var status: PatsyRigStatus = PatsyRigStatus.Ready
        val batches = mutableListOf<List<PatsyRigMutation>>()
        override fun apply(mutations: List<PatsyRigMutation>) {
            batches += mutations
        }
    }

    @Test
    fun renderWritesNormalizedCoreAndSpeechProperties() {
        val runtime = RecordingRuntime()
        val coordinator = PatsyRigCoordinator(runtime)

        coordinator.render(
            PatsyRigPose(
                motion = PatsyRigMotion.WALK,
                motionSpeed = 2f,
                stageX = -1f,
                stageY = 2f,
                stageScale = 3f,
                expression = PatsyRigExpression.CURIOUS,
                expressionIntensity = 2f,
                talking = true,
                viseme = PatsyRigViseme.O,
                visemeIntensity = 2f,
                speechEnergy = -1f,
            )
        )

        val values = runtime.batches.single().associate { it.propertyPath to it.value }
        assertEquals(PatsyRigValue.Enum("walk"), values[PatsyRigContractV1.Property.MOTION_MODE])
        assertEquals(PatsyRigValue.Number(1f), values[PatsyRigContractV1.Property.MOTION_SPEED])
        assertEquals(PatsyRigValue.Number(0f), values[PatsyRigContractV1.Property.STAGE_X])
        assertEquals(PatsyRigValue.Number(1f), values[PatsyRigContractV1.Property.STAGE_Y])
        assertEquals(PatsyRigValue.Number(1.4f), values[PatsyRigContractV1.Property.STAGE_SCALE])
        assertEquals(PatsyRigValue.Enum("curious"), values[PatsyRigContractV1.Property.FACE_EXPRESSION])
        assertEquals(PatsyRigValue.Number(1f), values[PatsyRigContractV1.Property.FACE_EXPRESSION_INTENSITY])
        assertEquals(PatsyRigValue.Boolean(true), values[PatsyRigContractV1.Property.SPEECH_TALKING])
        assertEquals(PatsyRigValue.Enum("o"), values[PatsyRigContractV1.Property.SPEECH_VISEME])
        assertEquals(PatsyRigValue.Number(1f), values[PatsyRigContractV1.Property.SPEECH_VISEME_INTENSITY])
        assertEquals(PatsyRigValue.Number(0f), values[PatsyRigContractV1.Property.SPEECH_ENERGY])
    }

    @Test
    fun reducedMotionSuppressesTravelAndDampsSecondaryMotion() {
        val runtime = RecordingRuntime()
        val coordinator = PatsyRigCoordinator(runtime)

        coordinator.render(
            PatsyRigPose(
                motion = PatsyRigMotion.WALK,
                motionSpeed = 1f,
                stageScale = 1.4f,
                tailEnergy = 1f,
                leftEarDrive = 1f,
                rightEarDrive = -1f,
                reducedMotion = true,
            )
        )

        val values = runtime.batches.single().associate { it.propertyPath to it.value }
        assertEquals(PatsyRigValue.Enum("idle"), values[PatsyRigContractV1.Property.MOTION_MODE])
        assertEquals(PatsyRigValue.Number(0f), values[PatsyRigContractV1.Property.MOTION_SPEED])
        assertEquals(PatsyRigValue.Number(1.1f), values[PatsyRigContractV1.Property.STAGE_SCALE])
        assertEquals(PatsyRigValue.Number(0.2f), values[PatsyRigContractV1.Property.EAR_LEFT_DRIVE])
        assertEquals(PatsyRigValue.Number(-0.2f), values[PatsyRigContractV1.Property.EAR_RIGHT_DRIVE])
        assertEquals(PatsyRigValue.Number(0.2f), values[PatsyRigContractV1.Property.TAIL_ENERGY])
    }

    @Test
    fun oneShotsAndBlinkUseIncreasingSequences() {
        val runtime = RecordingRuntime()
        val coordinator = PatsyRigCoordinator(runtime)

        coordinator.retriggerAction(PatsyRigMotion.POINT)
        coordinator.retriggerAction(PatsyRigMotion.POINT)
        coordinator.blink()
        coordinator.blink()

        val actionSequences = runtime.batches
            .flatMap { it }
            .filter { it.propertyPath == PatsyRigContractV1.Property.MOTION_ACTION_SEQUENCE }
            .map { (it.value as PatsyRigValue.Number).value }
        val blinkSequences = runtime.batches
            .flatMap { it }
            .filter { it.propertyPath == PatsyRigContractV1.Property.FACE_BLINK_SEQUENCE }
            .map { (it.value as PatsyRigValue.Number).value }

        assertEquals(2, actionSequences.size)
        assertEquals(2, blinkSequences.size)
        assertTrue(actionSequences[1] > actionSequences[0])
        assertTrue(blinkSequences[1] > blinkSequences[0])
    }
}
