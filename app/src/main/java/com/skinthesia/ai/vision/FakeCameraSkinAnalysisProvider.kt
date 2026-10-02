package com.skinthesia.ai.vision

import com.skinthesia.domain.model.IndicatorLevel
import com.skinthesia.domain.model.VisualIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

private const val AnalysisSteps = 24
private const val StepDelayMillis = 70L

/** A small, occasional chance the analysis fails partway through, so the failure/retry UI has something to show. */
private const val SimulatedFailureChance = 0.08f

/**
 * Simulates a computer-vision analysis of a selfie with realistic timing. Every value here is
 * fabricated locally, seeded from [photoId] so the same photo always yields the same qualitative
 * result; nothing is a real analysis of any image's pixels. A real computer-vision/ML
 * implementation of [CameraSkinAnalysisProvider] can replace this class without any UI change.
 */
class FakeCameraSkinAnalysisProvider : CameraSkinAnalysisProvider {

    override fun analyze(photoId: String): Flow<VisualAnalysisProgress> = flow {
        val willFail = Random.nextFloat() < SimulatedFailureChance
        val failAtStep = if (willFail) Random.nextInt(6, AnalysisSteps) else null

        for (step in 1..AnalysisSteps) {
            delay(StepDelayMillis)
            if (step == failAtStep) {
                throw CameraAnalysisFailedException("Simulated analysis failure for photo $photoId")
            }
            val fraction = step.toFloat() / AnalysisSteps
            val result = if (step == AnalysisSteps) simulatedResult(photoId) else null
            emit(VisualAnalysisProgress(fraction = fraction, result = result))
        }
    }

    private fun simulatedResult(photoId: String): Map<VisualIndicator, IndicatorLevel> {
        val random = Random(photoId.hashCode().toLong())
        return VisualIndicator.entries.associateWith { IndicatorLevel.entries[random.nextInt(IndicatorLevel.entries.size)] }
    }
}
