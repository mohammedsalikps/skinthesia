package com.skinthesia.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaBottomNavTab
import com.skinthesia.core.designsystem.component.SkinthesiaBottomNavigationBar
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.feature.combined.SkinPrintRoute
import com.skinthesia.feature.dashboard.DashboardRoute
import com.skinthesia.feature.dashboard.DashboardScreen
import com.skinthesia.feature.learn.LearnRoute
import com.skinthesia.feature.learn.learnScreen
import com.skinthesia.feature.plan.MorningRoutineRoute
import com.skinthesia.feature.plan.PersonalizedPlanRoute
import com.skinthesia.feature.profile.ProfileRoute
import com.skinthesia.feature.profile.profileScreen
import com.skinthesia.feature.progress.ProgressGraphRoute
import kotlinx.serialization.Serializable

@Serializable
data object MainShellRoute

/**
 * Registers the persistent main shell that follows Product Recommendations: a bottom navigation
 * bar over its own inner NavHost for the Home, Profile and Learn tabs. Analyze and Journey have no
 * tab content of their own — they always jump to the existing Camera AI Analysis and Skin Journey
 * screens on the outer graph, reusing them rather than duplicating any state.
 */
fun NavGraphBuilder.mainShellDestination(
    navController: NavHostController,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToJourney: () -> Unit,
) {
    composable<MainShellRoute> {
        MainShellScreen(
            navController = navController,
            onNavigateToAnalysis = onNavigateToAnalysis,
            onNavigateToJourney = onNavigateToJourney,
        )
    }
}

@Composable
private fun MainShellScreen(
    navController: NavHostController,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToJourney: () -> Unit,
) {
    val innerNavController = rememberNavController()
    var selectedTab by remember { mutableStateOf(SkinthesiaBottomNavTab.HOME) }

    fun switchTab(tab: SkinthesiaBottomNavTab, route: Any) {
        selectedTab = tab
        innerNavController.navigate(route) {
            popUpTo(innerNavController.graph.id) { inclusive = false }
            launchSingleTop = true
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(SkinthesiaTheme.colors.background)) {
        Box(modifier = Modifier.weight(1f)) {
            NavHost(navController = innerNavController, startDestination = DashboardRoute) {
                composable<DashboardRoute> {
                    // Home reads the centralized repository directly — no sibling back-stack lookup needed.
                    val appState by SkinthesiaRepository.state.collectAsState()
                    DashboardScreen(
                        skinPrint = appState.skinPrintHistory.lastOrNull(),
                        plan = appState.personalizedPlan,
                        onboardingProfile = appState.onboardingProfile,
                        goalStatement = appState.goalStatement,
                        onViewSkinPrint = { navController.navigate(SkinPrintRoute) },
                        onOpenRoutine = { navController.navigate(MorningRoutineRoute) },
                        onViewPlan = { navController.navigate(PersonalizedPlanRoute) },
                        onAnalyzeMySkin = onNavigateToAnalysis,
                        onCheckJourney = onNavigateToJourney,
                        onCheckIn = { navController.navigate(ProgressGraphRoute) },
                    )
                }
                profileScreen(navController)
                learnScreen(navController)
            }
        }
        SkinthesiaBottomNavigationBar(
            selected = selectedTab,
            labelFor = { tab -> stringResource(tab.labelRes()) },
            onTabClick = { tab ->
                when (tab) {
                    SkinthesiaBottomNavTab.HOME -> switchTab(SkinthesiaBottomNavTab.HOME, DashboardRoute)
                    SkinthesiaBottomNavTab.ANALYZE -> onNavigateToAnalysis()
                    SkinthesiaBottomNavTab.JOURNEY -> onNavigateToJourney()
                    SkinthesiaBottomNavTab.LEARN -> switchTab(SkinthesiaBottomNavTab.LEARN, LearnRoute)
                    SkinthesiaBottomNavTab.PROFILE -> switchTab(SkinthesiaBottomNavTab.PROFILE, ProfileRoute)
                }
            },
        )
    }
}

private fun SkinthesiaBottomNavTab.labelRes(): Int = when (this) {
    SkinthesiaBottomNavTab.HOME -> R.string.nav_home
    SkinthesiaBottomNavTab.ANALYZE -> R.string.nav_analyze
    SkinthesiaBottomNavTab.JOURNEY -> R.string.nav_journey
    SkinthesiaBottomNavTab.LEARN -> R.string.nav_learn
    SkinthesiaBottomNavTab.PROFILE -> R.string.nav_profile
}
