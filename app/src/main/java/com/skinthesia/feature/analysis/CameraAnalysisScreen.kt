package com.skinthesia.feature.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaProgressBar
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.CameraAnalysisStatus
import com.skinthesia.domain.model.IndicatorLevel
import com.skinthesia.domain.model.VisualIndicator
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object CameraAnalysisRoute

private const val PortraitAspectRatio = 0.8f

/**
 * Runs the (simulated) camera-based visual analysis on the selfie captured earlier in onboarding
 * and presents a qualitative summary. Analysis starts automatically on arrival.
 */
@Composable
fun CameraAnalysisScreen(
    status: CameraAnalysisStatus,
    progress: Float,
    indicators: Map<VisualIndicator, IndicatorLevel>,
    onStartAnalysis: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    LaunchedEffect(Unit) { onStartAnalysis() }

    val complete = status == CameraAnalysisStatus.COMPLETE
    val failed = status == CameraAnalysisStatus.FAILED

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            when {
                complete -> SkinthesiaPrimaryButton(
                    text = stringResource(R.string.action_continue),
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                )
                failed -> SkinthesiaPrimaryButton(
                    text = stringResource(R.string.analysis_retry_cta),
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
                else -> Unit
            }
        },
    ) {
        Text(
            text = stringResource(if (complete) R.string.analysis_overview_title else R.string.analysis_question_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = stringResource(R.string.analysis_simulated_label),
            style = typography.overline,
            color = colors.textTertiary,
        )
        Spacer(Modifier.height(spacing.xl))

        SelfiePortraitIllustration(
            scanning = status == CameraAnalysisStatus.ANALYZING,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PortraitAspectRatio),
        )
        Spacer(Modifier.height(spacing.lg))

        when (status) {
            CameraAnalysisStatus.PREPARING -> StatusText(stringResource(R.string.analysis_status_preparing))

            CameraAnalysisStatus.ANALYZING -> Column {
                StatusText(stringResource(R.string.analysis_status_analyzing))
                Spacer(Modifier.height(spacing.sm))
                SkinthesiaProgressBar(progress = progress)
            }

            CameraAnalysisStatus.FAILED -> StatusText(stringResource(R.string.analysis_status_failed), color = colors.error)

            CameraAnalysisStatus.COMPLETE -> Column {
                SkinthesiaCard {
                    VisualIndicator.entries.forEachIndexed { index, indicator ->
                        IndicatorRow(label = indicator.displayLabel(), level = indicators[indicator])
                        if (index < VisualIndicator.entries.lastIndex) {
                            Spacer(Modifier.height(spacing.sm))
                        }
                    }
                }
                Spacer(Modifier.height(spacing.lg))
                Text(
                    text = stringResource(R.string.analysis_disclaimer),
                    style = typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun StatusText(text: String, color: Color = SkinthesiaTheme.colors.textSecondary) {
    Text(text = text, style = SkinthesiaTheme.typography.body, color = color)
}

@Composable
private fun IndicatorRow(label: String, level: IndicatorLevel?) {
    val typography = SkinthesiaTheme.typography
    val colors = SkinthesiaTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = typography.body, color = colors.textPrimary)
        if (level != null) LevelPill(level)
    }
}

@Composable
private fun LevelPill(level: IndicatorLevel) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    val (container, content) = when (level) {
        IndicatorLevel.MINIMAL, IndicatorLevel.MILD -> colors.positiveContainer to colors.onPositiveContainer
        IndicatorLevel.MODERATE -> colors.highlightContainer to colors.onHighlightContainer
        IndicatorLevel.NOTICEABLE -> colors.errorContainer to colors.onErrorContainer
    }
    Box(
        modifier = Modifier
            .clip(SkinthesiaTheme.shapes.pill)
            .background(container)
            .padding(horizontal = spacing.sm, vertical = spacing.xxs),
    ) {
        Text(text = level.displayLabel(), style = SkinthesiaTheme.typography.labelSmall, color = content)
    }
}

@Composable
private fun VisualIndicator.displayLabel(): String = stringResource(
    when (this) {
        VisualIndicator.ACNE_APPEARANCE -> R.string.indicator_acne_appearance
        VisualIndicator.PIGMENTATION -> R.string.indicator_pigmentation
        VisualIndicator.UNEVEN_TONE -> R.string.indicator_uneven_tone
        VisualIndicator.TEXTURE -> R.string.indicator_texture
        VisualIndicator.PORE_APPEARANCE -> R.string.indicator_pore_appearance
        VisualIndicator.REDNESS -> R.string.indicator_redness
        VisualIndicator.DARK_CIRCLES -> R.string.indicator_dark_circles
        VisualIndicator.FINE_LINES -> R.string.indicator_fine_lines
    },
)

@Composable
private fun IndicatorLevel.displayLabel(): String = stringResource(
    when (this) {
        IndicatorLevel.MINIMAL -> R.string.indicator_level_minimal
        IndicatorLevel.MILD -> R.string.indicator_level_mild
        IndicatorLevel.MODERATE -> R.string.indicator_level_moderate
        IndicatorLevel.NOTICEABLE -> R.string.indicator_level_noticeable
    },
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1400)
@Composable
private fun CameraAnalysisScreenAnalyzingPreview() {
    SkinthesiaTheme {
        CameraAnalysisScreen(
            status = CameraAnalysisStatus.ANALYZING,
            progress = 0.5f,
            indicators = emptyMap(),
            onStartAnalysis = {},
            onRetry = {},
            onBack = {},
            onContinue = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1600)
@Composable
private fun CameraAnalysisScreenCompletePreview() {
    SkinthesiaTheme {
        CameraAnalysisScreen(
            status = CameraAnalysisStatus.COMPLETE,
            progress = 1f,
            indicators = VisualIndicator.entries.associateWith { IndicatorLevel.MILD },
            onStartAnalysis = {},
            onRetry = {},
            onBack = {},
            onContinue = {},
        )
    }
}
