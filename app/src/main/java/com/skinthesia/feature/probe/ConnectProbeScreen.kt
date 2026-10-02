package com.skinthesia.feature.probe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaCardStyle
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.core.designsystem.theme.skinthesiaShadow
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object ConnectProbeRoute

private val PhotoHeight = 220.dp

/**
 * The entry point to the measurement phase: introduces the probe, shows it isn't connected yet,
 * and offers a few tips before the person starts the (simulated) search.
 */
@Composable
fun ConnectProbeScreen(
    onBack: () -> Unit,
    onSearchForProbe: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            SkinthesiaPrimaryButton(
                text = stringResource(R.string.probe_search_cta),
                onClick = onSearchForProbe,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text(
            text = stringResource(R.string.probe_connect_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = stringResource(R.string.probe_connect_explain),
            style = typography.body,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.xl))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.skinthesia_probe_photo),
                contentDescription = stringResource(R.string.probe_photo_content_description),
                modifier = Modifier
                    .height(PhotoHeight)
                    .skinthesiaShadow(elevation = SkinthesiaTheme.elevation.low, shape = SkinthesiaTheme.shapes.large, color = colors.shadow)
                    .clip(SkinthesiaTheme.shapes.large),
            )
        }
        Spacer(Modifier.height(spacing.xl))

        SkinthesiaCard(style = SkinthesiaCardStyle.Outlined) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = SkinthesiaIcons.Signal,
                    contentDescription = null,
                    tint = colors.textTertiary,
                    modifier = Modifier.size(spacing.iconLarge),
                )
                Spacer(Modifier.width(spacing.sm))
                Column {
                    Text(text = stringResource(R.string.probe_status_label), style = typography.labelSmall, color = colors.textTertiary)
                    Text(text = stringResource(R.string.probe_status_not_connected), style = typography.label, color = colors.textPrimary)
                }
            }
            Spacer(Modifier.height(spacing.xs))
            Text(text = stringResource(R.string.probe_simulated_note), style = typography.caption, color = colors.textTertiary)
        }
        Spacer(Modifier.height(spacing.lg))

        Text(text = stringResource(R.string.probe_before_you_begin), style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            TipRow(text = stringResource(R.string.probe_tip_charged))
            TipRow(text = stringResource(R.string.probe_tip_bluetooth))
            TipRow(text = stringResource(R.string.probe_tip_nearby))
        }
    }
}

@Composable
private fun TipRow(text: String) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.xs)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(colors.primary),
        )
        Spacer(Modifier.width(spacing.sm))
        Text(text = text, style = SkinthesiaTheme.typography.body, color = colors.textSecondary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1200)
@Composable
private fun ConnectProbeScreenPreview() {
    SkinthesiaTheme {
        ConnectProbeScreen(onBack = {}, onSearchForProbe = {})
    }
}
