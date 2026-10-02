package com.skinthesia.hardware.measurement

import com.skinthesia.domain.model.FaceRegion
import kotlinx.coroutines.flow.Flow

/** One frame of a region measurement in progress. */
data class MeasurementProgress(
    /** 0f..1f */
    val fraction: Float,
)

/** Why a reading could not be completed. */
enum class MeasurementFailureReason { PROBE_DISCONNECTED, MEASUREMENT_ERROR }

/** Thrown by [SkinRegionMeasurementProvider.measure] when a reading cannot be completed. */
class MeasurementFailedException(val reason: MeasurementFailureReason, message: String) : Exception(message)

/**
 * Boundary between the UI and whatever actually reads a skin region. [FakeSkinRegionMeasurementProvider]
 * simulates a reading with realistic timing; a real implementation backed by actual probe sensor
 * data can satisfy this same contract without any screen change. No sensor values (pH, hydration,
 * temperature, or similar) are modelled yet, since the real hardware's capabilities aren't known.
 */
interface SkinRegionMeasurementProvider {
    /** Streams progress while measuring [region], then completes normally, or throws on failure. */
    fun measure(region: FaceRegion): Flow<MeasurementProgress>
}
