package com.skinthesia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.skinthesia.feature.analysis.CameraAnalysisRoute
import com.skinthesia.feature.analysis.cameraAnalysisScreen
import com.skinthesia.feature.combined.CombinedAnalysisGraphRoute
import com.skinthesia.feature.combined.combinedAnalysisGraph
import com.skinthesia.feature.community.communityScreen
import com.skinthesia.feature.consultation.consultationScreen
import com.skinthesia.feature.journey.SkinJourneyRoute
import com.skinthesia.feature.journey.skinJourneyScreen
import com.skinthesia.feature.learn.educationArticleScreen
import com.skinthesia.feature.marketplace.marketplaceScreen
import com.skinthesia.feature.measurement.MeasurementGraphRoute
import com.skinthesia.feature.measurement.measurementGraph
import com.skinthesia.feature.onboarding.OnboardingGraphRoute
import com.skinthesia.feature.onboarding.onboardingGraph
import com.skinthesia.feature.plan.PersonalizedPlanGraphRoute
import com.skinthesia.feature.plan.personalizedPlanGraph
import com.skinthesia.feature.probe.ProbeGraphRoute
import com.skinthesia.feature.probe.probeConnectionGraph
import com.skinthesia.feature.progress.progressGraph
import com.skinthesia.feature.report.AreasForImprovementRoute
import com.skinthesia.feature.report.SkinReportRoute
import com.skinthesia.feature.report.areasForImprovementScreen
import com.skinthesia.feature.report.skinReportScreen
import com.skinthesia.feature.sensor.SensorAnalysisRoute
import com.skinthesia.feature.sensor.sensorAnalysisScreen
import com.skinthesia.feature.splash.SplashRoute
import com.skinthesia.feature.splash.splashScreen
import com.skinthesia.feature.welcome.WelcomeRoute
import com.skinthesia.feature.welcome.welcomeScreen

/** App-level navigation graph. Each feature registers its own destinations. */
@Composable
fun SkinthesiaNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
    ) {
        splashScreen(
            onFinished = {
                navController.navigate(WelcomeRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            },
        )
        welcomeScreen(
            onGetStarted = { navController.navigate(OnboardingGraphRoute) },
            onSignIn = { navController.navigate(TemporarySignInRoute) },
        )
        onboardingGraph(
            navController = navController,
            onOnboardingComplete = { navController.navigate(ProbeGraphRoute) },
        )
        probeConnectionGraph(
            navController = navController,
            onMeasurementReady = { navController.navigate(MeasurementGraphRoute) },
        )
        measurementGraph(
            navController = navController,
            onAnalysisReady = { navController.navigate(CameraAnalysisRoute) },
        )
        cameraAnalysisScreen(
            navController = navController,
            onContinueToSensorAnalysis = { navController.navigate(SensorAnalysisRoute) },
        )
        sensorAnalysisScreen(
            navController = navController,
            onContinueToCombinedAnalysis = { navController.navigate(CombinedAnalysisGraphRoute) },
        )
        combinedAnalysisGraph(
            navController = navController,
            onViewSkinReport = { navController.navigate(SkinReportRoute) },
        )
        skinReportScreen(
            navController = navController,
            onExploreFocusAreas = { navController.navigate(AreasForImprovementRoute) },
        )
        areasForImprovementScreen(
            navController = navController,
            onContinueToPlan = { navController.navigate(PersonalizedPlanGraphRoute) },
        )
        personalizedPlanGraph(
            navController = navController,
            onContinueToDashboard = {
                navController.navigate(MainShellRoute) { launchSingleTop = true }
            },
        )
        mainShellDestination(
            navController = navController,
            onNavigateToAnalysis = { navController.navigate(CameraAnalysisRoute) { launchSingleTop = true } },
            onNavigateToJourney = { navController.navigate(SkinJourneyRoute) { launchSingleTop = true } },
        )
        skinJourneyScreen(navController = navController)
        educationArticleScreen(navController = navController)
        marketplaceScreen(navController = navController)
        consultationScreen(navController = navController)
        communityScreen(navController = navController)
        progressGraph(
            navController = navController,
            onReturnHome = { navController.navigate(MainShellRoute) { launchSingleTop = true } },
        )
        temporaryDestinations(onBack = { navController.navigateUp() })
    }
}
