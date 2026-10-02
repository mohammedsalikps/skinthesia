package com.skinthesia.hardware.sensor

import com.skinthesia.domain.model.FaceRegion
import com.skinthesia.domain.model.MeasurementSession
import com.skinthesia.domain.model.RegionSensorReadings
import com.skinthesia.domain.model.SensorAnalysisSession
import com.skinthesia.domain.model.SensorAnalysisStatus
import com.skinthesia.domain.model.SensorChannelReading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import java.util.UUID

private const val AnalysisSteps = 16
private const val StepDelayMillis = 60L

/** A small, occasional chance the reading fails partway through, so the failure/retry UI has something to show. */
private const val SimulatedFailureChance = 0.08f

/** Deliberately generic: real channel names depend on hardware specifications that don't exist yet. */
private val SimulatedChannelNames = listOf("Channel A", "Channel B", "Channel C")

/** Arbitrary units: a real value with no calibrated physical unit assigned to it yet. */
private const val SimulatedUnit = "a.u."

/**
 * Simulates reading sensor channel data for a completed measurement session, with realistic
 * timing. Every value here is fabricated locally and marked [SensorChannelReading.isSimulated];
 * nothing here reflects any real Skinthesia Probe sensor capability. A real implementation of
 * [SensorAnalysisProvider] can replace this class without any UI change.
 */
class FakeSensorAnalysisProvider : SensorAnalysisProvider {

    override fun analyze(measurementSession: MeasurementSession): Flow<SensorAnalysisProgress> = flow {
        val startedAt = System.currentTimeMillis()
        val willFail = Random.nextFloat() < SimulatedFailureChance
        val failAtStep = if (willFail) Random.nextInt(4, AnalysisSteps) else null

        for (step in 1..AnalysisSteps) {
            delay(StepDelayMillis)
            if (step == failAtStep) throw SensorAnalysisFailedException("Simulated sensor analysis failure")
            val fraction = step.toFloat() / AnalysisSteps
            val result = if (step == AnalysisSteps) buildResult(measurementSession, startedAt) else null
            emit(SensorAnalysisProgress(fraction = fraction, result = result))
        }
    }

    private fun buildResult(measurementSession: MeasurementSession, startedAt: Long): SensorAnalysisSession {
        val now = System.currentTimeMillis()
        val random = Random(measurementSession.id.hashCode().toLong())
        val regions = FaceRegion.entries.associateWith { region ->
            RegionSensorReadings(
                region = region,
                channels = SimulatedChannelNames.map { name ->
                    SensorChannelReading(
                        channelName = name,
                        value = random.nextInt(0, 101).toFloat(),
                        unit = SimulatedUnit,
                        timestampMillis = now,
                        isSimulated = true,
                    )
                },
            )
        }
        return SensorAnalysisSession(
            id = UUID.randomUUID().toString(),
            measurementSessionId = measurementSession.id,
            status = SensorAnalysisStatus.COMPLETE,
            startedAtMillis = startedAt,
            completedAtMillis = now,
            regions = regions,
        )
    }
}
