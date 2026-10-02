package com.skinthesia.feature.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.domain.model.buildAdaptivePlan
import kotlinx.serialization.Serializable

@Serializable
data object ProgressGraphRoute

/**
 * Weekly Check-in through Adaptive Plan, sharing one [ProgressViewModel] so the in-progress draft
 * survives moving between them. SkinPrint history and the existing PersonalizedPlan are read
 * directly from the centralized [SkinthesiaRepository] — never recalculated or duplicated.
 */
fun NavGraphBuilder.progressGraph(
    navController: NavHostController,
    onReturnHome: () -> Unit,
) {
    navigation<ProgressGraphRoute>(startDestination = WeeklyCheckInRoute) {
        composable<WeeklyCheckInRoute> { entry ->
            val viewModel = entry.progressViewModel(navController)
            val draft by viewModel.draft.collectAsState()
            WeeklyCheckInScreen(
                draft = draft,
                onSelectSkinFeeling = viewModel::selectSkinFeeling,
                onSelectRoutineConsistency = viewModel::selectRoutineConsistency,
                onToggleChange = viewModel::toggleChange,
                onNoteChange = viewModel::updateNote,
                onBack = { navController.navigateUp() },
                onComplete = {
                    viewModel.completeCheckIn()
                    navController.navigate(ProgressComparisonRoute)
                },
            )
        }
        composable<ProgressComparisonRoute> {
            val appState by SkinthesiaRepository.state.collectAsState()
            ProgressComparisonScreen(
                history = appState.skinPrintHistory,
                onBack = { navController.navigateUp() },
                onContinue = { navController.navigate(AdaptivePlanRoute) },
            )
        }
        composable<AdaptivePlanRoute> { entry ->
            val viewModel = entry.progressViewModel(navController)
            val checkIn by viewModel.checkIn.collectAsState()
            val appState by SkinthesiaRepository.state.collectAsState()
            val plan = appState.personalizedPlan
            if (plan != null) {
                AdaptivePlanScreen(
                    adaptivePlan = buildAdaptivePlan(plan, appState.skinPrintHistory, checkIn),
                    plan = plan,
                    onBack = { navController.navigateUp() },
                    onReturnHome = onReturnHome,
                )
            } else {
                // Reached without an existing plan (e.g. deep navigation out of order): step back rather than show a blank screen.
                LaunchedEffect(Unit) { navController.navigateUp() }
            }
        }
    }
}

@Composable
private fun NavBackStackEntry.progressViewModel(navController: NavHostController): ProgressViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(ProgressGraphRoute) }
    return remember(parentEntry) { ViewModelProvider(parentEntry)[ProgressViewModel::class.java] }
}
