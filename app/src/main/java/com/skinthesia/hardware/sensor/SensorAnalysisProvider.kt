package com.skinthesia.hardware.sensor

import com.skinthesia.domain.model.MeasurementSession
import com.skinthesia.domain.model.SensorAnalysisSession
import kotlinx.coroutines.flow.Flow

/** One frame of a sensor analysis in progress. [result] is populated only once [fraction] reaches 1. */
data class SensorAnalysisProgress(
    val fraction: Float,
    val result: SensorAnalysisSession? = null,
)

/** Thrown by [SensorAnalysisProvider.analyze] when readings cannot be produced. */
class SensorAnalysisFailedException(message: String) : Exception(message)

/**
 * Boundary between the UI and whatever actually reads sensor channel data for a completed
 * [MeasurementSession]. [FakeSensorAnalysisProvider] simulates development-only channel values; a
 * real implementation backed by the Skinthesia Probe's eventual sensor specification can satisfy
 * this same contract without any screen change. No specific sensor type (pH, hydration,
 * temperature, or similar) is assumed anywhere in this abstraction.
 */
interface SensorAnalysisProvider {
    /** Streams progress while reading sensor data for [measurementSession], then completes, or throws on failure. */
    fun analyze(measurementSession: MeasurementSession): Flow<SensorAnalysisProgress>
}
