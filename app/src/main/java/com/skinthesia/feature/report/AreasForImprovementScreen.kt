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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaProgressBar
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.IndicatorLevel
import com.skinthesia.domain.model.SkinReportFocusArea
import com.skinthesia.domain.model.VisualIndicator
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object AreasForImprovementRoute

/**
 * A calm, individual look at each characteristic the Skin Report flagged. Only areas actually
 * present in the simulated analysis appear here; nothing is hardcoded as a problem.
 */
@Composable
fun AreasForImprovementScreen(
    focusAreas: List<SkinReportFocusArea>,
    onBack: () -> Unit,
    onContinue: () -> Unit,
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
                text = stringResource(R.string.areas_continue_cta),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.areas_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.areas_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xl))

        if (focusAreas.isEmpty()) {
            EmptyState()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                focusAreas.forEach { area -> FocusAreaCard(area) }
            }
        }
        Spacer(Modifier.height(spacing.xl))

        Text(text = stringResource(R.string.areas_next_note), style = typography.bodySmall, color = colors.textTertiary)
    }
}

@Composable
private fun EmptyState() {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    SkinthesiaCard {
        Text(text = stringResource(R.string.areas_empty_title), style = typography.title, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.areas_empty_body), style = typography.bodySmall, color = colors.textSecondary)
    }
}

@Composable
private fun FocusAreaCard(area: SkinReportFocusArea) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    SkinthesiaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(area.indicator.labelRes()), style = typography.title, color = colors.textPrimary)
            LevelBadge(area.level)
        }
        Spacer(Modifier.height(spacing.xs))
        SkinthesiaProgressBar(progress = area.level.visualFraction())
        Spacer(Modifier.height(spacing.md))

        Text(text = stringResource(area.indicator.explanationRes()), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.md))

        Text(text = stringResource(R.string.areas_why_highlighted_label), style = typography.labelSmall, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(R.string.focus_why_highlighted), style = typography.bodySmall, color = colors.textSecondary)
    }
}

@Composable
private fun LevelBadge(level: IndicatorLevel) {
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

/** A gentle relative-magnitude indicator, not a precise measurement. */
private fun IndicatorLevel.visualFraction(): Float = when (this) {
    IndicatorLevel.MINIMAL -> 0.25f
    IndicatorLevel.MILD -> 0.45f
    IndicatorLevel.MODERATE -> 0.65f
    IndicatorLevel.NOTICEABLE -> 0.85f
}

private fun VisualIndicator.explanationRes(): Int = when (this) {
    VisualIndicator.ACNE_APPEARANCE -> R.string.focus_explanation_acne_appearance
    VisualIndicator.PIGMENTATION -> R.string.focus_explanation_pigmentation
    VisualIndicator.UNEVEN_TONE -> R.string.focus_explanation_uneven_tone
    VisualIndicator.TEXTURE -> R.string.focus_explanation_texture
    VisualIndicator.PORE_APPEARANCE -> R.string.focus_explanation_pore_appearance
    VisualIndicator.REDNESS -> R.string.focus_explanation_redness
    VisualIndicator.DARK_CIRCLES -> R.string.focus_explanation_dark_circles
    VisualIndicator.FINE_LINES -> R.string.focus_explanation_fine_lines
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1400)
@Composable
private fun AreasForImprovementScreenPreview() {
    SkinthesiaTheme {
        AreasForImprovementScreen(
            focusAreas = listOf(
                SkinReportFocusArea(VisualIndicator.UNEVEN_TONE, IndicatorLevel.MODERATE),
                SkinReportFocusArea(VisualIndicator.PORE_APPEARANCE, IndicatorLevel.NOTICEABLE),
            ),
            onBack = {},
            onContinue = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 700)
@Composable
private fun AreasForImprovementScreenEmptyPreview() {
    SkinthesiaTheme {
        AreasForImprovementScreen(focusAreas = emptyList(), onBack = {}, onContinue = {})
    }
}
