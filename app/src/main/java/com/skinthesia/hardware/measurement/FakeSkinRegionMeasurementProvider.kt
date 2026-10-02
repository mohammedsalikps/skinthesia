package com.skinthesia.hardware.measurement

import com.skinthesia.domain.model.FaceRegion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

private const val MeasurementSteps = 20
private const val StepDelayMillis = 90L

/** A small, occasional chance a reading fails partway through, so the failure/retry UI has something to show. */
private const val SimulatedFailureChance = 0.10f

/**
 * Simulates a probe reading a skin region with realistic timing. Every value here is fabricated
 * locally; nothing is read from real hardware. A real implementation of [SkinRegionMeasurementProvider]
 * can replace this class without any UI change.
 */
class FakeSkinRegionMeasurementProvider : SkinRegionMeasurementProvider {

    override fun measure(region: FaceRegion): Flow<MeasurementProgress> = flow {
        val willFail = Random.nextFloat() < SimulatedFailureChance
        val failAtStep = if (willFail) Random.nextInt(4, MeasurementSteps) else null
        val reason = if (Random.nextFloat() < 0.6f) MeasurementFailureReason.PROBE_DISCONNECTED else MeasurementFailureReason.MEASUREMENT_ERROR

        for (step in 1..MeasurementSteps) {
            delay(StepDelayMillis)
            if (step == failAtStep) {
                throw MeasurementFailedException(reason, "Simulated failure while measuring $region")
            }
            emit(MeasurementProgress(fraction = step.toFloat() / MeasurementSteps))
        }
    }
}
