package com.skinthesia.domain.model

/** How a single quality dimension reads, worst to best so [PhotoQualityCheck.overall] can take the minimum. */
enum class QualityLevel { POOR, FAIR, GOOD }

/**
 * The result of checking a captured selfie before it's used. Every dimension is simulated for
 * now; a real analyzer can populate the same shape later without changing the screens.
 */
data class PhotoQualityCheck(
    val lighting: QualityLevel,
    val facePositioning: QualityLevel,
    val clarity: QualityLevel,
) {
    val overall: QualityLevel get() = minOf(lighting, facePositioning, clarity)
    val isReadyToUse: Boolean get() = overall != QualityLevel.POOR
}
