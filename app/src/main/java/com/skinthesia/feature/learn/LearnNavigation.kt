package com.skinthesia.feature.learn

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.feature.community.CommunityRoute
import com.skinthesia.feature.consultation.ConsultationRoute
import com.skinthesia.feature.marketplace.MarketplaceRoute

/**
 * Registers the Learn tab's destination inside the main shell's inner NavHost. Reads the
 * centralized [SkinthesiaRepository] directly for its one piece of personalization input
 * (PersonalizedPlan's focus areas); [navController] is the OUTER controller, used to jump out to
 * Marketplace, Consultation, Community and article detail screens registered on the outer graph.
 */
fun NavGraphBuilder.learnScreen(navController: NavHostController) {
    composable<LearnRoute> {
        val appState by SkinthesiaRepository.state.collectAsState()
        val recommended = remember(appState.personalizedPlan) { recommendedArticles(appState.personalizedPlan?.priorityFocusAreas.orEmpty()) }
        LearnScreen(
            recommended = recommended,
            onOpenArticle = { article -> navController.navigate(EducationArticleRoute(article.id)) },
            onOpenMarketplace = { navController.navigate(MarketplaceRoute) },
            onOpenConsultation = { navController.navigate(ConsultationRoute) },
            onOpenCommunity = { navController.navigate(CommunityRoute) },
        )
    }
}

/** Registers the reusable article detail destination on the outer graph. */
fun NavGraphBuilder.educationArticleScreen(navController: NavHostController) {
    composable<EducationArticleRoute> { entry ->
        val route = entry.toRoute<EducationArticleRoute>()
        val article = EducationCatalog.first { it.id == route.articleId }
        EducationArticleScreen(article = article, onBack = { navController.navigateUp() })
    }
}
