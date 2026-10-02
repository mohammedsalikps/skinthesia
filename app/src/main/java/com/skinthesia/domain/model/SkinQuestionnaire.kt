package com.skinthesia.domain.model

import kotlinx.serialization.Serializable

/** Self-identified skin type. */
enum class SkinType { DRY, OILY, COMBINATION, NORMAL, NOT_SURE }

/** Self-reported sensitivity to products and environmental change. */
enum class SkinSensitivity { LOW, MODERATE, HIGH, NOT_SURE }

/** How involved the person's current skincare routine is. */
enum class SkincareRoutineLevel { MINIMAL, BASIC, DETAILED, NONE }

/** Concerns the person currently notices in their skin, as distinct from the goals set earlier. */
enum class SkinConcern { ACNE, DARK_SPOTS, UNEVEN_TONE, DRYNESS, OILINESS, TEXTURE, REDNESS, FINE_LINES }

/** Ingredients or product qualities the person prefers to avoid; a preference, not a diagnosis. */
enum class ProductSensitivity { FRAGRANCE, ESSENTIAL_OILS, ALCOHOL, SULFATES, NONE_OR_NOT_SURE }

/** Self-reported context about the person's skin, gathered to help personalise recommendations. */
@Serializable
data class SkinQuestionnaire(
    val skinType: SkinType? = null,
    val sensitivity: SkinSensitivity? = null,
    val routineLevel: SkincareRoutineLevel? = null,
    val concerns: Set<SkinConcern> = emptySet(),
    val productSensitivities: Set<ProductSensitivity> = emptySet(),
)
