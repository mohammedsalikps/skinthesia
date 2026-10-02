package com.skinthesia.feature.marketplace

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.skinthesia.data.SkinthesiaRepository

/**
 * Registers Marketplace on the outer graph. Reads and mutates the same centralized
 * [SkinthesiaRepository] PersonalizedPlan that Product Recommendations already uses, so adding a
 * product here shows up there too — never a second copy of the plan's state.
 */
fun NavGraphBuilder.marketplaceScreen(navController: NavHostController) {
    composable<MarketplaceRoute> {
        val appState by SkinthesiaRepository.state.collectAsState()
        MarketplaceScreen(
            recommendations = appState.personalizedPlan?.recommendations.orEmpty(),
            onToggleRecommendation = { category -> SkinthesiaRepository.toggleRecommendation(category) },
            onBack = { navController.navigateUp() },
        )
    }
}
