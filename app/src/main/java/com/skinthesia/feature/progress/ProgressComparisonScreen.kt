package com.skinthesia.feature.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinPrintHistory
import com.skinthesia.domain.model.SkinPrintSources
import com.skinthesia.domain.model.hasComparableHistory
import com.skinthesia.feature.combined.CompositeScoreRing
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object ProgressComparisonRoute

/**
 * Compares SkinPrint versions when more than one real one exists. Today there is only ever a
 * baseline, so this deliberately shows a calm "your baseline is ready" state instead of fabricating
 * history, deltas or percentages — never a graph or score drawn from data that doesn't exist yet.
 */
@Composable
fun ProgressComparisonScreen(
    history: SkinPrintHistory,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val baseline = history.minByOrNull { it.version }

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            SkinthesiaPrimaryButton(
                text = stringResource(R.string.progress_continue_cta),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.progress_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.progress_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xl))

        when {
            baseline == null -> {
                Text(text = stringResource(R.string.dashboard_skin_status_empty), style = typography.bodySmall, color = colors.textSecondary)
            }
            history.hasComparableHistory() -> {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    history.sortedBy { it.version }.forEach { entry -> HistoryEntryCard(entry) }
                }
            }
            else -> {
                SkinthesiaCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = stringResource(R.string.progress_baseline_ready), style = typography.title, color = colors.textPrimary)
                        Spacer(Modifier.height(spacing.sm))
                        CompositeScoreRing(score = baseline.compositeScore, modifier = Modifier.size(140.dp))
                        Spacer(Modifier.height(spacing.xs))
                        Text(text = stringResource(R.string.skinprint_tracking_label), style = typography.caption, color = colors.textTertiary)
                        Spacer(Modifier.height(spacing.md))
                        Text(
                            text = stringResource(R.string.progress_baseline_explanation),
                            style = typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryEntryCard(entry: SkinPrint) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    SkinthesiaCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(R.string.progress_history_version_format, entry.version),
                style = typography.label,
                color = colors.textPrimary,
            )
            Text(text = "${entry.compositeScore}", style = typography.numberMedium, color = colors.textPrimary)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1000)
@Composable
private fun ProgressComparisonScreenPreview() {
    SkinthesiaTheme {
        ProgressComparisonScreen(
            history = listOf(
                SkinPrint(
                    id = "preview",
                    profileId = "preview-profile",
                    createdAtMillis = System.currentTimeMillis(),
                    updatedAtMillis = System.currentTimeMillis(),
                    compositeScore = 68,
                    sources = SkinPrintSources(
                        cameraAnalysisId = "camera-1",
                        sensorAnalysisId = "sensor-1",
                        measurementSessionId = "measure-1",
                        goalIds = emptyList(),
                    ),
                ),
            ),
            onBack = {},
            onContinue = {},
        )
    }
}
