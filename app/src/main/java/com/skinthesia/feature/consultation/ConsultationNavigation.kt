package com.skinthesia.feature.consultation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

/** Registers Consultation and its expert-profile detail on the outer graph. Purely static content — no sibling state to read. */
fun NavGraphBuilder.consultationScreen(navController: NavHostController) {
    composable<ConsultationRoute> {
        ConsultationScreen(
            experts = SimulatedExperts,
            onViewProfile = { expert -> navController.navigate(ExpertProfileRoute(expert.id)) },
            onBack = { navController.navigateUp() },
        )
    }
    composable<ExpertProfileRoute> { entry ->
        val route = entry.toRoute<ExpertProfileRoute>()
        val expert = SimulatedExperts.first { it.id == route.expertId }
        ExpertProfileScreen(expert = expert, onBack = { navController.navigateUp() })
    }
}
