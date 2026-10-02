package com.skinthesia.feature.probe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object ProbeGraphRoute

/**
 * Connect Skinthesia Probe through Calibration / Ready, sharing one [ProbeConnectionViewModel] so
 * the same (simulated) connection persists across the three screens.
 */
fun NavGraphBuilder.probeConnectionGraph(
    navController: NavHostController,
    onMeasurementReady: () -> Unit,
) {
    navigation<ProbeGraphRoute>(startDestination = ConnectProbeRoute) {
        composable<ConnectProbeRoute> { entry ->
            val viewModel = entry.probeConnectionViewModel(navController)
            ConnectProbeScreen(
                onBack = { navController.navigateUp() },
                onSearchForProbe = {
                    viewModel.search()
                    navController.navigate(DevicePairingRoute)
                },
            )
        }
        composable<DevicePairingRoute> { entry ->
            val viewModel = entry.probeConnectionViewModel(navController)
            val state by viewModel.state.collectAsState()
            DevicePairingScreen(
                probeState = state,
                onConnect = viewModel::connect,
                onRetry = viewModel::retry,
                onBack = { navController.navigateUp() },
                onContinue = { navController.navigate(CalibrationRoute) },
            )
        }
        composable<CalibrationRoute> { entry ->
            val viewModel = entry.probeConnectionViewModel(navController)
            val state by viewModel.state.collectAsState()
            CalibrationScreen(
                phase = state.phase,
                onStartCalibration = viewModel::calibrate,
                onBack = { navController.navigateUp() },
                onBeginMeasurement = onMeasurementReady,
            )
        }
    }
}

/** Resolves the [ProbeConnectionViewModel] shared by every destination in [ProbeGraphRoute]. */
@Composable
private fun NavBackStackEntry.probeConnectionViewModel(navController: NavHostController): ProbeConnectionViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(ProbeGraphRoute) }
    return remember(parentEntry) { ViewModelProvider(parentEntry)[ProbeConnectionViewModel::class.java] }
}
