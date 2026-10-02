package com.skinthesia.feature.plan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.skinthesia.R
import com.skinthesia.domain.model.PersonalizedPlan
import com.skinthesia.domain.model.RoutineTime
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinReport
import com.skinthesia.domain.model.buildPersonalizedPlan
import com.skinthesia.domain.model.buildSkinReport
import com.skinthesia.feature.analysis.CameraAnalysisRoute
import com.skinthesia.feature.analysis.CameraAnalysisViewModel
import com.skinthesia.feature.combined.CombinedAnalysisGraphRoute
import com.skinthesia.feature.combined.CombinedAnalysisUiState
import com.skinthesia.feature.combined.CombinedAnalysisViewModel
import com.skinthesia.feature.onboarding.OnboardingGraphRoute
import com.skinthesia.feature.onboarding.OnboardingViewModel
import kotlinx.serialization.Serializable

@Serializable
data object PersonalizedPlanGraphRoute

/**
 * Personalized Plan through Product Recommendations, sharing one [PersonalizedPlanViewModel] so
 * routine-completion and add-to-routine state persists as the person moves between them. The plan
 * is built once, from the still-alive SkinPrint (Step 9), camera analysis (Step 7) and 12-week
 * goal (onboarding) — never a duplicate copy of any of them.
 */
fun NavGraphBuilder.personalizedPlanGraph(
    navController: NavHostController,
    onContinueToDashboard: () -> Unit,
) {
    navigation<PersonalizedPlanGraphRoute>(startDestination = PersonalizedPlanRoute) {
        composable<PersonalizedPlanRoute> { entry ->
            val viewModel = entry.personalizedPlanViewModel(navController)
            remember(viewModel) {
                loadPersonalizedPlanInput(navController)?.let { (skinPrint, report, goalStatement) ->
                    viewModel.initialize(buildPersonalizedPlan(skinPrint, report, goalStatement))
                }
            }
            val plan by viewModel.plan.collectAsState()
            PlanOrStepBack(plan, navController) {
                PersonalizedPlanScreen(
                    plan = it,
                    onBack = { navController.navigateUp() },
                    onViewMorningRoutine = { navController.navigate(MorningRoutineRoute) },
                )
            }
        }
        composable<MorningRoutineRoute> { entry ->
            val viewModel = entry.personalizedPlanViewModel(navController)
            val plan by viewModel.plan.collectAsState()
            PlanOrStepBack(plan, navController) {
                RoutineScreen(
                    title = stringResource(R.string.routine_morning_title),
                    subtitle = stringResource(R.string.routine_morning_subtitle),
                    steps = it.morningRoutine,
                    onToggleStep = { order -> viewModel.toggleRoutineStep(RoutineTime.MORNING, order) },
                    continueCta = stringResource(R.string.routine_view_evening_cta),
                    onContinue = { navController.navigate(EveningRoutineRoute) },
                    onBack = { navController.navigateUp() },
                )
            }
        }
        composable<EveningRoutineRoute> { entry ->
            val viewModel = entry.personalizedPlanViewModel(navController)
            val plan by viewModel.plan.collectAsState()
            PlanOrStepBack(plan, navController) {
                RoutineScreen(
                    title = stringResource(R.string.routine_evening_title),
                    subtitle = stringResource(R.string.routine_evening_subtitle),
                    steps = it.eveningRoutine,
                    onToggleStep = { order -> viewModel.toggleRoutineStep(RoutineTime.EVENING, order) },
                    continueCta = stringResource(R.string.routine_explore_products_cta),
                    onContinue = { navController.navigate(ProductRecommendationsRoute) },
                    onBack = { navController.navigateUp() },
                )
            }
        }
        composable<ProductRecommendationsRoute> { entry ->
            val viewModel = entry.personalizedPlanViewModel(navController)
            val plan by viewModel.plan.collectAsState()
            PlanOrStepBack(plan, navController) {
                ProductRecommendationsScreen(
                    recommendations = it.recommendations,
                    onToggleRecommendation = viewModel::toggleRecommendation,
                    onContinue = onContinueToDashboard,
                    onBack = { navController.navigateUp() },
                )
            }
        }
    }
}

/** Renders [content] once [plan] is available, or steps back rather than showing a blank screen. */
@Composable
private fun PlanOrStepBack(plan: PersonalizedPlan?, navController: NavHostController, content: @Composable (PersonalizedPlan) -> Unit) {
    if (plan != null) {
        content(plan)
    } else {
        LaunchedEffect(Unit) { navController.navigateUp() }
    }
}

@Composable
private fun NavBackStackEntry.personalizedPlanViewModel(navController: NavHostController): PersonalizedPlanViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(PersonalizedPlanGraphRoute) }
    return remember(parentEntry) { ViewModelProvider(parentEntry)[PersonalizedPlanViewModel::class.java] }
}

/** Reads the SkinPrint, camera analysis and 12-week goal from their still-alive sibling destinations. */
private fun loadPersonalizedPlanInput(navController: NavHostController): Triple<SkinPrint, SkinReport, String>? {
    val skinPrint = runCatching {
        val combinedEntry = navController.getBackStackEntry(CombinedAnalysisGraphRoute)
        val combinedState = ViewModelProvider(combinedEntry)[CombinedAnalysisViewModel::class.java].uiState.value
        (combinedState as? CombinedAnalysisUiState.Complete)?.skinPrint
    }.getOrNull() ?: return null

    val cameraAnalysis = runCatching {
        val cameraEntry = navController.getBackStackEntry(CameraAnalysisRoute)
        ViewModelProvider(cameraEntry)[CameraAnalysisViewModel::class.java].uiState.value.analysis
    }.getOrNull()

    val goalStatement = runCatching {
        val onboardingEntry = navController.getBackStackEntry(OnboardingGraphRoute)
        ViewModelProvider(onboardingEntry)[OnboardingViewModel::class.java].uiState.value.goalStatement
    }.getOrNull().orEmpty()

    return Triple(skinPrint, buildSkinReport(skinPrint, cameraAnalysis), goalStatement)
}
