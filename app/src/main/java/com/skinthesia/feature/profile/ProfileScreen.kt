package com.skinthesia.feature.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaCardStyle
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSectionHeader
import com.skinthesia.core.designsystem.component.SkinthesiaSelectableChip
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.ActivityLevel
import com.skinthesia.domain.model.DietPattern
import com.skinthesia.domain.model.Gender
import com.skinthesia.domain.model.LifestyleProfile
import com.skinthesia.domain.model.OnboardingProfile
import com.skinthesia.domain.model.OutdoorActivityFrequency
import com.skinthesia.domain.model.PersonalizedPlan
import com.skinthesia.domain.model.SkinGoal
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinPrintSources
import com.skinthesia.domain.model.SkinQuestionnaire
import com.skinthesia.domain.model.SkinSensitivity
import com.skinthesia.domain.model.SkinType
import com.skinthesia.domain.model.SkincareRoutineLevel
import com.skinthesia.domain.model.SleepQuality
import com.skinthesia.domain.model.SmokingStatus
import com.skinthesia.domain.model.StressLevel
import com.skinthesia.domain.model.SunExposure
import com.skinthesia.domain.model.VisualIndicator
import com.skinthesia.feature.combined.CompositeScoreRing
import com.skinthesia.feature.report.labelRes
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data object ProfileRoute

/**
 * A clean personal summary built entirely from existing onboarding, SkinPrint and plan data — no
 * second copy of any of it, and nothing invented for fields the person never provided.
 */
@Composable
fun ProfileScreen(
    profile: OnboardingProfile?,
    selectedGoals: Set<SkinGoal>,
    goalStatement: String,
    questionnaire: SkinQuestionnaire?,
    lifestyle: LifestyleProfile?,
    skinPrint: SkinPrint?,
    plan: PersonalizedPlan?,
    onViewSkinPrint: () -> Unit,
    onViewPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val notProvided = stringResource(R.string.profile_not_provided)

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = spacing.contentMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenHorizontal),
            ) {
                Spacer(Modifier.height(spacing.md))
                Text(
                    text = stringResource(R.string.profile_screen_title),
                    style = typography.headline,
                    color = colors.textPrimary,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(spacing.xl))

                // Personal info
                SkinthesiaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        InfoRow(stringResource(R.string.profile_name_label), profile?.name?.takeIf { it.isNotBlank() } ?: notProvided)
                        InfoRow(stringResource(R.string.profile_age_label), profile?.age?.toString() ?: notProvided)
                        InfoRow(stringResource(R.string.profile_gender_label), profile?.gender?.displayLabel() ?: notProvided)
                    }
                }
                Spacer(Modifier.height(spacing.lg))

                // Your SkinPrint
                SkinthesiaSectionHeader(title = stringResource(R.string.skinprint_title))
                Spacer(Modifier.height(spacing.sm))
                if (skinPrint != null) {
                    SkinthesiaCard {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            CompositeScoreRing(score = skinPrint.compositeScore, modifier = Modifier.size(120.dp))
                            Spacer(Modifier.height(spacing.xs))
                            Text(text = stringResource(R.string.skinprint_tracking_label), style = typography.caption, color = colors.textTertiary)
                            Spacer(Modifier.height(spacing.sm))
                            Text(
                                text = stringResource(R.string.profile_skinprint_created_format, formatDate(skinPrint.createdAtMillis)),
                                style = typography.bodySmall,
                                color = colors.textSecondary,
                            )
                            Text(
                                text = stringResource(R.string.profile_skinprint_version_format, skinPrint.version),
                                style = typography.bodySmall,
                                color = colors.textSecondary,
                            )
                            Spacer(Modifier.height(spacing.sm))
                            SkinthesiaSecondaryButton(text = stringResource(R.string.dashboard_view_skinprint_cta), onClick = onViewSkinPrint)
                        }
                    }
                } else {
                    Text(text = stringResource(R.string.dashboard_skin_status_empty), style = typography.bodySmall, color = colors.textSecondary)
                }
                Spacer(Modifier.height(spacing.lg))

                // My Goals
                SkinthesiaSectionHeader(title = stringResource(R.string.profile_section_goals))
                Spacer(Modifier.height(spacing.sm))
                if (selectedGoals.isEmpty() && goalStatement.isBlank()) {
                    Text(text = stringResource(R.string.profile_goals_empty), style = typography.bodySmall, color = colors.textSecondary)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        if (selectedGoals.isNotEmpty()) {
                            ReadOnlyChipRow(selectedGoals.toList().map { it.displayLabel() })
                        }
                        if (goalStatement.isNotBlank()) {
                            Text(text = "“$goalStatement”", style = typography.quote, color = colors.primary)
                        }
                    }
                }
                Spacer(Modifier.height(spacing.lg))

                // My Skin Profile
                SkinthesiaSectionHeader(title = stringResource(R.string.profile_section_skin_profile))
                Spacer(Modifier.height(spacing.sm))
                val skinType = questionnaire?.skinType
                val sensitivity = questionnaire?.sensitivity
                val routineLevel = questionnaire?.routineLevel
                if (skinType == null && sensitivity == null && routineLevel == null) {
                    Text(text = stringResource(R.string.profile_skin_profile_empty), style = typography.bodySmall, color = colors.textSecondary)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        if (skinType != null) InfoRow(stringResource(R.string.profile_field_skin_type), skinType.displayLabel())
                        if (sensitivity != null) InfoRow(stringResource(R.string.profile_field_sensitivity), sensitivity.displayLabel())
                        if (routineLevel != null) InfoRow(stringResource(R.string.profile_field_routine), routineLevel.displayLabel())
                    }
                }
                Spacer(Modifier.height(spacing.lg))

                // My Lifestyle
                SkinthesiaSectionHeader(title = stringResource(R.string.profile_section_lifestyle))
                Spacer(Modifier.height(spacing.sm))
                val lifestyleLabels = listOfNotNull(
                    lifestyle?.sleepQuality?.displayLabel(),
                    lifestyle?.stressLevel?.displayLabel(),
                    lifestyle?.sunExposure?.displayLabel(),
                    lifestyle?.outdoorActivity?.displayLabel(),
                    lifestyle?.activityLevel?.displayLabel(),
                    lifestyle?.smokingStatus?.displayLabel(),
                    lifestyle?.dietPattern?.displayLabel(),
                )
                if (lifestyleLabels.isEmpty()) {
                    Text(text = stringResource(R.string.profile_lifestyle_empty), style = typography.bodySmall, color = colors.textSecondary)
                } else {
                    ReadOnlyChipRow(lifestyleLabels)
                }
                Spacer(Modifier.height(spacing.lg))

                // My Plan
                SkinthesiaSectionHeader(
                    title = stringResource(R.string.profile_section_plan),
                    actionLabel = if (plan != null) stringResource(R.string.dashboard_action_view_plan) else null,
                    onAction = if (plan != null) onViewPlan else null,
                )
                Spacer(Modifier.height(spacing.sm))
                if (plan != null && plan.priorityFocusAreas.isNotEmpty()) {
                    SkinthesiaCard {
                        ReadOnlyChipRow(plan.priorityFocusAreas.map { stringResource(it.labelRes()) })
                    }
                } else {
                    Text(text = stringResource(R.string.profile_plan_empty), style = typography.bodySmall, color = colors.textSecondary)
                }
                Spacer(Modifier.height(spacing.lg))

                // Settings
                SkinthesiaSectionHeader(title = stringResource(R.string.profile_section_settings))
                Spacer(Modifier.height(spacing.sm))
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    SettingsRow(stringResource(R.string.profile_settings_notifications_title), stringResource(R.string.profile_settings_notifications_body))
                    SettingsRow(stringResource(R.string.profile_settings_privacy_title), stringResource(R.string.profile_settings_privacy_body))
                    SettingsRow(stringResource(R.string.profile_settings_about_title), stringResource(R.string.profile_settings_about_body))
                }
                Spacer(Modifier.height(spacing.xl))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = typography.bodySmall, color = colors.textSecondary)
        Text(text = value, style = typography.body, color = colors.textPrimary)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReadOnlyChipRow(labels: List<String>) {
    val spacing = SkinthesiaTheme.spacing
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        labels.forEach { label -> SkinthesiaSelectableChip(label = label, selected = true, onClick = {}, enabled = false) }
    }
}

@Composable
private fun SettingsRow(title: String, body: String) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    SkinthesiaCard(style = SkinthesiaCardStyle.Outlined) {
        Text(text = title, style = typography.label, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = body, style = typography.bodySmall, color = colors.textSecondary)
    }
}

private fun formatDate(millis: Long): String = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))

@Composable
private fun Gender.displayLabel(): String = stringResource(
    when (this) {
        Gender.WOMAN -> R.string.profile_gender_woman
        Gender.MAN -> R.string.profile_gender_man
        Gender.NON_BINARY -> R.string.profile_gender_non_binary
        Gender.PREFER_NOT_TO_SAY -> R.string.profile_gender_prefer_not_to_say
    },
)

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

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 2200)
@Composable
private fun ProfileScreenPreview() {
    SkinthesiaTheme {
        ProfileScreen(
            profile = OnboardingProfile(name = "Ava", ageInput = "28", gender = Gender.WOMAN),
            selectedGoals = setOf(SkinGoal.ACNE, SkinGoal.TEXTURE),
            goalStatement = "Clearer, more even skin",
            questionnaire = SkinQuestionnaire(
                skinType = SkinType.COMBINATION,
                sensitivity = SkinSensitivity.MODERATE,
                routineLevel = SkincareRoutineLevel.BASIC,
            ),
            lifestyle = LifestyleProfile(sleepQuality = SleepQuality.GOOD, stressLevel = StressLevel.MODERATE),
            skinPrint = SkinPrint(
                id = "preview",
                profileId = "preview-profile",
                createdAtMillis = System.currentTimeMillis(),
                updatedAtMillis = System.currentTimeMillis(),
                compositeScore = 68,
                sources = SkinPrintSources(
                    cameraAnalysisId = "camera-1",
                    sensorAnalysisId = "sensor-1",
                    measurementSessionId = "measure-1",
                    goalIds = emptyList(),
                ),
            ),
            plan = PersonalizedPlan(
                skinPrintId = "preview",
                priorityFocusAreas = listOf(VisualIndicator.UNEVEN_TONE),
                twelveWeekGoalStatement = "Clearer, more even skin",
                morningRoutine = emptyList(),
                eveningRoutine = emptyList(),
                recommendations = emptyList(),
            ),
            onViewSkinPrint = {},
            onViewPlan = {},
        )
    }
}
