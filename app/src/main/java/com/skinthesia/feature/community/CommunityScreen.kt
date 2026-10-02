package com.skinthesia.feature.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object CommunityRoute

/** A calm, clearly simulated discussion feed — no real posting, following or moderation backend. */
@Composable
fun CommunityScreen(
    discussions: List<CommunityDiscussion>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(onBack = onBack, modifier = modifier, bottomBar = {}) {
        Text(
            text = stringResource(R.string.community_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.community_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.sm))
        Text(text = stringResource(R.string.community_simulated_label), style = typography.caption, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.xl))

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            discussions.forEach { discussion -> DiscussionCard(discussion) }
        }
    }
}

@Composable
private fun DiscussionCard(discussion: CommunityDiscussion) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    SkinthesiaCard {
        Text(text = stringResource(discussion.titleRes), style = typography.title, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(R.string.community_participant_placeholder), style = typography.caption, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.sm))
        Text(text = stringResource(discussion.excerptRes), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.sm))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.community_comment_count_format, discussion.commentCount),
                style = typography.caption,
                color = colors.textTertiary,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1400)
@Composable
private fun CommunityScreenPreview() {
    SkinthesiaTheme {
        CommunityScreen(discussions = CommunityDiscussions, onBack = {})
    }
}
