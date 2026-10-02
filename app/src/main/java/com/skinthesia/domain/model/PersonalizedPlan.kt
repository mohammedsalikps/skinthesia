package com.skinthesia.domain.model

import kotlinx.serialization.Serializable

enum class RoutineTime { MORNING, EVENING }

/** A generic skincare category — never a specific medical treatment or branded product. */
enum class ProductCategory { CLEANSER, HYDRATING_SERUM, MOISTURIZER, SUNSCREEN, TARGETED_TREATMENT }

/** One step in a routine. [category] is a generic product category, not a specific product. */
@Serializable
data class RoutineStep(
    val order: Int,
    val time: RoutineTime,
    val category: ProductCategory,
    /** Which priority this step addresses, for the "Target" step only; other steps leave this null. */
    val targetsFocusArea: VisualIndicator? = null,
    /** True once the person has marked this step done. Persisted centrally so it survives navigation and app restart. */
    val isCompleted: Boolean = false,
)

/** A product category suggestion tied to why it was chosen and where it fits. */
@Serializable
data class ProductCategoryRecommendation(
    val category: ProductCategory,
    val supportsFocusArea: VisualIndicator? = null,
    val placement: RoutineTime,
    val isAddedToRoutine: Boolean = false,
)

/**
 * A deterministic, presentation-layer plan derived from the profile, goal and analysis data
 * already produced elsewhere — never a new analysis of its own. References existing domain
 * concepts (by ID or value) rather than copying them, so a real recommendation engine can replace
 * [buildPersonalizedPlan] later without changing this shape.
 */
@Serializable
data class PersonalizedPlan(
    val skinPrintId: String,
    val priorityFocusAreas: List<VisualIndicator>,
    val twelveWeekGoalStatement: String,
    val morningRoutine: List<RoutineStep>,
    val eveningRoutine: List<RoutineStep>,
    val recommendations: List<ProductCategoryRecommendation>,
    /** When this plan was built — a real timestamp, so the Skin Journey can show it honestly. */
    val createdAtMillis: Long = System.currentTimeMillis(),
)

private const val MaxPriorityFocusAreas = 3

/**
 * Derives a deterministic [PersonalizedPlan] from a SkinPrint, its Skin Report and the 12-week
 * goal set during onboarding. This mapper — not a new analysis — is the seam a real
 * recommendation engine can replace later without changing [PersonalizedPlan]'s shape. The
 * routine's structure (which categories, in which order) is fixed; only which priority each
 * personalizable step addresses varies with the person's own focus areas.
 */
fun buildPersonalizedPlan(
    skinPrint: SkinPrint,
    skinReport: SkinReport,
    twelveWeekGoalStatement: String,
): PersonalizedPlan {
    val priorityFocusAreas = skinReport.focusAreas
        .sortedByDescending { it.level.ordinal }
        .map { it.indicator }
        .take(MaxPriorityFocusAreas)
    val topPriority = priorityFocusAreas.firstOrNull()
    val secondPriority = priorityFocusAreas.getOrNull(1)

    val morningRoutine = listOf(
        RoutineStep(order = 1, time = RoutineTime.MORNING, category = ProductCategory.CLEANSER),
        RoutineStep(order = 2, time = RoutineTime.MORNING, category = ProductCategory.HYDRATING_SERUM),
        RoutineStep(order = 3, time = RoutineTime.MORNING, category = ProductCategory.SUNSCREEN),
    )
    val eveningRoutine = listOf(
        RoutineStep(order = 1, time = RoutineTime.EVENING, category = ProductCategory.CLEANSER),
        RoutineStep(order = 2, time = RoutineTime.EVENING, category = ProductCategory.TARGETED_TREATMENT, targetsFocusArea = topPriority),
        RoutineStep(order = 3, time = RoutineTime.EVENING, category = ProductCategory.MOISTURIZER),
    )

    val recommendations = listOf(
        ProductCategoryRecommendation(ProductCategory.CLEANSER, placement = RoutineTime.MORNING),
        ProductCategoryRecommendation(ProductCategory.HYDRATING_SERUM, supportsFocusArea = secondPriority, placement = RoutineTime.MORNING),
        ProductCategoryRecommendation(ProductCategory.SUNSCREEN, placement = RoutineTime.MORNING),
        ProductCategoryRecommendation(ProductCategory.TARGETED_TREATMENT, supportsFocusArea = topPriority, placement = RoutineTime.EVENING),
        ProductCategoryRecommendation(ProductCategory.MOISTURIZER, placement = RoutineTime.EVENING),
    )

    return PersonalizedPlan(
        skinPrintId = skinPrint.id,
        priorityFocusAreas = priorityFocusAreas,
        twelveWeekGoalStatement = twelveWeekGoalStatement,
        morningRoutine = morningRoutine,
        eveningRoutine = eveningRoutine,
        recommendations = recommendations,
        createdAtMillis = System.currentTimeMillis(),
    )
}
