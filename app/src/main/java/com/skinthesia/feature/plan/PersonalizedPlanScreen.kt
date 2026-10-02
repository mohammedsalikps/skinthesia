package com.skinthesia.feature.plan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSelectableChip
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.PersonalizedPlan
import com.skinthesia.domain.model.ProductCategory
import com.skinthesia.domain.model.VisualIndicator
import com.skinthesia.feature.onboarding.OnboardingScaffold
import com.skinthesia.feature.report.labelRes
import kotlinx.serialization.Serializable

@Serializable
data object PersonalizedPlanRoute

/** Turns existing SkinPrint, Skin Report and goal data into an encouraging, actionable overview. */
@Composable
fun PersonalizedPlanScreen(
    plan: PersonalizedPlan,
    onBack: () -> Unit,
    onViewMorningRoutine: () -> Unit,
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
                text = stringResource(R.string.plan_view_morning_routine_cta),
                onClick = onViewMorningRoutine,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.plan_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.plan_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xl))

        Text(text = stringResource(R.string.plan_section_priorities), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        if (plan.priorityFocusAreas.isEmpty()) {
            Text(text = stringResource(R.string.plan_priorities_empty), style = typography.bodySmall, color = colors.textSecondary)
        } else {
            PriorityChips(plan.priorityFocusAreas)
        }
        Spacer(Modifier.height(spacing.lg))

        Text(text = stringResource(R.string.plan_section_direction), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        SkinthesiaCard {
            Text(
                text = "“${plan.twelveWeekGoalStatement}”",
                style = typography.quote,
                color = colors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(spacing.lg))

        Text(text = stringResource(R.string.plan_section_plan), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            PillarCard(title = stringResource(R.string.plan_pillar_morning_title), body = stringResource(R.string.plan_pillar_morning_body))
            PillarCard(title = stringResource(R.string.plan_pillar_evening_title), body = stringResource(R.string.plan_pillar_evening_body))
            PillarCard(title = stringResource(R.string.plan_pillar_lifestyle_title), body = stringResource(R.string.plan_pillar_lifestyle_body))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PriorityChips(areas: List<VisualIndicator>) {
    val spacing = SkinthesiaTheme.spacing
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        areas.forEach { area ->
            SkinthesiaSelectableChip(label = stringResource(area.labelRes()), selected = true, onClick = {}, enabled = false)
        }
    }
}

@Composable
private fun PillarCard(title: String, body: String) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    SkinthesiaCard {
        Text(text = title, style = typography.title, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = body, style = typography.bodySmall, color = colors.textSecondary)
    }
}

/** Short, action-oriented title for a routine step (e.g. "Cleanse"), as opposed to its full category name. */
internal fun ProductCategory.routineStepTitleRes(): Int = when (this) {
    ProductCategory.CLEANSER -> R.string.routine_step_cleanse
    ProductCategory.HYDRATING_SERUM -> R.string.routine_step_hydrate
    ProductCategory.MOISTURIZER -> R.string.routine_step_moisturize
    ProductCategory.SUNSCREEN -> R.string.routine_step_protect
    ProductCategory.TARGETED_TREATMENT -> R.string.routine_step_target
}

internal fun ProductCategory.stepExplanationRes(): Int = when (this) {
    ProductCategory.CLEANSER -> R.string.routine_explanation_cleanse
    ProductCategory.HYDRATING_SERUM -> R.string.routine_explanation_hydrate
    ProductCategory.MOISTURIZER -> R.string.routine_explanation_moisturize
    ProductCategory.SUNSCREEN -> R.string.routine_explanation_protect
    ProductCategory.TARGETED_TREATMENT -> R.string.routine_explanation_target_generic
}

/** Full descriptive name for a product category (e.g. "Hydrating Serum"), as shown in recommendations. */
internal fun ProductCategory.recommendationTitleRes(): Int = when (this) {
    ProductCategory.CLEANSER -> R.string.product_category_cleanser
    ProductCategory.HYDRATING_SERUM -> R.string.product_category_hydrating_serum
    ProductCategory.MOISTURIZER -> R.string.product_category_moisturizer
    ProductCategory.SUNSCREEN -> R.string.product_category_sunscreen
    ProductCategory.TARGETED_TREATMENT -> R.string.product_category_targeted_treatment
}

internal fun ProductCategory.defaultWhyRes(): Int = when (this) {
    ProductCategory.CLEANSER -> R.string.product_why_cleanser
    ProductCategory.HYDRATING_SERUM -> R.string.product_why_hydrating_serum
    ProductCategory.MOISTURIZER -> R.string.product_why_moisturizer
    ProductCategory.SUNSCREEN -> R.string.product_why_sunscreen
    ProductCategory.TARGETED_TREATMENT -> R.string.product_why_targeted_treatment
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1600)
@Composable
private fun PersonalizedPlanScreenPreview() {
    SkinthesiaTheme {
        PersonalizedPlanScreen(
            plan = PersonalizedPlan(
                skinPrintId = "preview",
                priorityFocusAreas = listOf(VisualIndicator.UNEVEN_TONE, VisualIndicator.TEXTURE, VisualIndicator.PORE_APPEARANCE),
                twelveWeekGoalStatement = "Clearer, healthier skin",
                morningRoutine = emptyList(),
                eveningRoutine = emptyList(),
                recommendations = emptyList(),
            ),
            onBack = {},
            onViewMorningRoutine = {},
        )
    }
}
