package com.skinthesia.feature.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinReport
import com.skinthesia.domain.model.buildSkinReport
import com.skinthesia.feature.analysis.CameraAnalysisRoute
import com.skinthesia.feature.analysis.CameraAnalysisViewModel
import com.skinthesia.feature.combined.CombinedAnalysisGraphRoute
import com.skinthesia.feature.combined.CombinedAnalysisUiState
import com.skinthesia.feature.combined.CombinedAnalysisViewModel

/**
 * Registers Skin Report and Areas for Improvement. Neither needs a ViewModel of its own: the
 * report is a pure, deterministic derivation from the SkinPrint (Step 9) and camera analysis
 * (Step 7), both still alive as sibling destinations, so each screen simply recomputes it.
 */
fun NavGraphBuilder.skinReportScreen(
    navController: NavHostController,
    onExploreFocusAreas: () -> Unit,
) {
    composable<SkinReportRoute> { entry ->
        val loaded = remember { entry.loadSkinReport(navController) }
        if (loaded != null) {
            val (skinPrint, report) = loaded
            SkinReportScreen(
                skinPrint = skinPrint,
                report = report,
                onBack = { navController.navigateUp() },
                onExploreFocusAreas = onExploreFocusAreas,
            )
        } else {
            // Reached without a SkinPrint (e.g. deep navigation out of order): step back rather than show a blank screen.
            LaunchedEffect(Unit) { navController.navigateUp() }
        }
    }
}

fun NavGraphBuilder.areasForImprovementScreen(
    navController: NavHostController,
    onContinueToPlan: () -> Unit,
) {
    composable<AreasForImprovementRoute> { entry ->
        val loaded = remember { entry.loadSkinReport(navController) }
        if (loaded != null) {
            AreasForImprovementScreen(
                focusAreas = loaded.second.focusAreas,
                onBack = { navController.navigateUp() },
                onContinue = onContinueToPlan,
            )
        } else {
            LaunchedEffect(Unit) { navController.navigateUp() }
        }
    }
}

/** Reads the SkinPrint and camera analysis from their still-alive sibling destinations and derives the report. */
private fun NavBackStackEntry.loadSkinReport(navController: NavHostController): Pair<SkinPrint, SkinReport>? {
    val skinPrint = runCatching {
        val combinedEntry = navController.getBackStackEntry(CombinedAnalysisGraphRoute)
        val combinedState = ViewModelProvider(combinedEntry)[CombinedAnalysisViewModel::class.java].uiState.value
        (combinedState as? CombinedAnalysisUiState.Complete)?.skinPrint
    }.getOrNull() ?: return null

    val cameraAnalysis = runCatching {
        val cameraEntry = navController.getBackStackEntry(CameraAnalysisRoute)
        ViewModelProvider(cameraEntry)[CameraAnalysisViewModel::class.java].uiState.value.analysis
    }.getOrNull()

    return skinPrint to buildSkinReport(skinPrint, cameraAnalysis)
}
