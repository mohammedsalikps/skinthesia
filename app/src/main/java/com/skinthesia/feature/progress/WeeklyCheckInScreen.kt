package com.skinthesia.feature.progress

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
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSelectableChip
import com.skinthesia.core.designsystem.component.SkinthesiaTextField
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.RoutineConsistency
import com.skinthesia.domain.model.SkinFeeling
import com.skinthesia.domain.model.WeeklyChange
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object WeeklyCheckInRoute

/** A lightweight, quick weekly check-in — every question but the first two is optional. */
@Composable
fun WeeklyCheckInScreen(
    draft: WeeklyCheckInDraft,
    onSelectSkinFeeling: (SkinFeeling) -> Unit,
    onSelectRoutineConsistency: (RoutineConsistency) -> Unit,
    onToggleChange: (WeeklyChange) -> Unit,
    onNoteChange: (String) -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
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
                text = stringResource(R.string.checkin_complete_cta),
                onClick = onComplete,
                enabled = draft.canComplete,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.checkin_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.checkin_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xl))

        QuestionSection(title = stringResource(R.string.checkin_section_feeling)) {
            ChipGroup(SkinFeeling.entries) { feeling ->
                SkinthesiaSelectableChip(
                    label = feeling.displayLabel(),
                    selected = feeling == draft.skinFeeling,
                    onClick = { onSelectSkinFeeling(feeling) },
                )
            }
        }
        SectionSpacer()

        QuestionSection(title = stringResource(R.string.checkin_section_consistency)) {
            ChipGroup(RoutineConsistency.entries) { consistency ->
                SkinthesiaSelectableChip(
                    label = consistency.displayLabel(),
                    selected = consistency == draft.routineConsistency,
                    onClick = { onSelectRoutineConsistency(consistency) },
                )
            }
        }
        SectionSpacer()

        QuestionSection(
            title = stringResource(R.string.checkin_section_changes),
            subtitle = stringResource(R.string.questionnaire_select_all_that_apply),
        ) {
            ChipGroup(WeeklyChange.entries) { change ->
                SkinthesiaSelectableChip(
                    label = change.displayLabel(),
                    selected = change in draft.changes,
                    onClick = { onToggleChange(change) },
                )
            }
        }
        SectionSpacer()

        SkinthesiaTextField(
            value = draft.note,
            onValueChange = onNoteChange,
            label = stringResource(R.string.checkin_note_label),
            placeholder = stringResource(R.string.checkin_note_placeholder),
            singleLine = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun QuestionSection(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    Column {
        Text(text = title, style = typography.sectionHeading, color = colors.textPrimary)
        if (subtitle != null) {
            Spacer(Modifier.height(spacing.xxs))
            Text(text = subtitle, style = typography.caption, color = colors.textTertiary)
        }
        Spacer(Modifier.height(spacing.sm))
        content()
    }
}

@Composable
private fun SectionSpacer() {
    Spacer(Modifier.height(SkinthesiaTheme.spacing.sectionGap))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T> ChipGroup(items: List<T>, chip: @Composable (T) -> Unit) {
    val spacing = SkinthesiaTheme.spacing
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        items.forEach { chip(it) }
    }
}

@Composable
private fun SkinFeeling.displayLabel(): String = stringResource(
    when (this) {
        SkinFeeling.BETTER -> R.string.checkin_feeling_better
        SkinFeeling.ABOUT_THE_SAME -> R.string.checkin_feeling_same
        SkinFeeling.MORE_CHALLENGING -> R.string.checkin_feeling_challenging
    },
)

@Composable
private fun RoutineConsistency.displayLabel(): String = stringResource(
    when (this) {
        RoutineConsistency.VERY_CONSISTENT -> R.string.checkin_consistency_very
        RoutineConsistency.MOSTLY_CONSISTENT -> R.string.checkin_consistency_mostly
        RoutineConsistency.SOMEWHAT_CONSISTENT -> R.string.checkin_consistency_somewhat
        RoutineConsistency.STRUGGLED -> R.string.checkin_consistency_struggled
    },
)

@Composable
private fun WeeklyChange.displayLabel(): String = stringResource(
    when (this) {
        WeeklyChange.NOTHING_SIGNIFICANT -> R.string.checkin_change_nothing
        WeeklyChange.NEW_PRODUCTS -> R.string.checkin_change_products
        WeeklyChange.ROUTINE_CHANGED -> R.string.checkin_change_routine
        WeeklyChange.LIFESTYLE_CHANGED -> R.string.checkin_change_lifestyle
        WeeklyChange.ENVIRONMENT_CHANGED -> R.string.checkin_change_environment
    },
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1300)
@Composable
private fun WeeklyCheckInScreenPreview() {
    SkinthesiaTheme {
        WeeklyCheckInScreen(
            draft = WeeklyCheckInDraft(
                skinFeeling = SkinFeeling.BETTER,
                routineConsistency = RoutineConsistency.MOSTLY_CONSISTENT,
                changes = setOf(WeeklyChange.NEW_PRODUCTS),
            ),
            onSelectSkinFeeling = {},
            onSelectRoutineConsistency = {},
            onToggleChange = {},
            onNoteChange = {},
            onBack = {},
            onComplete = {},
        )
    }
}
