package com.skinthesia.feature.combined

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
import com.skinthesia.ai.combined.CombinedAnalysisInput
import com.skinthesia.feature.analysis.CameraAnalysisRoute
import com.skinthesia.feature.analysis.CameraAnalysisViewModel
import com.skinthesia.feature.measurement.MeasurementGraphRoute
import com.skinthesia.feature.measurement.MeasurementSessionViewModel
import com.skinthesia.feature.onboarding.OnboardingGraphRoute
import com.skinthesia.feature.onboarding.OnboardingViewModel
import com.skinthesia.feature.sensor.SensorAnalysisRoute
import com.skinthesia.feature.sensor.SensorAnalysisUiState
import com.skinthesia.feature.sensor.SensorAnalysisViewModel
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data object CombinedAnalysisGraphRoute

/**
 * Combined AI Analysis through SkinPrint™, sharing one [CombinedAnalysisViewModel]. The four
 * inputs it needs are read from their own still-alive sibling destinations rather than duplicated:
 * onboarding (profile, questionnaire, lifestyle, goals), the measurement graph, the camera
 * analysis screen and the sensor analysis screen.
 */
fun NavGraphBuilder.combinedAnalysisGraph(
    navController: NavHostController,
    onViewSkinReport: () -> Unit,
) {
    navigation<CombinedAnalysisGraphRoute>(startDestination = CombinedAnalysisRoute) {
        composable<CombinedAnalysisRoute> { entry ->
            val viewModel = entry.combinedAnalysisViewModel(navController)
            val uiState by viewModel.uiState.collectAsState()
            val input = remember { entry.gatherCombinedAnalysisInput(navController) }
            CombinedAnalysisScreen(
                uiState = uiState,
                onStart = { viewModel.start(input) },
                onRetry = viewModel::retry,
                onBack = { navController.navigateUp() },
                onComplete = { navController.navigate(SkinPrintRoute) },
            )
        }
        composable<SkinPrintRoute> { entry ->
            val viewModel = entry.combinedAnalysisViewModel(navController)
            val uiState by viewModel.uiState.collectAsState()
            val skinPrint = (uiState as? CombinedAnalysisUiState.Complete)?.skinPrint
            if (skinPrint != null) {
                SkinPrintScreen(
                    skinPrint = skinPrint,
                    onBack = { navController.navigateUp() },
                    onViewSkinReport = onViewSkinReport,
                )
            } else {
                // Reached without a result (e.g. deep navigation out of order): step back rather than show a blank screen.
                LaunchedEffect(Unit) { navController.navigateUp() }
            }
        }
    }
}

@Composable
private fun NavBackStackEntry.combinedAnalysisViewModel(navController: NavHostController): CombinedAnalysisViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(CombinedAnalysisGraphRoute) }
    return remember(parentEntry) { ViewModelProvider(parentEntry)[CombinedAnalysisViewModel::class.java] }
}

/** Reads the four existing data sources this analysis needs, without duplicating any of them. */
private fun NavBackStackEntry.gatherCombinedAnalysisInput(navController: NavHostController): CombinedAnalysisInput? = runCatching {
    val onboardingEntry = navController.getBackStackEntry(OnboardingGraphRoute)
    val onboardingState = ViewModelProvider(onboardingEntry)[OnboardingViewModel::class.java].uiState.value

    val measurementSession = runCatching {
        val measurementEntry = navController.getBackStackEntry(MeasurementGraphRoute)
        ViewModelProvider(measurementEntry)[MeasurementSessionViewModel::class.java].uiState.value.session
    }.getOrNull()

    val cameraAnalysis = runCatching {
        val cameraEntry = navController.getBackStackEntry(CameraAnalysisRoute)
        ViewModelProvider(cameraEntry)[CameraAnalysisViewModel::class.java].uiState.value.analysis
    }.getOrNull()

    val sensorAnalysis = runCatching {
        val sensorEntry = navController.getBackStackEntry(SensorAnalysisRoute)
        val sensorState = ViewModelProvider(sensorEntry)[SensorAnalysisViewModel::class.java].uiState.value
        (sensorState as? SensorAnalysisUiState.Complete)?.result
    }.getOrNull()

    CombinedAnalysisInput(
        profileId = measurementSession?.profileId ?: UUID.randomUUID().toString(),
        cameraAnalysis = cameraAnalysis,
        sensorAnalysis = sensorAnalysis,
        measurementSession = measurementSession,
        questionnaire = onboardingState.questionnaire,
        lifestyle = onboardingState.lifestyle,
        goals = onboardingState.selectedGoals,
    )
}.getOrNull()
