package com.skinthesia.feature.measurement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaProgressBar
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.FaceRegion
import com.skinthesia.domain.model.MeasurementStatus
import com.skinthesia.feature.onboarding.OnboardingScaffold
import com.skinthesia.hardware.measurement.MeasurementFailureReason
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

/*
 * Forehead, Left Cheek and Right Cheek measurement are the same screen parameterised by
 * [FaceRegion]; each gets its own route only so navigation can address it individually.
 */
@Serializable
data object ForeheadMeasurementRoute

@Serializable
data object LeftCheekMeasurementRoute

@Serializable
data object RightCheekMeasurementRoute

private val DiagramSize = 220.dp
private const val AutoAdvanceDelayMillis = 800L

/**
 * Guides the person through measuring one face region: instruction, a live face diagram, a
 * progress bar, and (on failure) a retry action. Measuring starts automatically on arrival and
 * advances automatically a moment after a successful capture.
 */
@Composable
fun RegionMeasurementScreen(
    region: FaceRegion,
    status: MeasurementStatus,
    progress: Float,
    failureReason: MeasurementFailureReason?,
    completedRegions: Set<FaceRegion>,
    onStartMeasuring: () -> Unit,
    onCaptured: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    // Read once, at first composition: if the person navigates back to an already-finished
    // region, this stops the screen from immediately auto-advancing forward again.
    val enteredAlreadyCaptured = remember { status == MeasurementStatus.CAPTURED }

    LaunchedEffect(status) {
        if (status == MeasurementStatus.PENDING) onStartMeasuring()
        if (status == MeasurementStatus.CAPTURED && !enteredAlreadyCaptured) {
            delay(AutoAdvanceDelayMillis)
            onCaptured()
        }
    }

    val regionIndex = FaceRegion.entries.indexOf(region) + 1

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            if (status == MeasurementStatus.FAILED) {
                SkinthesiaPrimaryButton(
                    text = stringResource(retryLabelRes(failureReason)),
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) {
        Text(
            text = stringResource(R.string.measurement_region_progress_format, regionIndex, FaceRegion.entries.size),
            style = typography.overline,
            color = colors.textTertiary,
        )
        Spacer(Modifier.height(spacing.sm))
        Text(
            text = region.instruction(),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        StatusLine(status = status, failureReason = failureReason)
        Spacer(Modifier.height(spacing.xl))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FaceDiagram(
                activeRegion = if (status == MeasurementStatus.FAILED) null else region,
                completedRegions = completedRegions,
                modifier = Modifier.size(DiagramSize),
            )
        }
        Spacer(Modifier.height(spacing.xl))

        SkinthesiaProgressBar(progress = if (status == MeasurementStatus.CAPTURED) 1f else progress)
    }
}

@Composable
private fun StatusLine(status: MeasurementStatus, failureReason: MeasurementFailureReason?) {
    val colors = SkinthesiaTheme.colors
    val (text, color) = when (status) {
        MeasurementStatus.PENDING -> stringResource(R.string.measurement_status_getting_ready) to colors.textSecondary
        MeasurementStatus.IN_PROGRESS -> stringResource(R.string.measurement_status_measuring) to colors.textSecondary
        MeasurementStatus.CAPTURED -> stringResource(R.string.measurement_status_captured) to colors.onPositiveContainer
        MeasurementStatus.FAILED -> failureBody(failureReason) to colors.error
    }
    Text(text = text, style = SkinthesiaTheme.typography.body, color = color, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun failureBody(reason: MeasurementFailureReason?): String = stringResource(
    if (reason == MeasurementFailureReason.PROBE_DISCONNECTED) {
        R.string.measurement_status_failed_disconnected_body
    } else {
        R.string.measurement_status_failed_generic_body
    },
)

private fun retryLabelRes(reason: MeasurementFailureReason?): Int =
    if (reason == MeasurementFailureReason.PROBE_DISCONNECTED) R.string.measurement_retry_reconnect_cta else R.string.measurement_retry_cta

@Composable
private fun FaceRegion.instruction(): String = stringResource(
    when (this) {
        FaceRegion.FOREHEAD -> R.string.measurement_instruction_forehead
        FaceRegion.LEFT_CHEEK -> R.string.measurement_instruction_left_cheek
        FaceRegion.RIGHT_CHEEK -> R.string.measurement_instruction_right_cheek
    },
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun RegionMeasurementScreenMeasuringPreview() {
    SkinthesiaTheme {
        RegionMeasurementScreen(
            region = FaceRegion.FOREHEAD,
            status = MeasurementStatus.IN_PROGRESS,
            progress = 0.4f,
            failureReason = null,
            completedRegions = emptySet(),
            onStartMeasuring = {},
            onCaptured = {},
            onRetry = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun RegionMeasurementScreenFailedPreview() {
    SkinthesiaTheme {
        RegionMeasurementScreen(
            region = FaceRegion.LEFT_CHEEK,
            status = MeasurementStatus.FAILED,
            progress = 0.5f,
            failureReason = MeasurementFailureReason.PROBE_DISCONNECTED,
            completedRegions = setOf(FaceRegion.FOREHEAD),
            onStartMeasuring = {},
            onCaptured = {},
            onRetry = {},
            onBack = {},
        )
    }
}
