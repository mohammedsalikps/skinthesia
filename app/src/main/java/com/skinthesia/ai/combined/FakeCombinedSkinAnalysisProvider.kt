package com.skinthesia.ai.combined

import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinPrintComponent
import com.skinthesia.domain.model.SkinPrintSources
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt
import kotlin.random.Random
import java.util.UUID

private const val AnalysisSteps = 20
private const val StepDelayMillis = 80L

/** A small, occasional chance combining fails partway through, so the failure/retry UI has something to show. */
private const val SimulatedFailureChance = 0.05f

/**
 * Simulates combining camera, sensor and user context into one SkinPrint, with realistic timing.
 * The composite and component scores are fabricated locally — deterministic per profile so
 * repeated runs for the same person feel stable — and are never a real clinical computation. A
 * real implementation of [CombinedSkinAnalysisProvider] can replace this class without any UI change.
 */
class FakeCombinedSkinAnalysisProvider : CombinedSkinAnalysisProvider {

    override fun analyze(input: CombinedAnalysisInput): Flow<CombinedAnalysisProgress> = flow {
        val willFail = Random.nextFloat() < SimulatedFailureChance
        val failAtStep = if (willFail) Random.nextInt(8, AnalysisSteps) else null

        for (step in 1..AnalysisSteps) {
            delay(StepDelayMillis)
            if (step == failAtStep) throw CombinedAnalysisFailedException("Simulated combined analysis failure")
            val fraction = step.toFloat() / AnalysisSteps
            val result = if (step == AnalysisSteps) buildSkinPrint(input) else null
            emit(CombinedAnalysisProgress(fraction = fraction, result = result))
        }
    }

    private fun buildSkinPrint(input: CombinedAnalysisInput): SkinPrint {
        val random = Random(input.profileId.hashCode().toLong())
        val componentScores = SkinPrintComponent.entries.associateWith { random.nextInt(45, 96) }
        val composite = componentScores.values.average().roundToInt()
        val now = System.currentTimeMillis()
        return SkinPrint(
            id = UUID.randomUUID().toString(),
            profileId = input.profileId,
            createdAtMillis = now,
            updatedAtMillis = now,
            version = 1,
            compositeScore = composite,
            componentScores = componentScores,
            sources = SkinPrintSources(
                cameraAnalysisId = input.cameraAnalysis?.id,
                sensorAnalysisId = input.sensorAnalysis?.id,
                measurementSessionId = input.measurementSession?.id,
                goalIds = input.goals.toList(),
            ),
            isSimulated = true,
        )
    }
}
