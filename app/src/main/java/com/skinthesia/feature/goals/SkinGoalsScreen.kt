package com.skinthesia.feature.goals

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSelectableChip
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.SkinGoal
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object SkinGoalsRoute

private const val TotalSteps = 7
private const val Step = 4

/** The fourth onboarding step: pick any number of skin concerns to focus on. */
@Composable
fun SkinGoalsScreen(
    selectedGoals: Set<SkinGoal>,
    canContinue: Boolean,
    onToggleGoal: (SkinGoal) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(
        onBack = onBack,
        currentStep = Step,
        totalSteps = TotalSteps,
        modifier = modifier,
        bottomBar = {
            SkinthesiaPrimaryButton(
                text = stringResource(R.string.action_continue),
                onClick = onContinue,
                enabled = canContinue,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.goals_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = stringResource(R.string.goals_subtitle),
            style = typography.body,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.xl))

        GoalChips(selectedGoals = selectedGoals, onToggleGoal = onToggleGoal)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GoalChips(selectedGoals: Set<SkinGoal>, onToggleGoal: (SkinGoal) -> Unit) {
    val spacing = SkinthesiaTheme.spacing
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        SkinGoal.entries.forEach { goal ->
            SkinthesiaSelectableChip(
                label = goal.displayLabel(),
                selected = goal in selectedGoals,
                onClick = { onToggleGoal(goal) },
            )
        }
    }
}

@Composable
private fun SkinGoal.displayLabel(): String = stringResource(
    when (this) {
        SkinGoal.ACNE -> R.string.goal_acne
        SkinGoal.DARK_SPOTS -> R.string.goal_dark_spots
        SkinGoal.UNEVEN_TONE -> R.string.goal_uneven_tone
        SkinGoal.DRYNESS -> R.string.goal_dryness
        SkinGoal.OILINESS -> R.string.goal_oiliness
        SkinGoal.TEXTURE -> R.string.goal_texture
        SkinGoal.REDNESS -> R.string.goal_redness
        SkinGoal.FINE_LINES -> R.string.goal_fine_lines
        SkinGoal.OTHER -> R.string.goal_other
    },
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun SkinGoalsScreenPreview() {
    SkinthesiaTheme {
        SkinGoalsScreen(
            selectedGoals = setOf(SkinGoal.ACNE, SkinGoal.TEXTURE),
            canContinue = true,
            onToggleGoal = {},
            onBack = {},
            onContinue = {},
        )
    }
}
