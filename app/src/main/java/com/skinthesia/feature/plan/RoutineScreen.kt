package com.skinthesia.feature.plan

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
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.ProductCategory
import com.skinthesia.domain.model.RoutineStep
import com.skinthesia.domain.model.RoutineTime
import com.skinthesia.domain.model.VisualIndicator
import com.skinthesia.feature.onboarding.OnboardingScaffold
import com.skinthesia.feature.report.labelRes
import kotlinx.serialization.Serializable

/*
 * Morning and Evening Routine are the same screen shape, parameterised by which routine to show;
 * each gets its own route only so navigation can address it individually.
 */
@Serializable
data object MorningRoutineRoute

@Serializable
data object EveningRoutineRoute

/** A premium, scannable step-by-step routine with a completion toggle on each step. */
@Composable
fun RoutineScreen(
    title: String,
    subtitle: String,
    steps: List<RoutineStep>,
    onToggleStep: (Int) -> Unit,
    continueCta: String,
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
            SkinthesiaPrimaryButton(text = continueCta, onClick = onContinue, modifier = Modifier.fillMaxWidth())
        },
    ) {
        Text(
            text = title,
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = subtitle, style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xl))

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            steps.forEach { step -> RoutineStepCard(step = step, onToggle = { onToggleStep(step.order) }) }
        }
    }
}

@Composable
private fun RoutineStepCard(step: RoutineStep, onToggle: () -> Unit) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    SkinthesiaCard(onClick = onToggle) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "%02d".format(step.order),
                style = typography.numberLarge,
                color = if (step.isCompleted) colors.textTertiary else colors.primary,
            )
            Spacer(Modifier.width(spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(step.category.routineStepTitleRes()),
                    style = typography.title,
                    color = if (step.isCompleted) colors.textTertiary else colors.textPrimary,
                )
                Spacer(Modifier.height(spacing.xxs))
                Text(
                    text = step.explanation(),
                    style = typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            Spacer(Modifier.width(spacing.sm))
            CompletionCircle(completed = step.isCompleted)
        }
    }
}

@Composable
private fun RoutineStep.explanation(): String = targetsFocusArea?.let { area ->
    stringResource(R.string.routine_explanation_target_format, stringResource(area.labelRes()))
} ?: stringResource(category.stepExplanationRes())

@Composable
private fun CompletionCircle(completed: Boolean) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    Box(
        modifier = Modifier
            .size(spacing.xl)
            .clip(SkinthesiaTheme.shapes.pill)
            .background(if (completed) colors.positiveContainer else colors.surfaceMuted),
        contentAlignment = Alignment.Center,
    ) {
        if (completed) {
            Icon(
                imageVector = SkinthesiaIcons.Check,
                contentDescription = null,
                tint = colors.onPositiveContainer,
                modifier = Modifier.size(spacing.iconSmall),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1100)
@Composable
private fun RoutineScreenPreview() {
    SkinthesiaTheme {
        RoutineScreen(
            title = "Morning Routine",
            subtitle = "A simple routine to start your day.",
            steps = listOf(
                RoutineStep(order = 1, time = RoutineTime.MORNING, category = ProductCategory.CLEANSER, isCompleted = true),
                RoutineStep(order = 2, time = RoutineTime.MORNING, category = ProductCategory.HYDRATING_SERUM),
                RoutineStep(order = 3, time = RoutineTime.MORNING, category = ProductCategory.SUNSCREEN, targetsFocusArea = VisualIndicator.UNEVEN_TONE),
            ),
            onToggleStep = {},
            continueCta = "View Evening Routine",
            onContinue = {},
            onBack = {},
        )
    }
}
