package com.skinthesia.domain.model

import kotlinx.serialization.Serializable

enum class SleepQuality { POOR, FAIR, GOOD, EXCELLENT }
enum class StressLevel { LOW, MODERATE, HIGH }
enum class SunExposure { MINIMAL, MODERATE, FREQUENT }
enum class OutdoorActivityFrequency { RARELY, SOMETIMES, OFTEN, DAILY }
enum class ActivityLevel { SEDENTARY, LIGHT, MODERATE, ACTIVE }
enum class SmokingStatus { NEVER, FORMER, OCCASIONAL, REGULAR }
enum class DietPattern { BALANCED, PLANT_BASED, PROTEIN_FOCUSED, IRREGULAR, PREFER_NOT_TO_SAY }

/**
 * Everyday context that can help personalise recommendations. Every field is optional and
 * self-reported; none of it is used to diagnose or claim a medical condition.
 */
@Serializable
data class LifestyleProfile(
    val sleepQuality: SleepQuality? = null,
    val sleepDurationHours: Int = DefaultSleepHours,
    val waterIntakeGlasses: Int = DefaultWaterGlasses,
    val stressLevel: StressLevel? = null,
    val sunExposure: SunExposure? = null,
    val outdoorActivity: OutdoorActivityFrequency? = null,
    val activityLevel: ActivityLevel? = null,
    val smokingStatus: SmokingStatus? = null,
    val dietPattern: DietPattern? = null,
) {
    companion object {
        const val DefaultSleepHours = 7
        const val DefaultWaterGlasses = 6
        val SleepHoursRange = 4f..10f
        val WaterGlassesRange = 2f..12f
    }
}
