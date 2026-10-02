package com.skinthesia.feature.analysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.skinthesia.feature.onboarding.OnboardingGraphRoute
import com.skinthesia.feature.onboarding.OnboardingViewModel

/**
 * Registers the Camera AI Analysis destination. It is a single screen, not a nested graph — the
 * SAME destination whether reached via the onboarding flow or via the Analyze bottom-nav tab, so
 * there is only ever one analysis pipeline. The captured selfie's identity is read from the
 * still-alive onboarding graph's back-stack entry, wrapped in [runCatching] so a person who somehow
 * reaches this screen without completed onboarding state sees an empty photo id (handled by
 * [CameraAnalysisViewModel]/[CameraAnalysisScreen]'s existing states) rather than a crash.
 */
fun NavGraphBuilder.cameraAnalysisScreen(
    navController: NavHostController,
    onContinueToSensorAnalysis: () -> Unit,
) {
    composable<CameraAnalysisRoute> { entry ->
        val viewModel: CameraAnalysisViewModel = remember { ViewModelProvider(entry)[CameraAnalysisViewModel::class.java] }
        val state by viewModel.uiState.collectAsState()
        val photoId = remember {
            runCatching {
                val onboardingEntry = navController.getBackStackEntry(OnboardingGraphRoute)
                ViewModelProvider(onboardingEntry)[OnboardingViewModel::class.java].uiState.value.photo?.id
            }.getOrNull().orEmpty()
        }
        CameraAnalysisScreen(
            status = state.analysis.status,
            progress = state.progress,
            indicators = state.analysis.indicators,
            onStartAnalysis = { viewModel.analyze(photoId) },
            onRetry = { viewModel.retry(photoId) },
            onBack = { navController.navigateUp() },
            onContinue = onContinueToSensorAnalysis,
        )
    }
}
