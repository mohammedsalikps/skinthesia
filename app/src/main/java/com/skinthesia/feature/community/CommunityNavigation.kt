package com.skinthesia.feature.community

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

/** Registers Community on the outer graph. Purely static, simulated content — no sibling state to read. */
fun NavGraphBuilder.communityScreen(navController: NavHostController) {
    composable<CommunityRoute> {
        CommunityScreen(discussions = CommunityDiscussions, onBack = { navController.navigateUp() })
    }
}
