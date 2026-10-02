package com.skinthesia.feature.progress

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
import com.skinthesia.core.designsystem.component.SkinthesiaSectionHeader
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.AdaptationInsightKind
import com.skinthesia.domain.model.AdaptivePlan
import com.skinthesia.domain.model.PersonalizedPlan
import com.skinthesia.feature.onboarding.OnboardingScaffold
import com.skinthesia.feature.report.labelRes
import kotlinx.serialization.Serializable

@Serializable
data object AdaptivePlanRoute

/**
 * Shows how the existing PersonalizedPlan could evolve — honest, rule-based planning notes only,
 * never a claim that real improvement or decline has been measured. References [plan] rather than
 * duplicating it, and never introduces a second SkinPrint or PersonalizedPlan.
 */
@Composable
fun AdaptivePlanScreen(
    adaptivePlan: AdaptivePlan,
    plan: PersonalizedPlan,
    onBack: () -> Unit,
    onReturnHome: () -> Unit,
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
                text = stringResource(R.string.adaptive_return_home_cta),
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.adaptive_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.adaptive_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xl))

        SkinthesiaCard {
            Text(
                text = stringResource(if (adaptivePlan.isBaselineOnly) R.string.adaptive_baseline_lead else R.string.adaptive_history_lead),
                style = typography.title,
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(spacing.xs))
            Text(text = stringResource(R.string.adaptive_future_note), style = typography.bodySmall, color = colors.textSecondary)
        }
        Spacer(Modifier.height(spacing.lg))

        SkinthesiaSectionHeader(title = stringResource(R.string.adaptive_considering_title))
        Spacer(Modifier.height(spacing.sm))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            ConsideringRow(stringResource(R.string.adaptive_considering_skinprint))
            plan.priorityFocusAreas.forEach { area -> ConsideringRow(stringResource(area.labelRes())) }
            if (adaptivePlan.insights.isNotEmpty()) {
                ConsideringRow(stringResource(R.string.adaptive_considering_checkin))
            }
            if (plan.twelveWeekGoalStatement.isNotBlank()) {
                ConsideringRow(stringResource(R.string.adaptive_considering_goal))
            }
        }
        Spacer(Modifier.height(spacing.lg))

        if (adaptivePlan.insights.isEmpty()) {
            Text(text = stringResource(R.string.adaptive_no_checkin), style = typography.bodySmall, color = colors.textSecondary)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                adaptivePlan.insights.forEach { kind -> InsightCard(kind) }
            }
        }
    }
}

@Composable
private fun InsightCard(kind: AdaptationInsightKind) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val messageRes = when (kind) {
        AdaptationInsightKind.CONSISTENCY_LOW -> R.string.adaptive_insight_consistency_low
        AdaptationInsightKind.CONSISTENCY_HIGH -> R.string.adaptive_insight_consistency_high
        AdaptationInsightKind.RECENT_CHANGE_NOTED -> R.string.adaptive_insight_recent_change
    }
    SkinthesiaCard {
        Text(text = stringResource(messageRes), style = typography.body, color = colors.textPrimary)
    }
}

@Composable
private fun ConsideringRow(label: String) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.lg)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(colors.positiveContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(SkinthesiaIcons.Check, contentDescription = null, tint = colors.onPositiveContainer, modifier = Modifier.size(spacing.iconSmall - 2.dp))
        }
        Spacer(Modifier.width(spacing.sm))
        Text(text = label, style = SkinthesiaTheme.typography.body, color = colors.textPrimary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1200)
@Composable
private fun AdaptivePlanScreenPreview() {
    SkinthesiaTheme {
        AdaptivePlanScreen(
            adaptivePlan = AdaptivePlan(
                skinPrintId = "preview",
                isBaselineOnly = true,
                insights = listOf(AdaptationInsightKind.CONSISTENCY_HIGH, AdaptationInsightKind.RECENT_CHANGE_NOTED),
            ),
            plan = PersonalizedPlan(
                skinPrintId = "preview",
                priorityFocusAreas = listOf(com.skinthesia.domain.model.VisualIndicator.UNEVEN_TONE),
                twelveWeekGoalStatement = "Clearer, more even skin",
                morningRoutine = emptyList(),
                eveningRoutine = emptyList(),
                recommendations = emptyList(),
            ),
            onBack = {},
            onReturnHome = {},
        )
    }
}
