package com.skinthesia.feature.questionnaire

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
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.ProductSensitivity
import com.skinthesia.domain.model.SkinConcern
import com.skinthesia.domain.model.SkinSensitivity
import com.skinthesia.domain.model.SkinType
import com.skinthesia.domain.model.SkincareRoutineLevel
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object SkinQuestionnaireRoute

private const val TotalSteps = 7
private const val Step = 6

/**
 * The sixth onboarding step: five short, independent questions presented as progressive sections
 * rather than one long form. Every answer is optional, so completing it stays quick.
 */
@Composable
fun SkinQuestionnaireScreen(
    skinType: SkinType?,
    sensitivity: SkinSensitivity?,
    routineLevel: SkincareRoutineLevel?,
    concerns: Set<SkinConcern>,
    productSensitivities: Set<ProductSensitivity>,
    onSkinTypeSelect: (SkinType) -> Unit,
    onSensitivitySelect: (SkinSensitivity) -> Unit,
    onRoutineLevelSelect: (SkincareRoutineLevel) -> Unit,
    onToggleConcern: (SkinConcern) -> Unit,
    onToggleProductSensitivity: (ProductSensitivity) -> Unit,
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
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.questionnaire_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = stringResource(R.string.questionnaire_subtitle),
            style = typography.body,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.xl))

        QuestionSection(title = stringResource(R.string.questionnaire_section_skin_type)) {
            ChipGroup(SkinType.entries) { type ->
                SkinthesiaSelectableChip(label = type.displayLabel(), selected = type == skinType, onClick = { onSkinTypeSelect(type) })
            }
        }
        SectionSpacer()

        QuestionSection(title = stringResource(R.string.questionnaire_section_sensitivity)) {
            ChipGroup(SkinSensitivity.entries) { level ->
                SkinthesiaSelectableChip(label = level.displayLabel(), selected = level == sensitivity, onClick = { onSensitivitySelect(level) })
            }
        }
        SectionSpacer()

        QuestionSection(title = stringResource(R.string.questionnaire_section_routine)) {
            ChipGroup(SkincareRoutineLevel.entries) { level ->
                SkinthesiaSelectableChip(label = level.displayLabel(), selected = level == routineLevel, onClick = { onRoutineLevelSelect(level) })
            }
        }
        SectionSpacer()

        QuestionSection(
            title = stringResource(R.string.questionnaire_section_concerns),
            subtitle = stringResource(R.string.questionnaire_select_all_that_apply),
        ) {
            ChipGroup(SkinConcern.entries) { concern ->
                SkinthesiaSelectableChip(label = concern.displayLabel(), selected = concern in concerns, onClick = { onToggleConcern(concern) })
            }
        }
        SectionSpacer()

        QuestionSection(
            title = stringResource(R.string.questionnaire_section_product_sensitivity),
            subtitle = stringResource(R.string.questionnaire_select_all_that_apply),
        ) {
            ChipGroup(ProductSensitivity.entries) { sensitivityOption ->
                SkinthesiaSelectableChip(
                    label = sensitivityOption.displayLabel(),
                    selected = sensitivityOption in productSensitivities,
                    onClick = { onToggleProductSensitivity(sensitivityOption) },
                )
            }
        }
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
private fun SkinType.displayLabel(): String = stringResource(
    when (this) {
        SkinType.DRY -> R.string.skin_type_dry
        SkinType.OILY -> R.string.skin_type_oily
        SkinType.COMBINATION -> R.string.skin_type_combination
        SkinType.NORMAL -> R.string.skin_type_normal
        SkinType.NOT_SURE -> R.string.skin_type_not_sure
    },
)

@Composable
private fun SkinSensitivity.displayLabel(): String = stringResource(
    when (this) {
        SkinSensitivity.LOW -> R.string.sensitivity_low
        SkinSensitivity.MODERATE -> R.string.sensitivity_moderate
        SkinSensitivity.HIGH -> R.string.sensitivity_high
        SkinSensitivity.NOT_SURE -> R.string.sensitivity_not_sure
    },
)

@Composable
private fun SkincareRoutineLevel.displayLabel(): String = stringResource(
    when (this) {
        SkincareRoutineLevel.MINIMAL -> R.string.routine_minimal
        SkincareRoutineLevel.BASIC -> R.string.routine_basic
        SkincareRoutineLevel.DETAILED -> R.string.routine_detailed
        SkincareRoutineLevel.NONE -> R.string.routine_none
    },
)

@Composable
private fun SkinConcern.displayLabel(): String = stringResource(
    when (this) {
        SkinConcern.ACNE -> R.string.concern_acne
        SkinConcern.DARK_SPOTS -> R.string.concern_dark_spots
        SkinConcern.UNEVEN_TONE -> R.string.concern_uneven_tone
        SkinConcern.DRYNESS -> R.string.concern_dryness
        SkinConcern.OILINESS -> R.string.concern_oiliness
        SkinConcern.TEXTURE -> R.string.concern_texture
        SkinConcern.REDNESS -> R.string.concern_redness
        SkinConcern.FINE_LINES -> R.string.concern_fine_lines
    },
)

@Composable
private fun ProductSensitivity.displayLabel(): String = stringResource(
    when (this) {
        ProductSensitivity.FRAGRANCE -> R.string.product_sensitivity_fragrance
        ProductSensitivity.ESSENTIAL_OILS -> R.string.product_sensitivity_essential_oils
        ProductSensitivity.ALCOHOL -> R.string.product_sensitivity_alcohol
        ProductSensitivity.SULFATES -> R.string.product_sensitivity_sulfates
        ProductSensitivity.NONE_OR_NOT_SURE -> R.string.product_sensitivity_none
    },
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1400)
@Composable
private fun SkinQuestionnaireScreenPreview() {
    SkinthesiaTheme {
        SkinQuestionnaireScreen(
            skinType = SkinType.COMBINATION,
            sensitivity = SkinSensitivity.MODERATE,
            routineLevel = SkincareRoutineLevel.BASIC,
            concerns = setOf(SkinConcern.ACNE, SkinConcern.TEXTURE),
            productSensitivities = setOf(ProductSensitivity.FRAGRANCE),
            onSkinTypeSelect = {},
            onSensitivitySelect = {},
            onRoutineLevelSelect = {},
            onToggleConcern = {},
            onToggleProductSensitivity = {},
            onBack = {},
            onContinue = {},
        )
    }
}
