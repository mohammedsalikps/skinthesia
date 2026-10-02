package com.skinthesia.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

/** How the person's skin felt over the past week — a self-report, never a measurement. */
enum class SkinFeeling { BETTER, ABOUT_THE_SAME, MORE_CHALLENGING }

/** How closely the person kept up with their routine this week. */
enum class RoutineConsistency { VERY_CONSISTENT, MOSTLY_CONSISTENT, SOMEWHAT_CONSISTENT, STRUGGLED }

/** Something that changed this week that could plausibly affect the skin picture. */
enum class WeeklyChange { NOTHING_SIGNIFICANT, NEW_PRODUCTS, ROUTINE_CHANGED, LIFESTYLE_CHANGED, ENVIRONMENT_CHANGED }

/**
 * One completed Weekly Check-in. Kept small and self-contained so the repository can persist a
 * growing list of these later without any shape changes.
 */
@Serializable
data class WeeklyCheckIn(
    val id: String = UUID.randomUUID().toString(),
    val timestampMillis: Long,
    val skinFeeling: SkinFeeling,
    val routineConsistency: RoutineConsistency,
    val changes: Set<WeeklyChange>,
    val note: String? = null,
)
