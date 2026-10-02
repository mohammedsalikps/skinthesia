package com.skinthesia.feature.sensor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaLoadingIndicator
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaProgressBar
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.FaceRegion
import com.skinthesia.domain.model.RegionSensorReadings
import com.skinthesia.domain.model.SensorAnalysisSession
import com.skinthesia.domain.model.SensorAnalysisStatus
import com.skinthesia.domain.model.SensorChannelReading
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data object SensorAnalysisRoute

/**
 * Reads (simulated, for now) sensor channel data for the measurement session captured in Step 6
 * and presents it per region. Reading starts automatically on arrival.
 */
@Composable
fun SensorAnalysisScreen(
    uiState: SensorAnalysisUiState,
    onStart: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) { onStart() }

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            when (uiState) {
                is SensorAnalysisUiState.Complete -> SkinthesiaPrimaryButton(
                    text = stringResource(R.string.action_continue),
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                )
                SensorAnalysisUiState.Failed -> SkinthesiaPrimaryButton(
                    text = stringResource(R.string.sensor_retry_cta),
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
                SensorAnalysisUiState.MissingSession, SensorAnalysisUiState.IncompleteRegions ->
                    SkinthesiaSecondaryButton(
                        text = stringResource(R.string.action_back),
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                    )
                is SensorAnalysisUiState.Analyzing -> Unit
            }
        },
    ) {
        when (uiState) {
            SensorAnalysisUiState.MissingSession -> ErrorContent(
                title = stringResource(R.string.sensor_error_missing_session_title),
                body = stringResource(R.string.sensor_error_missing_session_body),
            )
            SensorAnalysisUiState.IncompleteRegions -> ErrorContent(
                title = stringResource(R.string.sensor_error_incomplete_title),
                body = stringResource(R.string.sensor_error_incomplete_body),
            )
            is SensorAnalysisUiState.Analyzing -> AnalyzingContent(progress = uiState.progress)
            is SensorAnalysisUiState.Complete -> CompleteContent(result = uiState.result)
            SensorAnalysisUiState.Failed -> ErrorContent(
                title = stringResource(R.string.sensor_error_failed_title),
                body = stringResource(R.string.sensor_error_failed_body),
            )
        }
    }
}

@Composable
private fun AnalyzingContent(progress: Float) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    Text(
        text = stringResource(R.string.sensor_title),
        style = typography.headline,
        color = colors.textPrimary,
        modifier = Modifier.semantics { heading() },
    )
    Spacer(Modifier.height(spacing.xl))
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        SkinthesiaLoadingIndicator(size = spacing.xl)
        Spacer(Modifier.height(spacing.md))
        Text(text = stringResource(R.string.sensor_status_reading), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.md))
        SkinthesiaProgressBar(progress = progress)
    }
}

@Composable
private fun CompleteContent(result: SensorAnalysisSession) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val allChannels = result.regions.values.flatMap { it.channels }
    val allSimulated = allChannels.isNotEmpty() && allChannels.all { it.isSimulated }

    Text(
        text = stringResource(R.string.sensor_title),
        style = typography.headline,
        color = colors.textPrimary,
        modifier = Modifier.semantics { heading() },
    )
    Spacer(Modifier.height(spacing.xs))
    Text(text = stringResource(R.string.sensor_simulated_label), style = typography.overline, color = colors.textTertiary)
    Spacer(Modifier.height(spacing.xl))

    SkinthesiaCard {
        FaceRegion.entries.forEachIndexed { index, region ->
            RegionChecklistRow(label = stringResource(region.labelRes()))
            if (index < FaceRegion.entries.lastIndex) Spacer(Modifier.height(spacing.sm))
        }
    }
    Spacer(Modifier.height(spacing.lg))

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        FaceRegion.entries.forEach { region ->
            RegionSensorCard(label = stringResource(region.labelRes()), readings = result.regions[region])
        }
    }
    Spacer(Modifier.height(spacing.lg))

    Text(
        text = stringResource(if (allSimulated) R.string.sensor_explanation_simulated else R.string.sensor_explanation_real),
        style = typography.bodySmall,
        color = colors.textSecondary,
    )
}

@Composable
private fun RegionChecklistRow(label: String) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.xl)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(colors.positiveContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = SkinthesiaIcons.Check,
                contentDescription = null,
                tint = colors.onPositiveContainer,
                modifier = Modifier.size(spacing.iconSmall),
            )
        }
        Spacer(Modifier.width(spacing.sm))
        Text(text = label, style = SkinthesiaTheme.typography.body, color = colors.textPrimary)
    }
}

@Composable
private fun RegionSensorCard(label: String, readings: RegionSensorReadings?) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    SkinthesiaCard {
        Text(text = label, style = typography.sectionHeading, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.sm))
        val channels = readings?.channels.orEmpty()
        if (channels.isEmpty()) {
            Text(text = stringResource(R.string.sensor_no_data_for_region), style = typography.bodySmall, color = colors.textTertiary)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                channels.forEach { channel -> ChannelRow(channel) }
            }
        }
    }
}

@Composable
private fun ChannelRow(channel: SensorChannelReading) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = channel.channelName, style = typography.body, color = colors.textPrimary)
            Text(text = "${channel.value.roundToInt()} ${channel.unit}", style = typography.numberSmall, color = colors.textSecondary)
        }
        Spacer(Modifier.height(spacing.xxs))
        SkinthesiaProgressBar(progress = (channel.value / 100f).coerceIn(0f, 1f))
    }
}

@Composable
private fun ErrorContent(title: String, body: String) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    Text(text = title, style = typography.headline, color = colors.textPrimary, modifier = Modifier.semantics { heading() })
    Spacer(Modifier.height(spacing.sm))
    Text(text = body, style = typography.body, color = colors.textSecondary)
}

private fun FaceRegion.labelRes(): Int = when (this) {
    FaceRegion.FOREHEAD -> R.string.measurement_region_forehead
    FaceRegion.LEFT_CHEEK -> R.string.measurement_region_left_cheek
    FaceRegion.RIGHT_CHEEK -> R.string.measurement_region_right_cheek
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1800)
@Composable
private fun SensorAnalysisScreenCompletePreview() {
    SkinthesiaTheme {
        SensorAnalysisScreen(
            uiState = SensorAnalysisUiState.Complete(
                SensorAnalysisSession(
                    id = "preview",
                    measurementSessionId = "preview-session",
                    status = SensorAnalysisStatus.COMPLETE,
                    startedAtMillis = 0L,
                    regions = FaceRegion.entries.associateWith { region ->
                        RegionSensorReadings(
                            region = region,
                            channels = listOf(
                                SensorChannelReading("Channel A", 62f, "a.u.", 0L, isSimulated = true),
                                SensorChannelReading("Channel B", 41f, "a.u.", 0L, isSimulated = true),
                                SensorChannelReading("Channel C", 78f, "a.u.", 0L, isSimulated = true),
                            ),
                        )
                    },
                ),
            ),
            onStart = {},
            onRetry = {},
            onBack = {},
            onContinue = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun SensorAnalysisScreenMissingSessionPreview() {
    SkinthesiaTheme {
        SensorAnalysisScreen(
            uiState = SensorAnalysisUiState.MissingSession,
            onStart = {},
            onRetry = {},
            onBack = {},
            onContinue = {},
        )
    }
}
