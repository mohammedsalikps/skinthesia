package com.skinthesia.feature.combined

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
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaProgressBar
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinPrintComponent
import com.skinthesia.domain.model.SkinPrintSources
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data object SkinPrintRoute

/** The evolving composite tracking profile: the central identity screen of the product so far. */
@Composable
fun SkinPrintScreen(
    skinPrint: SkinPrint,
    onBack: () -> Unit,
    onViewSkinReport: () -> Unit,
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
                text = stringResource(R.string.skinprint_view_report_cta),
                onClick = onViewSkinReport,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.skinprint_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.skinprint_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.sm))
        Text(text = stringResource(R.string.skinprint_results_ready), style = typography.label, color = colors.positive)
        Spacer(Modifier.height(spacing.xl))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.skinprint_overall_label).uppercase(Locale.getDefault()),
                style = typography.overline,
                color = colors.textTertiary,
            )
            Spacer(Modifier.height(spacing.sm))
            CompositeScoreRing(score = skinPrint.compositeScore, modifier = Modifier.size(180.dp))
            Spacer(Modifier.height(spacing.sm))
            Text(text = stringResource(R.string.skinprint_tracking_label), style = typography.caption, color = colors.textTertiary)
        }
        Spacer(Modifier.height(spacing.xl))

        Text(text = stringResource(R.string.skinprint_breakdown_title), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        SkinthesiaCard {
            SkinPrintComponent.entries.forEachIndexed { index, component ->
                ComponentRow(label = stringResource(component.labelRes()), score = skinPrint.componentScores[component] ?: 0)
                if (index < SkinPrintComponent.entries.lastIndex) Spacer(Modifier.height(spacing.sm))
            }
        }
        Spacer(Modifier.height(spacing.lg))

        Text(text = stringResource(R.string.skinprint_built_from_title), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            BuiltFromRow(label = stringResource(R.string.skinprint_built_from_camera), included = skinPrint.sources.cameraAnalysisId != null)
            BuiltFromRow(label = stringResource(R.string.skinprint_built_from_probe), included = skinPrint.sources.sensorAnalysisId != null)
            BuiltFromRow(label = stringResource(R.string.skinprint_built_from_goals), included = skinPrint.sources.goalIds.isNotEmpty())
            BuiltFromRow(label = stringResource(R.string.skinprint_built_from_lifestyle), included = true)
        }
        Spacer(Modifier.height(spacing.lg))

        Text(text = stringResource(R.string.skinprint_disclaimer), style = typography.bodySmall, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.sm))
        Text(text = stringResource(R.string.skinprint_will_evolve), style = typography.bodySmall, color = colors.textTertiary)
    }
}

@Composable
private fun ComponentRow(label: String, score: Int) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    Column {
        Text(text = label, style = typography.body, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xxs))
        SkinthesiaProgressBar(progress = (score / 100f).coerceIn(0f, 1f))
    }
}

@Composable
private fun BuiltFromRow(label: String, included: Boolean) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    val containerColor = if (included) colors.positiveContainer else colors.surfaceMuted
    val contentColor = if (included) colors.onPositiveContainer else colors.textTertiary
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.xl)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(containerColor),
            contentAlignment = Alignment.Center,
        ) {
            if (included) {
                Icon(SkinthesiaIcons.Check, contentDescription = null, tint = contentColor, modifier = Modifier.size(spacing.iconSmall))
            }
        }
        Spacer(Modifier.width(spacing.sm))
        Text(text = label, style = SkinthesiaTheme.typography.body, color = if (included) colors.textPrimary else colors.textTertiary)
    }
}

private fun SkinPrintComponent.labelRes(): Int = when (this) {
    SkinPrintComponent.CLARITY -> R.string.skinprint_component_clarity
    SkinPrintComponent.EVEN_TONE -> R.string.skinprint_component_even_tone
    SkinPrintComponent.TEXTURE -> R.string.skinprint_component_texture
    SkinPrintComponent.HYDRATION -> R.string.skinprint_component_hydration
    SkinPrintComponent.PORE_APPEARANCE -> R.string.skinprint_component_pore_appearance
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1800)
@Composable
private fun SkinPrintScreenPreview() {
    SkinthesiaTheme {
        SkinPrintScreen(
            skinPrint = SkinPrint(
                id = "preview",
                profileId = "preview-profile",
                createdAtMillis = 0L,
                updatedAtMillis = 0L,
                compositeScore = 68,
                componentScores = SkinPrintComponent.entries.associateWith { 60 + it.ordinal * 5 },
                sources = SkinPrintSources(
                    cameraAnalysisId = "camera-1",
                    sensorAnalysisId = "sensor-1",
                    measurementSessionId = "measure-1",
                    goalIds = emptyList(),
                ),
            ),
            onBack = {},
            onViewSkinReport = {},
        )
    }
}
