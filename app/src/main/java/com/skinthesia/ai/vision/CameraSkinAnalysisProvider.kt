package com.skinthesia.ai.vision

import com.skinthesia.domain.model.IndicatorLevel
import com.skinthesia.domain.model.VisualIndicator
import kotlinx.coroutines.flow.Flow

/** One frame of a visual analysis in progress. [result] is populated only once [fraction] reaches 1. */
data class VisualAnalysisProgress(
    val fraction: Float,
    val result: Map<VisualIndicator, IndicatorLevel>? = null,
)

/** Thrown by [CameraSkinAnalysisProvider.analyze] when the analysis cannot be completed. */
class CameraAnalysisFailedException(message: String) : Exception(message)

/**
 * Boundary between the UI and whatever actually analyzes a selfie's visible skin characteristics.
 * [FakeCameraSkinAnalysisProvider] simulates a result locally; a real computer-vision/ML
 * implementation can satisfy this same contract without any screen change. Every result is a
 * visual estimate only — never a medical diagnosis or a scientifically validated measurement.
 */
interface CameraSkinAnalysisProvider {
    /** Streams progress while analyzing [photoId], completing with a populated result, or throws on failure. */
    fun analyze(photoId: String): Flow<VisualAnalysisProgress>
}
