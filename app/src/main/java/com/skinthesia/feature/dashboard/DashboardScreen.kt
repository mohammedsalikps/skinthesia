package com.skinthesia.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaCardStyle
import com.skinthesia.core.designsystem.component.SkinthesiaProgressBar
import com.skinthesia.core.designsystem.component.SkinthesiaSectionHeader
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.OnboardingProfile
import com.skinthesia.domain.model.PersonalizedPlan
import com.skinthesia.domain.model.RoutineStep
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinPrintSources
import com.skinthesia.domain.model.VisualIndicator
import com.skinthesia.feature.combined.CompositeScoreRing
import com.skinthesia.feature.plan.routineStepTitleRes
import com.skinthesia.feature.report.labelRes
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data object DashboardRoute

private const val TotalGoalWeeks = 12
private const val MillisPerWeek = 7L * 24 * 60 * 60 * 1000

/**
 * The Home tab: a calm overview built entirely from existing SkinPrint, Personalized Plan and
 * onboarding data. It never recalculates or invents any of it — where a piece of data doesn't yet
 * exist, each section shows a plain, honest empty state instead.
 */
@Composable
fun DashboardScreen(
    skinPrint: SkinPrint?,
    plan: PersonalizedPlan?,
    onboardingProfile: OnboardingProfile?,
    goalStatement: String,
    onViewSkinPrint: () -> Unit,
    onOpenRoutine: () -> Unit,
    onViewPlan: () -> Unit,
    onAnalyzeMySkin: () -> Unit,
    onCheckJourney: () -> Unit,
    onCheckIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

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

                // Greeting
                Text(text = stringResource(R.string.dashboard_greeting_title), style = typography.headline, color = colors.textPrimary)
                Spacer(Modifier.height(spacing.xs))
                Text(text = greeting(onboardingProfile), style = typography.body, color = colors.textSecondary)
                Spacer(Modifier.height(spacing.xl))

                // 1. Current Skin Status
                SkinthesiaSectionHeader(
                    title = stringResource(R.string.dashboard_section_skin_status),
                    actionLabel = if (skinPrint != null) stringResource(R.string.dashboard_view_skinprint_cta) else null,
                    onAction = if (skinPrint != null) onViewSkinPrint else null,
                )
                Spacer(Modifier.height(spacing.sm))
                if (skinPrint != null) {
                    SkinthesiaCard {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(text = stringResource(R.string.skinprint_title), style = typography.title, color = colors.textPrimary)
                            Spacer(Modifier.height(spacing.sm))
                            CompositeScoreRing(score = skinPrint.compositeScore, modifier = Modifier.size(120.dp))
                            Spacer(Modifier.height(spacing.xs))
                            Text(text = stringResource(R.string.skinprint_tracking_label), style = typography.caption, color = colors.textTertiary)
                            Spacer(Modifier.height(spacing.sm))
                            Text(
                                text = stringResource(R.string.dashboard_skin_status_interpretation),
                                style = typography.bodySmall,
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    Text(text = stringResource(R.string.dashboard_skin_status_empty), style = typography.bodySmall, color = colors.textSecondary)
                }
                Spacer(Modifier.height(spacing.lg))

                // Weekly Check-in prompt
                SkinthesiaCard(style = SkinthesiaCardStyle.Tonal) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.dashboard_checkin_title), style = typography.label, color = colors.textPrimary)
                            Spacer(Modifier.height(spacing.xxs))
                            Text(text = stringResource(R.string.dashboard_checkin_body), style = typography.bodySmall, color = colors.textSecondary)
                        }
                        Spacer(Modifier.width(spacing.sm))
                        SkinthesiaSecondaryButton(text = stringResource(R.string.dashboard_checkin_cta), onClick = onCheckIn)
                    }
                }
                Spacer(Modifier.height(spacing.lg))

                // 2. Today's Focus
                SkinthesiaSectionHeader(title = stringResource(R.string.dashboard_section_focus))
                Spacer(Modifier.height(spacing.sm))
                val topFocusArea = plan?.priorityFocusAreas?.firstOrNull()
                if (plan != null && topFocusArea != null) {
                    SkinthesiaCard {
                        Text(text = stringResource(topFocusArea.labelRes()), style = typography.title, color = colors.textPrimary)
                        Spacer(Modifier.height(spacing.xxs))
                        Text(text = stringResource(R.string.dashboard_focus_supporting), style = typography.bodySmall, color = colors.textSecondary)
                    }
                } else {
                    Text(text = stringResource(R.string.dashboard_focus_empty), style = typography.bodySmall, color = colors.textSecondary)
                }
                Spacer(Modifier.height(spacing.lg))

                // 3. Today's Routine
                SkinthesiaSectionHeader(
                    title = stringResource(R.string.dashboard_section_routine),
                    actionLabel = if (plan != null) stringResource(R.string.dashboard_open_routine_cta) else null,
                    onAction = if (plan != null) onOpenRoutine else null,
                )
                Spacer(Modifier.height(spacing.sm))
                if (plan != null) {
                    SkinthesiaCard {
                        Text(
                            text = stringResource(R.string.dashboard_routine_context_morning).uppercase(Locale.getDefault()),
                            style = typography.overline,
                            color = colors.textTertiary,
                        )
                        Spacer(Modifier.height(spacing.sm))
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                            plan.morningRoutine.sortedBy { it.order }.forEach { step -> RoutineStepLabel(step) }
                        }
                        Spacer(Modifier.height(spacing.sm))
                        val completed = plan.morningRoutine.count { it.isCompleted }
                        Text(
                            text = stringResource(R.string.dashboard_routine_progress_format, completed, plan.morningRoutine.size),
                            style = typography.caption,
                            color = colors.textTertiary,
                        )
                    }
                } else {
                    Text(text = stringResource(R.string.dashboard_routine_empty), style = typography.bodySmall, color = colors.textSecondary)
                }
                Spacer(Modifier.height(spacing.lg))

                // 4. 12-Week Goal
                SkinthesiaSectionHeader(title = stringResource(R.string.dashboard_section_goal))
                Spacer(Modifier.height(spacing.sm))
                if (goalStatement.isNotBlank()) {
                    SkinthesiaCard {
                        Text(
                            text = "“$goalStatement”",
                            style = typography.quote,
                            color = colors.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(spacing.md))
                        val weeksElapsed = weeksElapsed(skinPrint)
                        SkinthesiaProgressBar(progress = weeksElapsed / TotalGoalWeeks.toFloat())
                        Spacer(Modifier.height(spacing.xs))
                        Text(
                            text = stringResource(
                                if (weeksElapsed <= 0) R.string.dashboard_goal_baseline_today else R.string.dashboard_goal_baseline_established,
                            ),
                            style = typography.caption,
                            color = colors.textTertiary,
                        )
                    }
                } else {
                    Text(text = stringResource(R.string.dashboard_goal_empty), style = typography.bodySmall, color = colors.textSecondary)
                }
                Spacer(Modifier.height(spacing.lg))

                // 5. Quick Actions
                SkinthesiaSectionHeader(title = stringResource(R.string.dashboard_section_quick_actions))
                Spacer(Modifier.height(spacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    QuickActionItem(
                        icon = SkinthesiaIcons.Analyze,
                        label = stringResource(R.string.dashboard_action_analyze),
                        onClick = onAnalyzeMySkin,
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionItem(
                        icon = SkinthesiaIcons.Check,
                        label = stringResource(R.string.dashboard_action_view_plan),
                        onClick = onViewPlan,
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionItem(
                        icon = SkinthesiaIcons.Journey,
                        label = stringResource(R.string.dashboard_action_check_journey),
                        onClick = onCheckJourney,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(spacing.lg))

                // 6. Insight
                SkinthesiaSectionHeader(title = stringResource(R.string.dashboard_section_insight))
                Spacer(Modifier.height(spacing.sm))
                SkinthesiaCard(style = SkinthesiaCardStyle.Tonal) {
                    Text(
                        text = stringResource(if (plan != null) R.string.dashboard_insight_with_plan else R.string.dashboard_insight_generic),
                        style = typography.body,
                        color = colors.textSecondary,
                    )
                }
                Spacer(Modifier.height(spacing.xl))
            }
        }
    }
}

@Composable
private fun RoutineStepLabel(step: RoutineStep) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val symbol = if (step.isCompleted) "✓" else "○"
    val color = if (step.isCompleted) colors.textPrimary else colors.textTertiary
    Text(text = "$symbol ${stringResource(step.category.routineStepTitleRes())}", style = typography.body, color = color)
}

@Composable
private fun QuickActionItem(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    SkinthesiaCard(
        modifier = modifier,
        style = SkinthesiaCardStyle.Outlined,
        onClick = onClick,
        contentPadding = PaddingValues(vertical = spacing.md, horizontal = spacing.xs),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(spacing.iconLarge))
            Spacer(Modifier.height(spacing.xs))
            Text(text = label, style = typography.labelSmall, color = colors.textPrimary, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

/** A personalized greeting using the existing profile's name if one was collected; never invents one. */
@Composable
private fun greeting(profile: OnboardingProfile?): String {
    val name = profile?.name?.takeIf { it.isNotBlank() }
    return if (name != null) stringResource(R.string.dashboard_greeting_named, name) else stringResource(R.string.dashboard_greeting_generic)
}

/** Real elapsed time since the SkinPrint baseline was created, in whole weeks up to the 12-week goal window — never a fabricated improvement figure. */
private fun weeksElapsed(skinPrint: SkinPrint?): Int {
    val createdAtMillis = skinPrint?.createdAtMillis ?: return 0
    val elapsedMillis = (System.currentTimeMillis() - createdAtMillis).coerceAtLeast(0)
    return (elapsedMillis / MillisPerWeek).toInt().coerceIn(0, TotalGoalWeeks)
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1500)
@Composable
private fun DashboardScreenPreview() {
    SkinthesiaTheme {
        DashboardScreen(
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
                priorityFocusAreas = listOf(VisualIndicator.UNEVEN_TONE, VisualIndicator.TEXTURE),
                twelveWeekGoalStatement = "Clearer, more even skin",
                morningRoutine = listOf(
                    RoutineStep(1, com.skinthesia.domain.model.RoutineTime.MORNING, com.skinthesia.domain.model.ProductCategory.CLEANSER, isCompleted = true),
                    RoutineStep(2, com.skinthesia.domain.model.RoutineTime.MORNING, com.skinthesia.domain.model.ProductCategory.HYDRATING_SERUM, isCompleted = true),
                    RoutineStep(3, com.skinthesia.domain.model.RoutineTime.MORNING, com.skinthesia.domain.model.ProductCategory.SUNSCREEN),
                ),
                eveningRoutine = emptyList(),
                recommendations = emptyList(),
            ),
            onboardingProfile = OnboardingProfile(name = "Sam", ageInput = "29"),
            goalStatement = "Clearer, more even skin",
            onViewSkinPrint = {},
            onOpenRoutine = {},
            onViewPlan = {},
            onAnalyzeMySkin = {},
            onCheckJourney = {},
            onCheckIn = {},
        )
    }
}
