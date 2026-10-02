package com.skinthesia.feature.selfie

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaLoadingIndicator
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.PhotoQualityCheck
import com.skinthesia.domain.model.QualityLevel
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
data object PhotoQualityCheckRoute

private const val TotalSteps = 7
private const val Step = 3
private const val SimulatedAnalysisDelayMillis = 900L

/**
 * The third onboarding step: a brief simulated analysis, then a readable breakdown of lighting,
 * face positioning and clarity, with a chance to use the photo or go back and retake it.
 */
@Composable
fun PhotoQualityCheckScreen(
    quality: PhotoQualityCheck?,
    onQualityCheckReady: () -> Unit,
    onBack: () -> Unit,
    onUsePhoto: () -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    LaunchedEffect(quality) {
        if (quality == null) {
            delay(SimulatedAnalysisDelayMillis)
            onQualityCheckReady()
        }
    }

    OnboardingScaffold(
        onBack = onBack,
        currentStep = Step,
        totalSteps = TotalSteps,
        modifier = modifier,
        bottomBar = {
            if (quality != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    SkinthesiaSecondaryButton(
                        text = stringResource(R.string.selfie_retake),
                        onClick = onRetake,
                        modifier = Modifier.weight(1f),
                    )
                    SkinthesiaPrimaryButton(
                        text = stringResource(R.string.quality_use_photo),
                        onClick = onUsePhoto,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
    ) {
        Text(
            text = stringResource(R.string.quality_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xl))

        if (quality == null) {
            AnalyzingState()
        } else {
            QualityResults(quality = quality)
        }
    }
}

@Composable
private fun AnalyzingState() {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SkinthesiaLoadingIndicator(size = spacing.xl)
        Spacer(Modifier.height(spacing.md))
        Text(text = stringResource(R.string.quality_analyzing), style = typography.body, color = colors.textSecondary)
    }
}

@Composable
private fun QualityResults(quality: PhotoQualityCheck) {
    val spacing = SkinthesiaTheme.spacing
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 10 },
    ) {
        Column {
            SkinthesiaCard {
                QualityRow(label = stringResource(R.string.quality_lighting), level = quality.lighting)
                Spacer(Modifier.height(spacing.sm))
                QualityRow(label = stringResource(R.string.quality_face_positioning), level = quality.facePositioning)
                Spacer(Modifier.height(spacing.sm))
                QualityRow(label = stringResource(R.string.quality_clarity), level = quality.clarity)
            }
            Spacer(Modifier.height(spacing.md))
            OverallBanner(level = quality.overall)
        }
    }
}

@Composable
private fun QualityRow(label: String, level: QualityLevel) {
    val typography = SkinthesiaTheme.typography
    val colors = SkinthesiaTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = typography.body, color = colors.textPrimary)
        QualityPill(level = level)
    }
}

@Composable
private fun QualityPill(level: QualityLevel) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    val (container, content, label) = when (level) {
        QualityLevel.GOOD -> Triple(colors.positiveContainer, colors.onPositiveContainer, stringResource(R.string.quality_level_good))
        QualityLevel.FAIR -> Triple(colors.highlightContainer, colors.onHighlightContainer, stringResource(R.string.quality_level_fair))
        QualityLevel.POOR -> Triple(colors.errorContainer, colors.onErrorContainer, stringResource(R.string.quality_level_poor))
    }
    Box(
        modifier = Modifier
            .clip(SkinthesiaTheme.shapes.pill)
            .background(container)
            .padding(horizontal = spacing.sm, vertical = spacing.xxs),
    ) {
        Text(text = label, style = SkinthesiaTheme.typography.labelSmall, color = content)
    }
}

@Composable
private fun OverallBanner(level: QualityLevel) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val (container, content, message) = when (level) {
        QualityLevel.GOOD -> Triple(colors.positiveContainer, colors.onPositiveContainer, stringResource(R.string.quality_overall_ready))
        QualityLevel.FAIR -> Triple(colors.highlightContainer, colors.onHighlightContainer, stringResource(R.string.quality_overall_fair))
        QualityLevel.POOR -> Triple(colors.errorContainer, colors.onErrorContainer, stringResource(R.string.quality_overall_poor))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SkinthesiaTheme.shapes.field)
            .background(container)
            .padding(horizontal = spacing.md, vertical = spacing.sm),
    ) {
        Text(text = message, style = typography.label, color = content)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun PhotoQualityCheckScreenAnalyzingPreview() {
    SkinthesiaTheme {
        PhotoQualityCheckScreen(quality = null, onQualityCheckReady = {}, onBack = {}, onUsePhoto = {}, onRetake = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun PhotoQualityCheckScreenReadyPreview() {
    SkinthesiaTheme {
        PhotoQualityCheckScreen(
            quality = PhotoQualityCheck(QualityLevel.GOOD, QualityLevel.GOOD, QualityLevel.GOOD),
            onQualityCheckReady = {},
            onBack = {},
            onUsePhoto = {},
            onRetake = {},
        )
    }
}
