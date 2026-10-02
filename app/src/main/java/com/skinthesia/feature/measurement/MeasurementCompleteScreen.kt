package com.skinthesia.feature.measurement

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.FaceRegion
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object MeasurementCompleteRoute

private val DiagramSize = 200.dp

/** The payoff screen once all three regions are captured: a completed face map and what happens next. */
@Composable
fun MeasurementCompleteScreen(
    onBack: () -> Unit,
    onContinueToAnalysis: () -> Unit,
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
                text = stringResource(R.string.measurement_continue_to_analysis),
                onClick = onContinueToAnalysis,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.measurement_complete_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xl))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FaceDiagram(
                activeRegion = null,
                completedRegions = FaceRegion.entries.toSet(),
                modifier = Modifier.size(DiagramSize),
            )
        }
        Spacer(Modifier.height(spacing.xl))

        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            CompletedRegionRow(stringResource(R.string.measurement_region_forehead))
            CompletedRegionRow(stringResource(R.string.measurement_region_left_cheek))
            CompletedRegionRow(stringResource(R.string.measurement_region_right_cheek))
        }
        Spacer(Modifier.height(spacing.lg))

        Text(
            text = stringResource(R.string.measurement_complete_explanation),
            style = typography.body,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun CompletedRegionRow(label: String) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.xl)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(colors.positiveContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = SkinthesiaIcons.Check,
                contentDescription = null,
                tint = colors.onPositiveContainer,
                modifier = Modifier.size(spacing.iconSmall),
            )
        }
        Spacer(Modifier.width(spacing.sm))
        Text(text = label, style = SkinthesiaTheme.typography.body, color = colors.textPrimary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1100)
@Composable
private fun MeasurementCompleteScreenPreview() {
    SkinthesiaTheme {
        MeasurementCompleteScreen(onBack = {}, onContinueToAnalysis = {})
    }
}
