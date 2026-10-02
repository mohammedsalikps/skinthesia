package com.skinthesia.domain.model

import java.util.UUID

/** A supportive observation the report can show — grounded in a specific visual indicator, or a general fallback when there isn't enough positive indicator data to fill out the usual 2-4. */
sealed interface SkinReportStrength {
    data class FromIndicator(val indicator: VisualIndicator) : SkinReportStrength
    data class General(val kind: GeneralStrengthKind) : SkinReportStrength
}

/** Supportive observations that don't depend on any specific visual finding. */
enum class GeneralStrengthKind { BUILDING_BASELINE, PERSONALIZED_CONTEXT, READY_TO_ACT }

/** One characteristic the report is calling out for attention, with enough context to explore it individually. */
data class SkinReportFocusArea(
    val indicator: VisualIndicator,
    val level: IndicatorLevel,
)

/**
 * A readable, editorial summary derived from a [SkinPrint] and its camera analysis — never a new
 * source of truth. [skinPrintId] and [cameraAnalysisId] keep this traceable back to the analyses
 * that produced it, so a future backend can persist the derived copy without duplicating them.
 */
data class SkinReport(
    val id: String,
    val skinPrintId: String,
    val cameraAnalysisId: String?,
    val generatedAtMillis: Long,
    val strengths: List<SkinReportStrength>,
    val focusAreas: List<SkinReportFocusArea>,
)

private val FocusWorthyLevels = setOf(IndicatorLevel.MODERATE, IndicatorLevel.NOTICEABLE)
private const val MinStrengths = 2
private const val MaxStrengths = 4

/**
 * Derives a [SkinReport] from a SkinPrint and the camera analysis that helped produce it. Pure and
 * synchronous — this is a presentation-layer summary of existing results, not a new analysis, so
 * it needs no processing delay or provider of its own.
 */
fun buildSkinReport(skinPrint: SkinPrint, cameraAnalysis: CameraSkinAnalysis?): SkinReport {
    val indicatorLevels = cameraAnalysis?.indicators.orEmpty()

    val focusAreas = indicatorLevels
        .filterValues { it in FocusWorthyLevels }
        .map { (indicator, level) -> SkinReportFocusArea(indicator, level) }
        .sortedByDescending { it.level.ordinal }

    val indicatorStrengths = indicatorLevels
        .filterValues { it !in FocusWorthyLevels }
        .map { (indicator, _) -> SkinReportStrength.FromIndicator(indicator) }
    val generalStrengths = GeneralStrengthKind.entries.map { SkinReportStrength.General(it) }

    val strengths = buildList {
        addAll(indicatorStrengths.take(MaxStrengths))
        var next = 0
        while (size < MinStrengths && next < generalStrengths.size) {
            add(generalStrengths[next])
            next++
        }
    }.take(MaxStrengths)

    return SkinReport(
        id = UUID.randomUUID().toString(),
        skinPrintId = skinPrint.id,
        cameraAnalysisId = cameraAnalysis?.id,
        generatedAtMillis = System.currentTimeMillis(),
        strengths = strengths,
        focusAreas = focusAreas,
    )
}
