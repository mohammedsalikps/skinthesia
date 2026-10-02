package com.skinthesia.feature.learn

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaCardStyle
import com.skinthesia.core.designsystem.component.SkinthesiaSectionHeader
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import kotlinx.serialization.Serializable

@Serializable
data object LearnRoute

/**
 * Learn's editorial home: one featured article, a grid of topic cards, a short deterministically
 * personalized reading list, and quiet entry points into Marketplace, Consultation and Community.
 */
@Composable
fun LearnScreen(
    recommended: List<EducationArticle>,
    onOpenArticle: (EducationArticle) -> Unit,
    onOpenMarketplace: () -> Unit,
    onOpenConsultation: () -> Unit,
    onOpenCommunity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val featured = EducationCatalog.first { it.id == FeaturedArticleId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = spacing.contentMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenHorizontal),
            ) {
                Spacer(Modifier.height(spacing.md))
                Text(
                    text = stringResource(R.string.learn_title),
                    style = typography.headline,
                    color = colors.textPrimary,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(spacing.xs))
                Text(text = stringResource(R.string.learn_subtitle), style = typography.body, color = colors.textSecondary)
                Spacer(Modifier.height(spacing.xl))

                // Featured
                SkinthesiaSectionHeader(title = stringResource(R.string.learn_section_featured))
                Spacer(Modifier.height(spacing.sm))
                SkinthesiaCard(onClick = { onOpenArticle(featured) }) {
                    Text(text = stringResource(featured.titleRes), style = typography.title, color = colors.textPrimary)
                    Spacer(Modifier.height(spacing.xs))
                    Text(text = stringResource(featured.summaryRes), style = typography.body, color = colors.textSecondary)
                    Spacer(Modifier.height(spacing.sm))
                    Text(text = stringResource(R.string.learn_simulated_label), style = typography.caption, color = colors.textTertiary)
                }
                Spacer(Modifier.height(spacing.lg))

                // Explore Topics
                SkinthesiaSectionHeader(title = stringResource(R.string.learn_section_explore_topics))
                Spacer(Modifier.height(spacing.sm))
                TopicGrid(onOpenArticle = onOpenArticle)
                Spacer(Modifier.height(spacing.lg))

                // Recommended for You
                if (recommended.isNotEmpty()) {
                    SkinthesiaSectionHeader(title = stringResource(R.string.learn_section_recommended))
                    Spacer(Modifier.height(spacing.sm))
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        recommended.forEach { article -> ArticleRow(article = article, onClick = { onOpenArticle(article) }) }
                    }
                    Spacer(Modifier.height(spacing.lg))
                }

                // Discover More
                SkinthesiaSectionHeader(title = stringResource(R.string.learn_section_discover_more))
                Spacer(Modifier.height(spacing.sm))
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    DiscoverCard(
                        title = stringResource(R.string.learn_discover_marketplace_title),
                        body = stringResource(R.string.learn_discover_marketplace_body),
                        onClick = onOpenMarketplace,
                    )
                    DiscoverCard(
                        title = stringResource(R.string.learn_discover_consultation_title),
                        body = stringResource(R.string.learn_discover_consultation_body),
                        onClick = onOpenConsultation,
                    )
                    DiscoverCard(
                        title = stringResource(R.string.learn_discover_community_title),
                        body = stringResource(R.string.learn_discover_community_body),
                        onClick = onOpenCommunity,
                    )
                }
                Spacer(Modifier.height(spacing.xl))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopicGrid(onOpenArticle: (EducationArticle) -> Unit) {
    val spacing = SkinthesiaTheme.spacing
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        EducationTopic.entries.forEach { topic ->
            val article = topic.article()
            SkinthesiaCard(
                style = SkinthesiaCardStyle.Outlined,
                onClick = { onOpenArticle(article) },
                modifier = Modifier.widthIn(min = 150.dp),
            ) {
                Text(text = stringResource(topic.labelRes()), style = SkinthesiaTheme.typography.label, color = SkinthesiaTheme.colors.textPrimary)
            }
        }
    }
}

@Composable
private fun ArticleRow(article: EducationArticle, onClick: () -> Unit) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    SkinthesiaCard(onClick = onClick) {
        Text(text = stringResource(article.category.labelRes()), style = typography.overline, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(article.titleRes), style = typography.label, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(article.summaryRes), style = typography.bodySmall, color = colors.textSecondary)
    }
}

@Composable
private fun DiscoverCard(title: String, body: String, onClick: () -> Unit) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    SkinthesiaCard(style = SkinthesiaCardStyle.Tonal, onClick = onClick) {
        Text(text = title, style = typography.label, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = body, style = typography.bodySmall, color = colors.textSecondary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 2000)
@Composable
private fun LearnScreenPreview() {
    SkinthesiaTheme {
        LearnScreen(
            recommended = recommendedArticles(emptyList()),
            onOpenArticle = {},
            onOpenMarketplace = {},
            onOpenConsultation = {},
            onOpenCommunity = {},
        )
    }
}
