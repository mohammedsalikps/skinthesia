package com.skinthesia.feature.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSelectableChip
import com.skinthesia.core.designsystem.component.SkinthesiaTextField
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.Gender
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object CreateProfileRoute

private const val TotalSteps = 7
private const val Step = 1

/** The first onboarding step: a short, minimal form collecting a name, age and how to refer to the person. */
@Composable
fun CreateProfileScreen(
    name: String,
    ageInput: String,
    gender: Gender?,
    canContinue: Boolean,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onGenderSelect: (Gender) -> Unit,
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
            text = stringResource(R.string.profile_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = stringResource(R.string.profile_subtitle),
            style = typography.body,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.xl))

        SkinthesiaTextField(
            value = name,
            onValueChange = onNameChange,
            label = stringResource(R.string.profile_name_label),
            placeholder = stringResource(R.string.profile_name_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(spacing.md))

        SkinthesiaTextField(
            value = ageInput,
            onValueChange = onAgeChange,
            label = stringResource(R.string.profile_age_label),
            placeholder = stringResource(R.string.profile_age_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(spacing.lg))

        Text(text = stringResource(R.string.profile_gender_label), style = typography.labelSmall, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xs))
        GenderChips(selected = gender, onSelect = onGenderSelect)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GenderChips(selected: Gender?, onSelect: (Gender) -> Unit) {
    val spacing = SkinthesiaTheme.spacing
    val options = listOf(
        Gender.WOMAN to stringResource(R.string.profile_gender_woman),
        Gender.MAN to stringResource(R.string.profile_gender_man),
        Gender.NON_BINARY to stringResource(R.string.profile_gender_non_binary),
        Gender.PREFER_NOT_TO_SAY to stringResource(R.string.profile_gender_prefer_not_to_say),
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        options.forEach { (gender, label) ->
            SkinthesiaSelectableChip(label = label, selected = gender == selected, onClick = { onSelect(gender) })
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun CreateProfileScreenPreview() {
    SkinthesiaTheme {
        CreateProfileScreen(
            name = "Ava",
            ageInput = "28",
            gender = Gender.WOMAN,
            canContinue = true,
            onNameChange = {},
            onAgeChange = {},
            onGenderSelect = {},
            onBack = {},
            onContinue = {},
        )
    }
}
