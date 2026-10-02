package com.skinthesia.domain.model

/** The three probe contact points captured during one measurement session. */
enum class FaceRegion { FOREHEAD, LEFT_CHEEK, RIGHT_CHEEK }

enum class MeasurementStatus { PENDING, IN_PROGRESS, CAPTURED, FAILED }

/**
 * One region's reading within a measurement session. The reading's actual value is not modelled
 * yet — real sensor capabilities depend on hardware that doesn't exist; see
 * `hardware.measurement` for the (currently simulated) source of readings.
 */
data class RegionMeasurement(
    val sessionId: String,
    val region: FaceRegion,
    val status: MeasurementStatus,
    val capturedAtMillis: Long? = null,
    val deviceId: String? = null,
)

/**
 * Groups the three region measurements taken in one sitting. [profileId] stands in for a real
 * account/profile identifier until accounts exist; today it is generated once per session.
 */
data class MeasurementSession(
    val id: String,
    val profileId: String,
    val startedAtMillis: Long,
    val regions: Map<FaceRegion, RegionMeasurement> = emptyMap(),
) {
    val isComplete: Boolean get() = FaceRegion.entries.all { regions[it]?.status == MeasurementStatus.CAPTURED }
}
