package com.skinthesia.domain.model

/** A deterministic, rule-based planning note — never a medical conclusion or a claim of measured change. */
enum class AdaptationInsightKind { CONSISTENCY_LOW, CONSISTENCY_HIGH, RECENT_CHANGE_NOTED }

private val NotableChanges = setOf(
    WeeklyChange.NEW_PRODUCTS,
    WeeklyChange.ROUTINE_CHANGED,
    WeeklyChange.LIFESTYLE_CHANGED,
    WeeklyChange.ENVIRONMENT_CHANGED,
)

/**
 * A presentation-layer view of how the existing [PersonalizedPlan] could evolve. References the
 * plan by its SkinPrint id rather than copying it — never a second SkinPrint, and never another
 * PersonalizedPlan duplicating Step 11's. [insights] are ordered, deterministic notes; this is the
 * seam a future recommendation engine can replace by swapping [buildAdaptivePlan] alone.
 */
data class AdaptivePlan(
    val skinPrintId: String,
    /** True until real longitudinal SkinPrint history exists to adapt against. */
    val isBaselineOnly: Boolean,
    val insights: List<AdaptationInsightKind>,
)

/**
 * Derives a deterministic [AdaptivePlan] from the existing plan, the current SkinPrint history and
 * an optional Weekly Check-in. Real longitudinal data doesn't exist yet, so this never implies a
 * measured improvement or decline — only honest, rule-based planning notes.
 */
fun buildAdaptivePlan(plan: PersonalizedPlan, history: SkinPrintHistory, checkIn: WeeklyCheckIn?): AdaptivePlan {
    val insights = buildList {
        when (checkIn?.routineConsistency) {
            RoutineConsistency.SOMEWHAT_CONSISTENT, RoutineConsistency.STRUGGLED -> add(AdaptationInsightKind.CONSISTENCY_LOW)
            RoutineConsistency.VERY_CONSISTENT, RoutineConsistency.MOSTLY_CONSISTENT -> add(AdaptationInsightKind.CONSISTENCY_HIGH)
            null -> Unit
        }
        if (checkIn != null && checkIn.changes.any { it in NotableChanges }) {
            add(AdaptationInsightKind.RECENT_CHANGE_NOTED)
        }
    }
    return AdaptivePlan(
        skinPrintId = plan.skinPrintId,
        isBaselineOnly = !history.hasComparableHistory(),
        insights = insights,
    )
}
