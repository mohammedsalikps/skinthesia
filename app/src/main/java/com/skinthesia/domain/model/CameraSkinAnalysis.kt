package com.skinthesia.domain.model

/** Visual characteristics the (simulated) camera analysis reports on. */
enum class VisualIndicator {
    ACNE_APPEARANCE,
    PIGMENTATION,
    UNEVEN_TONE,
    TEXTURE,
    PORE_APPEARANCE,
    REDNESS,
    DARK_CIRCLES,
    FINE_LINES,
}

/** A qualitative reading for one indicator — never a fabricated numeric or clinical measurement. */
enum class IndicatorLevel { MINIMAL, MILD, MODERATE, NOTICEABLE }

enum class CameraAnalysisStatus { PREPARING, ANALYZING, COMPLETE, FAILED }

/**
 * The result of a (simulated) visual analysis of one selfie. This is a visual-estimate summary,
 * never a medical diagnosis or a scientifically validated measurement. Raw image bytes are never
 * stored here — only [photoId], a reference to the [SelfiePhoto] that was analyzed.
 */
data class CameraSkinAnalysis(
    val id: String,
    val photoId: String,
    val status: CameraAnalysisStatus,
    val startedAtMillis: Long,
    val completedAtMillis: Long? = null,
    val indicators: Map<VisualIndicator, IndicatorLevel> = emptyMap(),
)
