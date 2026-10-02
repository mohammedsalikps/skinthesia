package com.skinthesia.ai.combined

import com.skinthesia.domain.model.CameraSkinAnalysis
import com.skinthesia.domain.model.LifestyleProfile
import com.skinthesia.domain.model.MeasurementSession
import com.skinthesia.domain.model.SensorAnalysisSession
import com.skinthesia.domain.model.SkinGoal
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinQuestionnaire
import kotlinx.coroutines.flow.Flow

/**
 * Everything the combined analysis needs, gathered from screens built in earlier steps. Every
 * field here is a reference to (or a copy of the small value already held by) an existing model —
 * nothing is duplicated or reinvented. Camera/sensor/measurement data may be absent if those steps
 * were skipped or are unreachable; the provider does what it reasonably can with what's available.
 */
data class CombinedAnalysisInput(
    val profileId: String,
    val cameraAnalysis: CameraSkinAnalysis?,
    val sensorAnalysis: SensorAnalysisSession?,
    val measurementSession: MeasurementSession?,
    val questionnaire: SkinQuestionnaire,
    val lifestyle: LifestyleProfile,
    val goals: Set<SkinGoal>,
)

/** One frame of the combination process. [result] is the finished [SkinPrint], populated only once [fraction] reaches 1. */
data class CombinedAnalysisProgress(
    val fraction: Float,
    val result: SkinPrint? = null,
)

/** Thrown by [CombinedSkinAnalysisProvider.analyze] when a SkinPrint cannot be produced. */
class CombinedAnalysisFailedException(message: String) : Exception(message)

/**
 * Boundary between the UI and whatever actually combines camera, sensor and user context into a
 * SkinPrint. [FakeCombinedSkinAnalysisProvider] simulates a result locally from the real inputs
 * already gathered elsewhere in the app; a real implementation can satisfy this same contract
 * without any screen change. Never a medical diagnosis or a clinically validated conclusion.
 */
interface CombinedSkinAnalysisProvider {
    fun analyze(input: CombinedAnalysisInput): Flow<CombinedAnalysisProgress>
}
