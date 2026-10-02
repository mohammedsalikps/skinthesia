package com.skinthesia.feature.combined

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaLoadingIndicator
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
data object CombinedAnalysisRoute

private const val ChecklistStepDelayMillis = 220L
private const val CompleteSettleDelayMillis = 600L

/**
 * Shows the four inputs (camera, probe, profile, goals) being brought together, then hands off to
 * the SkinPrint result once combining finishes.
 */
@Composable
fun CombinedAnalysisScreen(
    uiState: CombinedAnalysisUiState,
    onStart: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onComplete: (SkinPrint) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    LaunchedEffect(Unit) { onStart() }

    var readySources by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        repeat(4) { index ->
            delay(if (index == 0) 150L else ChecklistStepDelayMillis)
            readySources = index + 1
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is CombinedAnalysisUiState.Complete) {
            delay(CompleteSettleDelayMillis)
            onComplete(uiState.skinPrint)
        }
    }

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            when (uiState) {
                CombinedAnalysisUiState.Failed -> SkinthesiaPrimaryButton(
                    text = stringResource(R.string.combined_retry_cta),
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
                CombinedAnalysisUiState.MissingInputs -> SkinthesiaSecondaryButton(
                    text = stringResource(R.string.action_back),
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                )
                else -> Unit
            }
        },
    ) {
        when (uiState) {
            CombinedAnalysisUiState.MissingInputs -> ErrorBlock(
                title = stringResource(R.string.combined_error_missing_title),
                body = stringResource(R.string.combined_error_missing_body),
            )
            CombinedAnalysisUiState.Failed -> ErrorBlock(
                title = stringResource(R.string.combined_error_failed_title),
                body = stringResource(R.string.combined_error_failed_body),
            )
            else -> {
                Text(
                    text = stringResource(R.string.combined_title),
                    style = typography.headline,
                    color = colors.textPrimary,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(spacing.xl))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ConvergenceIllustration(
                        sourcesReady = readySources,
                        synthesizing = readySources >= 4,
                        modifier = Modifier.size(200.dp),
                    )
                }
                Spacer(Modifier.height(spacing.xl))

                Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                    SourceRow(
                        title = stringResource(R.string.combined_source_camera),
                        readyDetail = stringResource(R.string.combined_source_camera_ready),
                        ready = readySources >= 1,
                    )
                    SourceRow(
                        title = stringResource(R.string.combined_source_probe),
                        readyDetail = stringResource(R.string.combined_source_probe_ready),
                        ready = readySources >= 2,
                    )
                    SourceRow(
                        title = stringResource(R.string.combined_source_profile),
                        readyDetail = stringResource(R.string.combined_source_profile_ready),
                        ready = readySources >= 3,
                    )
                    SourceRow(
                        title = stringResource(R.string.combined_source_goals),
                        readyDetail = stringResource(R.string.combined_source_goals_ready),
                        ready = readySources >= 4,
                    )
                }

                if (readySources >= 4) {
                    Spacer(Modifier.height(spacing.xl))
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        SkinthesiaLoadingIndicator(size = spacing.lg)
                        Spacer(Modifier.height(spacing.sm))
                        Text(
                            text = stringResource(
                                if (uiState is CombinedAnalysisUiState.Complete) {
                                    R.string.combined_status_ready
                                } else {
                                    R.string.combined_status_understanding
                                },
                            ),
                            style = typography.body,
                            color = colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceRow(title: String, readyDetail: String, ready: Boolean) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val containerColor by animateColorAsState(
        targetValue = if (ready) colors.positiveContainer else colors.surfaceMuted,
        animationSpec = tween(300),
        label = "sourceContainer",
    )
    val contentColor = if (ready) colors.onPositiveContainer else colors.textTertiary

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.xl)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(containerColor),
            contentAlignment = Alignment.Center,
        ) {
            if (ready) {
                Icon(
                    imageVector = SkinthesiaIcons.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(spacing.iconSmall),
                )
            }
        }
        Spacer(Modifier.width(spacing.sm))
        Column {
            Text(text = title, style = typography.label, color = colors.textPrimary)
            Text(
                text = if (ready) readyDetail else stringResource(R.string.combined_source_preparing),
                style = typography.caption,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun ErrorBlock(title: String, body: String) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    Text(text = title, style = typography.headline, color = colors.textPrimary, modifier = Modifier.semantics { heading() })
    Spacer(Modifier.height(spacing.sm))
    Text(text = body, style = typography.body, color = colors.textSecondary)
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1200)
@Composable
private fun CombinedAnalysisScreenPreview() {
    SkinthesiaTheme {
        CombinedAnalysisScreen(
            uiState = CombinedAnalysisUiState.Combining(progress = 0.6f),
            onStart = {},
            onRetry = {},
            onBack = {},
            onComplete = {},
        )
    }
}
