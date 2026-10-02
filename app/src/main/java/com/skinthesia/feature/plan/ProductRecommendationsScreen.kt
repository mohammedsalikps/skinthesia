package com.skinthesia.feature.plan

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.ProductCategory
import com.skinthesia.domain.model.ProductCategoryRecommendation
import com.skinthesia.domain.model.RoutineTime
import com.skinthesia.domain.model.VisualIndicator
import com.skinthesia.feature.onboarding.OnboardingScaffold
import com.skinthesia.feature.report.labelRes
import kotlinx.serialization.Serializable

@Serializable
data object ProductRecommendationsRoute

/**
 * Category-level recommendations for the plan's routines — never real products, prices, brands or
 * purchase links, and always clearly labeled as a simulated development recommendation.
 */
@Composable
fun ProductRecommendationsScreen(
    recommendations: List<ProductCategoryRecommendation>,
    onToggleRecommendation: (ProductCategory) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            SkinthesiaPrimaryButton(text = stringResource(R.string.action_continue), onClick = onContinue, modifier = Modifier.fillMaxWidth())
        },
    ) {
        Text(
            text = stringResource(R.string.products_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.products_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xl))

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            recommendations.forEach { recommendation ->
                RecommendationCard(recommendation = recommendation, onToggle = { onToggleRecommendation(recommendation.category) })
            }
        }
    }
}

@Composable
private fun RecommendationCard(recommendation: ProductCategoryRecommendation, onToggle: () -> Unit) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    SkinthesiaCard {
        Text(text = stringResource(recommendation.category.recommendationTitleRes()), style = typography.title, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(R.string.products_simulated_label), style = typography.overline, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.sm))
        Text(text = recommendation.whyItFits(), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.sm))

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            recommendation.supportsFocusArea?.let { area -> InfoChip(stringResource(R.string.products_focus_format, stringResource(area.labelRes()))) }
            InfoChip(stringResource(R.string.products_placement_format, recommendation.placement.labelRes()))
        }
        Spacer(Modifier.height(spacing.md))

        if (recommendation.isAddedToRoutine) {
            SkinthesiaSecondaryButton(
                text = stringResource(R.string.products_added_cta),
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            SkinthesiaPrimaryButton(
                text = stringResource(R.string.products_add_cta),
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    Box(
        modifier = Modifier
            .clip(SkinthesiaTheme.shapes.pill)
            .background(colors.surfaceMuted)
            .padding(horizontal = spacing.sm, vertical = spacing.xxs),
    ) {
        Text(text = text, style = SkinthesiaTheme.typography.labelSmall, color = colors.textSecondary)
    }
}

@Composable
private fun ProductCategoryRecommendation.whyItFits(): String = supportsFocusArea?.let { area ->
    stringResource(R.string.product_why_supports_format, stringResource(area.labelRes()))
} ?: stringResource(category.defaultWhyRes())

@Composable
private fun RoutineTime.labelRes(): String = stringResource(
    when (this) {
        RoutineTime.MORNING -> R.string.plan_pillar_morning_title
        RoutineTime.EVENING -> R.string.plan_pillar_evening_title
    },
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1800)
@Composable
private fun ProductRecommendationsScreenPreview() {
    SkinthesiaTheme {
        ProductRecommendationsScreen(
            recommendations = listOf(
                ProductCategoryRecommendation(ProductCategory.CLEANSER, placement = RoutineTime.MORNING),
                ProductCategoryRecommendation(ProductCategory.TARGETED_TREATMENT, supportsFocusArea = VisualIndicator.UNEVEN_TONE, placement = RoutineTime.EVENING, isAddedToRoutine = true),
            ),
            onToggleRecommendation = {},
            onContinue = {},
            onBack = {},
        )
    }
}
