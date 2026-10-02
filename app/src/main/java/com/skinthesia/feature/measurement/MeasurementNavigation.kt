package com.skinthesia.feature.measurement

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
import com.skinthesia.domain.model.FaceRegion
import com.skinthesia.feature.probe.ProbeConnectionViewModel
import com.skinthesia.feature.probe.ProbeGraphRoute
import kotlinx.serialization.Serializable

@Serializable
data object MeasurementGraphRoute

/**
 * Forehead through Measurement Complete, sharing one [MeasurementSessionViewModel]. The connected
 * probe's device identifier is read from the still-alive [ProbeGraphRoute] back stack entry, so
 * each capture is genuinely tied to the device that (in simulation) took it.
 */
fun NavGraphBuilder.measurementGraph(
    navController: NavHostController,
    onAnalysisReady: () -> Unit,
) {
    navigation<MeasurementGraphRoute>(startDestination = ForeheadMeasurementRoute) {
        regionMeasurementDestination(FaceRegion.FOREHEAD, navController) { navController.navigate(LeftCheekMeasurementRoute) }
        regionMeasurementDestination(FaceRegion.LEFT_CHEEK, navController) { navController.navigate(RightCheekMeasurementRoute) }
        regionMeasurementDestination(FaceRegion.RIGHT_CHEEK, navController) { navController.navigate(MeasurementCompleteRoute) }

        composable<MeasurementCompleteRoute> {
            MeasurementCompleteScreen(
                onBack = { navController.navigateUp() },
                onContinueToAnalysis = onAnalysisReady,
            )
        }
    }
}

private fun NavGraphBuilder.regionMeasurementDestination(
    region: FaceRegion,
    navController: NavHostController,
    onCaptured: () -> Unit,
) {
    when (region) {
        FaceRegion.FOREHEAD -> composable<ForeheadMeasurementRoute> { entry -> RegionDestination(region, entry, navController, onCaptured) }
        FaceRegion.LEFT_CHEEK -> composable<LeftCheekMeasurementRoute> { entry -> RegionDestination(region, entry, navController, onCaptured) }
        FaceRegion.RIGHT_CHEEK -> composable<RightCheekMeasurementRoute> { entry -> RegionDestination(region, entry, navController, onCaptured) }
    }
}

@Composable
private fun RegionDestination(
    region: FaceRegion,
    entry: NavBackStackEntry,
    navController: NavHostController,
    onCaptured: () -> Unit,
) {
    val viewModel = entry.measurementSessionViewModel(navController)
    val state by viewModel.uiState.collectAsState()
    val deviceId = entry.probeDeviceId(navController)
    RegionMeasurementScreen(
        region = region,
        status = state.statusOf(region),
        progress = state.activeRegionProgress,
        failureReason = state.failureReason,
        completedRegions = state.completedRegions,
        onStartMeasuring = { viewModel.measure(region, deviceId) },
        onRetry = { viewModel.measure(region, deviceId) },
        onCaptured = onCaptured,
        onBack = { navController.navigateUp() },
    )
}

/** Resolves the [MeasurementSessionViewModel] shared by every destination in [MeasurementGraphRoute]. */
@Composable
private fun NavBackStackEntry.measurementSessionViewModel(navController: NavHostController): MeasurementSessionViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(MeasurementGraphRoute) }
    return remember(parentEntry) { ViewModelProvider(parentEntry)[MeasurementSessionViewModel::class.java] }
}

/** Reads the connected probe's simulated device identifier from the (still on the back stack) probe graph. */
@Composable
private fun NavBackStackEntry.probeDeviceId(navController: NavHostController): String? {
    val probeEntry = remember(this) { navController.getBackStackEntry(ProbeGraphRoute) }
    val probeViewModel = remember(probeEntry) { ViewModelProvider(probeEntry)[ProbeConnectionViewModel::class.java] }
    return probeViewModel.state.value.deviceId
}
