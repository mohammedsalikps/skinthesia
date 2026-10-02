package com.skinthesia.domain.model

import kotlinx.serialization.Serializable

/** One qualitative/relative component of a SkinPrint's breakdown — never a clinical measurement. */
enum class SkinPrintComponent { CLARITY, EVEN_TONE, TEXTURE, HYDRATION, PORE_APPEARANCE }

/**
 * Which prior analyses and context contributed to a [SkinPrint], so its provenance is always
 * traceable without duplicating the referenced data here — only IDs are kept.
 */
@Serializable
data class SkinPrintSources(
    val cameraAnalysisId: String?,
    val sensorAnalysisId: String?,
    val measurementSessionId: String?,
    /** The skin goals in effect when this SkinPrint was built. */
    val goalIds: List<SkinGoal>,
)

/**
 * A person's evolving, composite skin-tracking profile — a simulated development indicator, never
 * a medical score or a clinically validated measurement. [version] increases with every new
 * SkinPrint built for the same [profileId], so a history can be kept once persistence exists. No
 * raw image bytes are stored here, and no specific fake sensor-channel shape is assumed:
 * [sources] carries only IDs, and [componentScores] uses this stable domain enum rather than any
 * provider's internal representation.
 */
@Serializable
data class SkinPrint(
    val id: String,
    val profileId: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val version: Int = 1,
    /** 0..100, a composite tracking indicator, not a diagnostic score. */
    val compositeScore: Int,
    val componentScores: Map<SkinPrintComponent, Int> = emptyMap(),
    val sources: SkinPrintSources,
    val isSimulated: Boolean = true,
)
