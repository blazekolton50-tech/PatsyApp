package com.patsy.app.patsy.rig.rive

import com.patsy.app.patsy.rig.PatsyRigContractV1
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigStatus
import com.patsy.app.patsy.rig.PatsyRigValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PatsyRiveRuntimeAdapterTest {
    @Test
    fun pendingValuesKeepOnlyLatestValuePerPropertyUntilAttach() {
        val adapter = PatsyRiveRuntimeAdapter()
        val writes = mutableListOf<PatsyRigMutation>()
        val writer = PatsyRiveMutationWriter { writes += it }

        adapter.apply(
            listOf(
                PatsyRigMutation(PatsyRigContractV1.Property.HEAD_LOOK_X, PatsyRigValue.Number(-0.7f))
            )
        )
        adapter.apply(
            listOf(
                PatsyRigMutation(PatsyRigContractV1.Property.HEAD_LOOK_X, PatsyRigValue.Number(0.8f))
            )
        )

        adapter.attach(writer)

        assertEquals(1, writes.size)
        assertEquals(PatsyRigContractV1.Property.HEAD_LOOK_X, writes.single().propertyPath)
        assertEquals(PatsyRigValue.Number(0.8f), writes.single().value)
        assertEquals(PatsyRigStatus.Ready, adapter.status)
    }

    @Test
    fun statusTransitionsRemainFailSafe() {
        val adapter = PatsyRiveRuntimeAdapter()

        adapter.markLoading()
        assertEquals(PatsyRigStatus.Loading, adapter.status)

        adapter.attach(PatsyRiveMutationWriter { })
        assertEquals(PatsyRigStatus.Ready, adapter.status)

        adapter.markInvalid(setOf("property:motion/mode"))
        assertIs<PatsyRigStatus.InvalidAsset>(adapter.status)

        adapter.markFailed("safe failure")
        assertEquals(PatsyRigStatus.Failed("safe failure"), adapter.status)

        adapter.detach()
        assertEquals(PatsyRigStatus.Detached, adapter.status)
    }

    @Test
    fun writerFailureDropsActiveWriterAndReportsFailed() {
        val adapter = PatsyRiveRuntimeAdapter()
        adapter.attach(PatsyRiveMutationWriter { error("boom") })

        adapter.apply(
            listOf(
                PatsyRigMutation(PatsyRigContractV1.Property.TAIL_ENERGY, PatsyRigValue.Number(0.5f))
            )
        )

        assertIs<PatsyRigStatus.Failed>(adapter.status)
    }

    @Test
    fun unknownPropertyFailsClosedInsteadOfWriting() {
        val adapter = PatsyRiveRuntimeAdapter()
        val writes = mutableListOf<PatsyRigMutation>()
        adapter.attach(PatsyRiveMutationWriter { writes += it })

        adapter.apply(listOf(PatsyRigMutation("unknown/path", PatsyRigValue.Number(1f))))

        assertEquals(emptyList(), writes)
        assertIs<PatsyRigStatus.Failed>(adapter.status)
    }
}
