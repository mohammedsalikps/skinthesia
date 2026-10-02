package com.skinthesia.domain.model

/**
 * A single sensor reading. [channelName] and [unit] are free-form rather than a fixed enum, so the
 * set of channels a probe reports can grow or change once real hardware specifications exist,
 * without changing this model. [isSimulated] must be surfaced in the UI wherever a value appears.
 */
data class SensorChannelReading(
    val channelName: String,
    val value: Float,
    val unit: String,
    val timestampMillis: Long,
    val isSimulated: Boolean,
)

/** One face region's sensor channels within a sensor analysis session. */
data class RegionSensorReadings(
    val region: FaceRegion,
    val channels: List<SensorChannelReading> = emptyList(),
)

enum class SensorAnalysisStatus { ANALYZING, COMPLETE, FAILED }

/**
 * The result of reading sensor data for a completed [MeasurementSession]. Channel names, units and
 * values are placeholders until the real Skinthesia Probe's sensor specification is known — see
 * `hardware.sensor` for the (currently simulated) source of these readings.
 */
data class SensorAnalysisSession(
    val id: String,
    val measurementSessionId: String,
    val status: SensorAnalysisStatus,
    val startedAtMillis: Long,
    val completedAtMillis: Long? = null,
    val regions: Map<FaceRegion, RegionSensorReadings> = emptyMap(),
)
