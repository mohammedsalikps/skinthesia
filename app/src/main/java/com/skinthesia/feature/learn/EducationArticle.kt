package com.skinthesia.feature.learn

import com.skinthesia.R
import com.skinthesia.domain.model.VisualIndicator

/** Educational categories Learn organizes content into. */
enum class EducationTopic { SKIN_BASICS, UNDERSTANDING_YOUR_SKIN, INGREDIENTS, DAILY_HABITS, ROUTINES, SKINTHESIA_TECHNOLOGY }

/**
 * A small, presentation-only educational article. Content is short, original placeholder copy —
 * never a real external article, and never a claim of AI-generated or clinical content.
 */
data class EducationArticle(
    val id: String,
    val category: EducationTopic,
    val titleRes: Int,
    val summaryRes: Int,
    val bodyRes: Int,
    /** Focus areas this article is relevant to, for deterministic "Recommended for You" matching. */
    val relatedFocusAreas: Set<VisualIndicator> = emptySet(),
    val isSimulated: Boolean = true,
)

/** The id of the single featured article shown at the top of Learn. */
const val FeaturedArticleId = "skinprint-technology"

/** One fixed, hand-written article per topic. */
val EducationCatalog: List<EducationArticle> = listOf(
    EducationArticle(
        id = FeaturedArticleId,
        category = EducationTopic.SKINTHESIA_TECHNOLOGY,
        titleRes = R.string.article_skinprint_title,
        summaryRes = R.string.article_skinprint_summary,
        bodyRes = R.string.article_skinprint_body,
    ),
    EducationArticle(
        id = "skin-basics",
        category = EducationTopic.SKIN_BASICS,
        titleRes = R.string.article_skin_basics_title,
        summaryRes = R.string.article_skin_basics_summary,
        bodyRes = R.string.article_skin_basics_body,
    ),
    EducationArticle(
        id = "understanding-your-skin",
        category = EducationTopic.UNDERSTANDING_YOUR_SKIN,
        titleRes = R.string.article_understanding_skin_title,
        summaryRes = R.string.article_understanding_skin_summary,
        bodyRes = R.string.article_understanding_skin_body,
    ),
    EducationArticle(
        id = "ingredients",
        category = EducationTopic.INGREDIENTS,
        titleRes = R.string.article_ingredients_title,
        summaryRes = R.string.article_ingredients_summary,
        bodyRes = R.string.article_ingredients_body,
        relatedFocusAreas = setOf(VisualIndicator.TEXTURE, VisualIndicator.PORE_APPEARANCE),
    ),
    EducationArticle(
        id = "daily-habits",
        category = EducationTopic.DAILY_HABITS,
        titleRes = R.string.article_daily_habits_title,
        summaryRes = R.string.article_daily_habits_summary,
        bodyRes = R.string.article_daily_habits_body,
        relatedFocusAreas = setOf(VisualIndicator.REDNESS, VisualIndicator.DARK_CIRCLES),
    ),
    EducationArticle(
        id = "routines",
        category = EducationTopic.ROUTINES,
        titleRes = R.string.article_routines_title,
        summaryRes = R.string.article_routines_summary,
        bodyRes = R.string.article_routines_body,
        relatedFocusAreas = setOf(
            VisualIndicator.UNEVEN_TONE,
            VisualIndicator.PIGMENTATION,
            VisualIndicator.ACNE_APPEARANCE,
            VisualIndicator.FINE_LINES,
        ),
    ),
)

/** The one representative article for a topic's "Explore Topics" card. */
fun EducationTopic.article(): EducationArticle = EducationCatalog.first { it.category == this }

/**
 * Deterministically prioritizes articles related to the person's existing focus areas, then fills
 * out the remainder with the rest of the catalog — never a second analysis, just a simple filter
 * over data the plan already produced.
 */
fun recommendedArticles(focusAreas: List<VisualIndicator>, limit: Int = 3): List<EducationArticle> {
    val matched = EducationCatalog.filter { article -> article.relatedFocusAreas.any { it in focusAreas } }
    val fallback = EducationCatalog.filterNot { it in matched }
    return (matched + fallback).take(limit)
}
