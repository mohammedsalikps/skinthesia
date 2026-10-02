package com.skinthesia.feature.journey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data object SkinJourneyRoute

/** One real, already-happened event in the person's journey — never a future or fabricated one. */
enum class JourneyMilestoneKind { BASELINE_ESTABLISHED, SKINPRINT_UPDATED, PLAN_CREATED, CHECK_IN_COMPLETED }

/** A presentation-only pairing of a real milestone with the real timestamp it happened at. */
data class JourneyMilestone(val kind: JourneyMilestoneKind, val timestampMillis: Long)

/**
 * A calm chronological view of what has actually happened so far — never a fabricated history.
 * [milestones] must already be backed by real application state; this screen only renders and
 * orders them, and always ends with an honest "just beginning" note rather than invented history.
 */
@Composable
fun SkinJourneyScreen(
    milestones: List<JourneyMilestone>,
    onViewProgress: () -> Unit,
    onCheckIn: () -> Unit,
    onViewPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val ordered = milestones.sortedBy { it.timestampMillis }

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
                    text = stringResource(R.string.journey_title),
                    style = typography.headline,
                    color = colors.textPrimary,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(spacing.xs))
                Text(text = stringResource(R.string.journey_subtitle), style = typography.body, color = colors.textSecondary)
                Spacer(Modifier.height(spacing.xl))

                if (ordered.isEmpty()) {
                    Text(text = stringResource(R.string.journey_empty_state), style = typography.bodySmall, color = colors.textSecondary)
                } else {
                    Column {
                        ordered.forEachIndexed { index, milestone ->
                            TimelineEntry(milestone = milestone, isLast = index == ordered.lastIndex)
                        }
                    }
                }
                Spacer(Modifier.height(spacing.xl))

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.journey_future_title),
                        style = typography.title,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(spacing.xs))
                    Text(
                        text = stringResource(R.string.journey_future_body),
                        style = typography.bodySmall,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(spacing.xl))

                SkinthesiaPrimaryButton(text = stringResource(R.string.journey_action_checkin), onClick = onCheckIn, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(spacing.sm))
                SkinthesiaSecondaryButton(text = stringResource(R.string.journey_action_view_progress), onClick = onViewProgress, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(spacing.sm))
                SkinthesiaSecondaryButton(text = stringResource(R.string.journey_action_view_plan), onClick = onViewPlan, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(spacing.xl))
            }
        }
    }
}

private val DotSize = 28.dp
private val ConnectorHeight = 32.dp

@Composable
private fun TimelineEntry(milestone: JourneyMilestone, isLast: Boolean) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(DotSize)
                    .clip(SkinthesiaTheme.shapes.pill)
                    .background(colors.positiveContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(SkinthesiaIcons.Check, contentDescription = null, tint = colors.onPositiveContainer, modifier = Modifier.size(spacing.iconSmall))
            }
            if (!isLast) {
                Box(modifier = Modifier.width(spacing.hairline).height(ConnectorHeight).background(colors.outline))
            }
        }
        Spacer(Modifier.width(spacing.sm))
        SkinthesiaCard(modifier = Modifier.weight(1f).padding(bottom = if (isLast) spacing.none else spacing.sm)) {
            Text(text = stringResource(milestone.kind.titleRes()), style = typography.label, color = colors.textPrimary)
            Spacer(Modifier.height(spacing.xxs))
            Text(text = formatMilestoneDate(milestone.timestampMillis), style = typography.caption, color = colors.textTertiary)
        }
    }
}

private fun JourneyMilestoneKind.titleRes(): Int = when (this) {
    JourneyMilestoneKind.BASELINE_ESTABLISHED -> R.string.journey_milestone_baseline
    JourneyMilestoneKind.SKINPRINT_UPDATED -> R.string.journey_milestone_skinprint_updated
    JourneyMilestoneKind.PLAN_CREATED -> R.string.journey_milestone_plan_created
    JourneyMilestoneKind.CHECK_IN_COMPLETED -> R.string.journey_milestone_checkin
}

private fun formatMilestoneDate(millis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1400)
@Composable
private fun SkinJourneyScreenPreview() {
    SkinthesiaTheme {
        SkinJourneyScreen(
            milestones = listOf(
                JourneyMilestone(JourneyMilestoneKind.BASELINE_ESTABLISHED, System.currentTimeMillis() - 60_000),
                JourneyMilestone(JourneyMilestoneKind.PLAN_CREATED, System.currentTimeMillis() - 30_000),
                JourneyMilestone(JourneyMilestoneKind.CHECK_IN_COMPLETED, System.currentTimeMillis()),
            ),
            onViewProgress = {},
            onCheckIn = {},
            onViewPlan = {},
        )
    }
}
