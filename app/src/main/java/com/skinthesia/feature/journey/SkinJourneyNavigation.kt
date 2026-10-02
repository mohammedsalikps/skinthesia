package com.skinthesia.feature.journey

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.skinthesia.data.PersistedAppState
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.feature.plan.PersonalizedPlanRoute
import com.skinthesia.feature.progress.ProgressComparisonRoute
import com.skinthesia.feature.progress.ProgressGraphRoute

/**
 * Registers the Skin Journey destination. A single screen, not a nested graph — it reads the
 * centralized repository directly (SkinPrint history, PersonalizedPlan, an optional WeeklyCheckIn)
 * to build an honest, real-events-only timeline; it never creates or stores anything of its own.
 */
fun NavGraphBuilder.skinJourneyScreen(navController: NavHostController) {
    composable<SkinJourneyRoute> {
        val appState by SkinthesiaRepository.state.collectAsState()
        SkinJourneyScreen(
            milestones = journeyMilestones(appState),
            onViewProgress = { navController.navigate(ProgressComparisonRoute) },
            onCheckIn = { navController.navigate(ProgressGraphRoute) },
            onViewPlan = { navController.navigate(PersonalizedPlanRoute) },
        )
    }
}

/** Only ever includes milestones backed by real, already-existing state — never a fabricated one. */
private fun journeyMilestones(appState: PersistedAppState): List<JourneyMilestone> = buildList {
    appState.skinPrintHistory.firstOrNull()?.let { baseline ->
        add(JourneyMilestone(JourneyMilestoneKind.BASELINE_ESTABLISHED, baseline.createdAtMillis))
    }
    // Every SkinPrint after the baseline is a real, legitimate later version — never a fabricated one.
    appState.skinPrintHistory.drop(1).forEach { later ->
        add(JourneyMilestone(JourneyMilestoneKind.SKINPRINT_UPDATED, later.createdAtMillis))
    }
    appState.personalizedPlan?.let { plan ->
        add(JourneyMilestone(JourneyMilestoneKind.PLAN_CREATED, plan.createdAtMillis))
    }
    appState.weeklyCheckIn?.let { checkIn ->
        add(JourneyMilestone(JourneyMilestoneKind.CHECK_IN_COMPLETED, checkIn.timestampMillis))
    }
}
