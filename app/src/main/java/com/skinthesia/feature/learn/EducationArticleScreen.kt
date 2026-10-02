package com.skinthesia.feature.learn

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class EducationArticleRoute(val articleId: String)

/** A reusable detail screen for any [EducationArticle] opened from Learn. */
@Composable
fun EducationArticleScreen(
    article: EducationArticle,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(onBack = onBack, modifier = modifier, bottomBar = {}) {
        Text(
            text = stringResource(article.category.labelRes()).uppercase(Locale.getDefault()),
            style = typography.overline,
            color = colors.textTertiary,
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = stringResource(article.titleRes),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.sm))
        if (article.isSimulated) {
            Text(text = stringResource(R.string.learn_simulated_label), style = typography.caption, color = colors.textTertiary)
            Spacer(Modifier.height(spacing.md))
        }
        Text(text = stringResource(article.summaryRes), style = typography.bodyLarge, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.lg))
        Text(text = stringResource(article.bodyRes), style = typography.body, color = colors.textPrimary, modifier = Modifier.fillMaxWidth())
    }
}

fun EducationTopic.labelRes(): Int = when (this) {
    EducationTopic.SKIN_BASICS -> R.string.topic_skin_basics
    EducationTopic.UNDERSTANDING_YOUR_SKIN -> R.string.topic_understanding_your_skin
    EducationTopic.INGREDIENTS -> R.string.topic_ingredients
    EducationTopic.DAILY_HABITS -> R.string.topic_daily_habits
    EducationTopic.ROUTINES -> R.string.topic_routines
    EducationTopic.SKINTHESIA_TECHNOLOGY -> R.string.topic_skinthesia_technology
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 900)
@Composable
private fun EducationArticleScreenPreview() {
    SkinthesiaTheme {
        EducationArticleScreen(article = EducationCatalog.first(), onBack = {})
    }
}
