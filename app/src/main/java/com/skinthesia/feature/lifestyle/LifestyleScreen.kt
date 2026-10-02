package com.skinthesia.feature.lifestyle

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
import kotlin.math.roundToInt
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSelectableChip
import com.skinthesia.core.designsystem.component.SkinthesiaSlider
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.ActivityLevel
import com.skinthesia.domain.model.DietPattern
import com.skinthesia.domain.model.LifestyleProfile
import com.skinthesia.domain.model.OutdoorActivityFrequency
import com.skinthesia.domain.model.SleepQuality
import com.skinthesia.domain.model.SmokingStatus
import com.skinthesia.domain.model.StressLevel
import com.skinthesia.domain.model.SunExposure
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object LifestyleRoute

private const val TotalSteps = 7
private const val Step = 7

/**
 * The seventh and final onboarding step in this flow: everyday context grouped into a few casual
 * sections, styled to feel like a lifestyle profile rather than a medical intake form. Every
 * answer is optional.
 */
@Composable
fun LifestyleScreen(
    lifestyle: LifestyleProfile,
    onSleepQualitySelect: (SleepQuality) -> Unit,
    onSleepDurationChange: (Int) -> Unit,
    onWaterIntakeChange: (Int) -> Unit,
    onStressLevelSelect: (StressLevel) -> Unit,
    onSunExposureSelect: (SunExposure) -> Unit,
    onOutdoorActivitySelect: (OutdoorActivityFrequency) -> Unit,
    onActivityLevelSelect: (ActivityLevel) -> Unit,
    onSmokingStatusSelect: (SmokingStatus) -> Unit,
    onDietPatternSelect: (DietPattern) -> Unit,
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
            text = stringResource(R.string.lifestyle_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = stringResource(R.string.lifestyle_subtitle),
            style = typography.body,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.xl))

        LifestyleSection(title = stringResource(R.string.lifestyle_section_sleep)) {
            FieldLabel(stringResource(R.string.lifestyle_sleep_quality))
            FieldSpacer()
            ChipGroup(SleepQuality.entries) { quality ->
                SkinthesiaSelectableChip(
                    label = quality.displayLabel(),
                    selected = quality == lifestyle.sleepQuality,
                    onClick = { onSleepQualitySelect(quality) },
                )
            }
            Spacer(Modifier.height(spacing.md))
            SkinthesiaSlider(
                label = stringResource(R.string.lifestyle_sleep_duration),
                valueLabel = stringResource(R.string.lifestyle_hours_format, lifestyle.sleepDurationHours),
                value = lifestyle.sleepDurationHours.toFloat(),
                onValueChange = { onSleepDurationChange(it.roundToInt()) },
                valueRange = LifestyleProfile.SleepHoursRange,
                steps = 5,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        SectionSpacer()

        LifestyleSection(title = stringResource(R.string.lifestyle_section_wellbeing)) {
            FieldLabel(stringResource(R.string.lifestyle_stress_level))
            FieldSpacer()
            ChipGroup(StressLevel.entries) { level ->
                SkinthesiaSelectableChip(
                    label = level.displayLabel(),
                    selected = level == lifestyle.stressLevel,
                    onClick = { onStressLevelSelect(level) },
                )
            }
            Spacer(Modifier.height(spacing.md))
            SkinthesiaSlider(
                label = stringResource(R.string.lifestyle_water_intake),
                valueLabel = stringResource(R.string.lifestyle_glasses_format, lifestyle.waterIntakeGlasses),
                value = lifestyle.waterIntakeGlasses.toFloat(),
                onValueChange = { onWaterIntakeChange(it.roundToInt()) },
                valueRange = LifestyleProfile.WaterGlassesRange,
                steps = 9,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(spacing.md))
            FieldLabel(stringResource(R.string.lifestyle_activity_level))
            FieldSpacer()
            ChipGroup(ActivityLevel.entries) { level ->
                SkinthesiaSelectableChip(
                    label = level.displayLabel(),
                    selected = level == lifestyle.activityLevel,
                    onClick = { onActivityLevelSelect(level) },
                )
            }
        }
        SectionSpacer()

        LifestyleSection(title = stringResource(R.string.lifestyle_section_environment)) {
            FieldLabel(stringResource(R.string.lifestyle_sun_exposure))
            FieldSpacer()
            ChipGroup(SunExposure.entries) { exposure ->
                SkinthesiaSelectableChip(
                    label = exposure.displayLabel(),
                    selected = exposure == lifestyle.sunExposure,
                    onClick = { onSunExposureSelect(exposure) },
                )
            }
            Spacer(Modifier.height(spacing.md))
            FieldLabel(stringResource(R.string.lifestyle_outdoor_activity))
            FieldSpacer()
            ChipGroup(OutdoorActivityFrequency.entries) { frequency ->
                SkinthesiaSelectableChip(
                    label = frequency.displayLabel(),
                    selected = frequency == lifestyle.outdoorActivity,
                    onClick = { onOutdoorActivitySelect(frequency) },
                )
            }
            Spacer(Modifier.height(spacing.md))
            FieldLabel(stringResource(R.string.lifestyle_smoking_status))
            FieldSpacer()
            ChipGroup(SmokingStatus.entries) { status ->
                SkinthesiaSelectableChip(
                    label = status.displayLabel(),
                    selected = status == lifestyle.smokingStatus,
                    onClick = { onSmokingStatusSelect(status) },
                )
            }
            Spacer(Modifier.height(spacing.md))
            FieldLabel(stringResource(R.string.lifestyle_diet_pattern))
            FieldSpacer()
            ChipGroup(DietPattern.entries) { pattern ->
                SkinthesiaSelectableChip(
                    label = pattern.displayLabel(),
                    selected = pattern == lifestyle.dietPattern,
                    onClick = { onDietPatternSelect(pattern) },
                )
            }
        }
    }
}

@Composable
private fun LifestyleSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(text = title, style = SkinthesiaTheme.typography.sectionHeading, color = SkinthesiaTheme.colors.textPrimary)
        Spacer(Modifier.height(SkinthesiaTheme.spacing.md))
        content()
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, style = SkinthesiaTheme.typography.labelSmall, color = SkinthesiaTheme.colors.textSecondary)
}

@Composable
private fun FieldSpacer() {
    Spacer(Modifier.height(SkinthesiaTheme.spacing.xs))
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
private fun SleepQuality.displayLabel(): String = stringResource(
    when (this) {
        SleepQuality.POOR -> R.string.lifestyle_sleep_quality_poor
        SleepQuality.FAIR -> R.string.lifestyle_sleep_quality_fair
        SleepQuality.GOOD -> R.string.lifestyle_sleep_quality_good
        SleepQuality.EXCELLENT -> R.string.lifestyle_sleep_quality_excellent
    },
)

@Composable
private fun StressLevel.displayLabel(): String = stringResource(
    when (this) {
        StressLevel.LOW -> R.string.lifestyle_stress_low
        StressLevel.MODERATE -> R.string.lifestyle_stress_moderate
        StressLevel.HIGH -> R.string.lifestyle_stress_high
    },
)

@Composable
private fun SunExposure.displayLabel(): String = stringResource(
    when (this) {
        SunExposure.MINIMAL -> R.string.lifestyle_sun_minimal
        SunExposure.MODERATE -> R.string.lifestyle_sun_moderate
        SunExposure.FREQUENT -> R.string.lifestyle_sun_frequent
    },
)

@Composable
private fun OutdoorActivityFrequency.displayLabel(): String = stringResource(
    when (this) {
        OutdoorActivityFrequency.RARELY -> R.string.lifestyle_outdoor_rarely
        OutdoorActivityFrequency.SOMETIMES -> R.string.lifestyle_outdoor_sometimes
        OutdoorActivityFrequency.OFTEN -> R.string.lifestyle_outdoor_often
        OutdoorActivityFrequency.DAILY -> R.string.lifestyle_outdoor_daily
    },
)

@Composable
private fun ActivityLevel.displayLabel(): String = stringResource(
    when (this) {
        ActivityLevel.SEDENTARY -> R.string.lifestyle_activity_sedentary
        ActivityLevel.LIGHT -> R.string.lifestyle_activity_light
        ActivityLevel.MODERATE -> R.string.lifestyle_activity_moderate
        ActivityLevel.ACTIVE -> R.string.lifestyle_activity_active
    },
)

@Composable
private fun SmokingStatus.displayLabel(): String = stringResource(
    when (this) {
        SmokingStatus.NEVER -> R.string.lifestyle_smoking_never
        SmokingStatus.FORMER -> R.string.lifestyle_smoking_former
        SmokingStatus.OCCASIONAL -> R.string.lifestyle_smoking_occasional
        SmokingStatus.REGULAR -> R.string.lifestyle_smoking_regular
    },
)

@Composable
private fun DietPattern.displayLabel(): String = stringResource(
    when (this) {
        DietPattern.BALANCED -> R.string.lifestyle_diet_balanced
        DietPattern.PLANT_BASED -> R.string.lifestyle_diet_plant_based
        DietPattern.PROTEIN_FOCUSED -> R.string.lifestyle_diet_protein_focused
        DietPattern.IRREGULAR -> R.string.lifestyle_diet_irregular
        DietPattern.PREFER_NOT_TO_SAY -> R.string.lifestyle_diet_prefer_not_to_say
    },
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1800)
@Composable
private fun LifestyleScreenPreview() {
    SkinthesiaTheme {
        LifestyleScreen(
            lifestyle = LifestyleProfile(sleepQuality = SleepQuality.GOOD, stressLevel = StressLevel.MODERATE),
            onSleepQualitySelect = {},
            onSleepDurationChange = {},
            onWaterIntakeChange = {},
            onStressLevelSelect = {},
            onSunExposureSelect = {},
            onOutdoorActivitySelect = {},
            onActivityLevelSelect = {},
            onSmokingStatusSelect = {},
            onDietPatternSelect = {},
            onBack = {},
            onContinue = {},
        )
    }
}
