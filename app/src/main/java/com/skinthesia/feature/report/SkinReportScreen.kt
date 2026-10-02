package com.skinthesia.feature.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.GeneralStrengthKind
import com.skinthesia.domain.model.IndicatorLevel
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinReport
import com.skinthesia.domain.model.SkinReportFocusArea
import com.skinthesia.domain.model.SkinReportStrength
import com.skinthesia.domain.model.SkinPrintSources
import com.skinthesia.domain.model.VisualIndicator
import com.skinthesia.feature.combined.CompositeScoreRing
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object SkinReportRoute

/** Transforms a SkinPrint and its camera analysis into an editorial, readable summary. */
@Composable
fun SkinReportScreen(
    skinPrint: SkinPrint,
    report: SkinReport,
    onBack: () -> Unit,
    onExploreFocusAreas: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            SkinthesiaPrimaryButton(
                text = stringResource(R.string.skin_report_explore_cta),
                onClick = onExploreFocusAreas,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.skin_report_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.skin_report_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xl))

        // 1. SkinPrint summary
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            CompositeScoreRing(score = skinPrint.compositeScore, modifier = Modifier.size(140.dp))
            Spacer(Modifier.height(spacing.sm))
            Text(text = stringResource(R.string.skinprint_tracking_label), style = typography.caption, color = colors.textTertiary)
        }
        Spacer(Modifier.height(spacing.xl))

        // 2. Your strengths
        Text(text = stringResource(R.string.skin_report_section_strengths), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            report.strengths.forEach { strength -> StrengthCard(strength) }
        }
        Spacer(Modifier.height(spacing.lg))

        // 3. Focus areas
        Text(text = stringResource(R.string.skin_report_section_focus_areas), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        if (report.focusAreas.isEmpty()) {
            Text(text = stringResource(R.string.skin_report_focus_areas_empty), style = typography.bodySmall, color = colors.textSecondary)
        } else {
            SkinthesiaCard {
                report.focusAreas.forEachIndexed { index, area ->
                    FocusAreaSummaryRow(area)
                    if (index < report.focusAreas.lastIndex) Spacer(Modifier.height(spacing.sm))
                }
            }
        }
        Spacer(Modifier.height(spacing.lg))

        // 4. Understanding your profile
        Text(text = stringResource(R.string.skin_report_section_understanding), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.skin_report_understanding_body), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.md))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            SourceRow(label = stringResource(R.string.skin_report_source_camera), included = skinPrint.sources.cameraAnalysisId != null)
            SourceRow(label = stringResource(R.string.skin_report_source_probe), included = skinPrint.sources.sensorAnalysisId != null)
            SourceRow(label = stringResource(R.string.skin_report_source_lifestyle), included = true)
            SourceRow(label = stringResource(R.string.skin_report_source_goals), included = skinPrint.sources.goalIds.isNotEmpty())
            SourceRow(label = stringResource(R.string.skin_report_source_questionnaire), included = true)
        }
    }
}

@Composable
private fun StrengthCard(strength: SkinReportStrength) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val (title, detail) = strength.copyRes()

    SkinthesiaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(spacing.xl)
                    .clip(SkinthesiaTheme.shapes.pill)
                    .background(colors.positiveContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(SkinthesiaIcons.Check, contentDescription = null, tint = colors.onPositiveContainer, modifier = Modifier.size(spacing.iconSmall))
            }
            Spacer(Modifier.width(spacing.sm))
            Column {
                Text(text = stringResource(title), style = typography.label, color = colors.textPrimary)
                Text(text = stringResource(detail), style = typography.bodySmall, color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun FocusAreaSummaryRow(area: SkinReportFocusArea) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(area.indicator.labelRes()), style = typography.body, color = colors.textPrimary)
        LevelPill(area.level)
    }
}

@Composable
private fun LevelPill(level: IndicatorLevel) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    val (container, content) = when (level) {
        IndicatorLevel.MODERATE -> colors.highlightContainer to colors.onHighlightContainer
        IndicatorLevel.NOTICEABLE -> colors.errorContainer to colors.onErrorContainer
        IndicatorLevel.MINIMAL, IndicatorLevel.MILD -> colors.positiveContainer to colors.onPositiveContainer
    }
    Box(
        modifier = Modifier
            .clip(SkinthesiaTheme.shapes.pill)
            .background(container)
            .padding(horizontal = spacing.sm, vertical = spacing.xxs),
    ) {
        Text(text = stringResource(level.labelRes()), style = SkinthesiaTheme.typography.labelSmall, color = content)
    }
}

@Composable
private fun SourceRow(label: String, included: Boolean) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.lg)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(if (included) colors.positiveContainer else colors.surfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            if (included) {
                Icon(SkinthesiaIcons.Check, contentDescription = null, tint = colors.onPositiveContainer, modifier = Modifier.size(spacing.iconSmall - 2.dp))
            }
        }
        Spacer(Modifier.width(spacing.sm))
        Text(text = label, style = SkinthesiaTheme.typography.body, color = if (included) colors.textPrimary else colors.textTertiary)
    }
}

private fun SkinReportStrength.copyRes(): Pair<Int, Int> = when (this) {
    is SkinReportStrength.FromIndicator -> indicator.strengthCopyRes()
    is SkinReportStrength.General -> kind.copyRes()
}

private fun VisualIndicator.strengthCopyRes(): Pair<Int, Int> = when (this) {
    VisualIndicator.ACNE_APPEARANCE -> R.string.strength_acne_appearance_title to R.string.strength_acne_appearance_detail
    VisualIndicator.PIGMENTATION -> R.string.strength_pigmentation_title to R.string.strength_pigmentation_detail
    VisualIndicator.UNEVEN_TONE -> R.string.strength_uneven_tone_title to R.string.strength_uneven_tone_detail
    VisualIndicator.TEXTURE -> R.string.strength_texture_title to R.string.strength_texture_detail
    VisualIndicator.PORE_APPEARANCE -> R.string.strength_pore_appearance_title to R.string.strength_pore_appearance_detail
    VisualIndicator.REDNESS -> R.string.strength_redness_title to R.string.strength_redness_detail
    VisualIndicator.DARK_CIRCLES -> R.string.strength_dark_circles_title to R.string.strength_dark_circles_detail
    VisualIndicator.FINE_LINES -> R.string.strength_fine_lines_title to R.string.strength_fine_lines_detail
}

private fun GeneralStrengthKind.copyRes(): Pair<Int, Int> = when (this) {
    GeneralStrengthKind.BUILDING_BASELINE -> R.string.strength_general_building_baseline_title to R.string.strength_general_building_baseline_detail
    GeneralStrengthKind.PERSONALIZED_CONTEXT -> R.string.strength_general_personalized_context_title to R.string.strength_general_personalized_context_detail
    GeneralStrengthKind.READY_TO_ACT -> R.string.strength_general_ready_to_act_title to R.string.strength_general_ready_to_act_detail
}

internal fun VisualIndicator.labelRes(): Int = when (this) {
    VisualIndicator.ACNE_APPEARANCE -> R.string.indicator_acne_appearance
    VisualIndicator.PIGMENTATION -> R.string.indicator_pigmentation
    VisualIndicator.UNEVEN_TONE -> R.string.indicator_uneven_tone
    VisualIndicator.TEXTURE -> R.string.indicator_texture
    VisualIndicator.PORE_APPEARANCE -> R.string.indicator_pore_appearance
    VisualIndicator.REDNESS -> R.string.indicator_redness
    VisualIndicator.DARK_CIRCLES -> R.string.indicator_dark_circles
    VisualIndicator.FINE_LINES -> R.string.indicator_fine_lines
}

internal fun IndicatorLevel.labelRes(): Int = when (this) {
    IndicatorLevel.MINIMAL -> R.string.indicator_level_minimal
    IndicatorLevel.MILD -> R.string.indicator_level_mild
    IndicatorLevel.MODERATE -> R.string.indicator_level_moderate
    IndicatorLevel.NOTICEABLE -> R.string.indicator_level_noticeable
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 2200)
@Composable
private fun SkinReportScreenPreview() {
    SkinthesiaTheme {
        SkinReportScreen(
            skinPrint = SkinPrint(
                id = "preview",
                profileId = "preview-profile",
                createdAtMillis = 0L,
                updatedAtMillis = 0L,
                compositeScore = 68,
                sources = SkinPrintSources(
                    cameraAnalysisId = "camera-1",
                    sensorAnalysisId = "sensor-1",
                    measurementSessionId = "measure-1",
                    goalIds = emptyList(),
                ),
            ),
            report = SkinReport(
                id = "report-1",
                skinPrintId = "preview",
                cameraAnalysisId = "camera-1",
                generatedAtMillis = 0L,
                strengths = listOf(
                    SkinReportStrength.FromIndicator(VisualIndicator.TEXTURE),
                    SkinReportStrength.FromIndicator(VisualIndicator.REDNESS),
                ),
                focusAreas = listOf(
                    SkinReportFocusArea(VisualIndicator.UNEVEN_TONE, IndicatorLevel.MODERATE),
                    SkinReportFocusArea(VisualIndicator.PORE_APPEARANCE, IndicatorLevel.NOTICEABLE),
                ),
            ),
            onBack = {},
            onExploreFocusAreas = {},
        )
    }
}
