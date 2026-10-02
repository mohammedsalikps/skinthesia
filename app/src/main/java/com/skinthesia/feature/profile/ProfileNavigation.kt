package com.skinthesia.feature.profile

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.feature.combined.SkinPrintRoute
import com.skinthesia.feature.plan.PersonalizedPlanRoute

/**
 * Registers the Profile tab's destination inside the main shell's inner NavHost. Reads the
 * centralized [SkinthesiaRepository] directly — no sibling back-stack lookup needed. [navController]
 * is the OUTER controller, the one "View SkinPrint" / "View My Plan" jump out on.
 */
fun NavGraphBuilder.profileScreen(navController: NavHostController) {
    composable<ProfileRoute> {
        val appState by SkinthesiaRepository.state.collectAsState()
        ProfileScreen(
            profile = appState.onboardingProfile,
            selectedGoals = appState.selectedGoals,
            goalStatement = appState.goalStatement,
            questionnaire = appState.questionnaire,
            lifestyle = appState.lifestyle,
            skinPrint = appState.skinPrintHistory.lastOrNull(),
            plan = appState.personalizedPlan,
            onViewSkinPrint = { navController.navigate(SkinPrintRoute) },
            onViewPlan = { navController.navigate(PersonalizedPlanRoute) },
        )
    }
}
