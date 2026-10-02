package com.skinthesia.feature.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.skinthesia.feature.measurement.MeasurementGraphRoute
import com.skinthesia.feature.measurement.MeasurementSessionViewModel

/**
 * Registers the Sensor Analysis destination. It is a single screen, not a nested graph. The
 * completed measurement session is read from the still-alive measurement graph's back-stack entry,
 * the same way earlier steps read sibling-graph state; if that graph is unexpectedly gone (rather
 * than assuming it can't happen), the screen shows its "missing measurement session" state instead
 * of crashing.
 */
fun NavGraphBuilder.sensorAnalysisScreen(
    navController: NavHostController,
    onContinueToCombinedAnalysis: () -> Unit,
) {
    composable<SensorAnalysisRoute> { entry ->
        val viewModel: SensorAnalysisViewModel = remember { ViewModelProvider(entry)[SensorAnalysisViewModel::class.java] }
        val uiState by viewModel.uiState.collectAsState()
        val measurementSession = remember {
            runCatching {
                val measurementEntry = navController.getBackStackEntry(MeasurementGraphRoute)
                val measurementViewModel = ViewModelProvider(measurementEntry)[MeasurementSessionViewModel::class.java]
                measurementViewModel.uiState.value.session
            }.getOrNull()
        }
        SensorAnalysisScreen(
            uiState = uiState,
            onStart = { viewModel.start(measurementSession) },
            onRetry = viewModel::retry,
            onBack = { navController.navigateUp() },
            onContinue = onContinueToCombinedAnalysis,
        )
    }
}
