package com.skinthesia.feature.probe

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.skinthesia.core.designsystem.component.SkinthesiaLoadingIndicator
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.core.designsystem.theme.skinthesiaShadow
import com.skinthesia.feature.onboarding.OnboardingScaffold
import com.skinthesia.hardware.probe.ProbeConnectionPhase
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
data object CalibrationRoute

private val ChecklistStepDelaysMillis = longArrayOf(200L, 650L, 650L)
private val PhotoHeight = 140.dp

/**
 * A calm preparation state between connecting and measuring. Calibration starts as soon as this
 * screen is reached, its checklist fills in a step at a time, and "Begin Measurement" appears only
 * once the probe reports [ProbeConnectionPhase.READY].
 */
@Composable
fun CalibrationScreen(
    phase: ProbeConnectionPhase,
    onStartCalibration: () -> Unit,
    onBack: () -> Unit,
    onBeginMeasurement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    LaunchedEffect(Unit) {
        if (phase == ProbeConnectionPhase.CONNECTED) onStartCalibration()
    }

    var visibleSteps by remember { mutableIntStateOf(if (phase == ProbeConnectionPhase.READY) 3 else 0) }
    LaunchedEffect(phase) {
        when (phase) {
            ProbeConnectionPhase.CALIBRATING -> {
                visibleSteps = 0
                for (index in ChecklistStepDelaysMillis.indices) {
                    delay(ChecklistStepDelaysMillis[index])
                    visibleSteps = index + 1
                }
            }
            ProbeConnectionPhase.READY -> visibleSteps = 3
            else -> Unit
        }
    }

    val ready = phase == ProbeConnectionPhase.READY

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            if (ready) {
                SkinthesiaPrimaryButton(
                    text = stringResource(R.string.calibration_begin_measurement),
                    onClick = onBeginMeasurement,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) {
        Text(
            text = stringResource(if (ready) R.string.calibration_ready_title else R.string.calibration_preparing_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.lg))

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

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            ChecklistRow(label = stringResource(R.string.calibration_checklist_device_connected), checked = visibleSteps >= 1)
            ChecklistRow(label = stringResource(R.string.calibration_checklist_sensors_ready), checked = visibleSteps >= 2)
            ChecklistRow(label = stringResource(R.string.calibration_checklist_measurement_ready), checked = visibleSteps >= 3)
        }

        if (!ready) {
            Spacer(Modifier.height(spacing.xl))
            Row(verticalAlignment = Alignment.CenterVertically) {
                SkinthesiaLoadingIndicator(size = spacing.lg)
                Spacer(Modifier.width(spacing.sm))
                Text(text = stringResource(R.string.calibration_calibrating_label), style = typography.body, color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun ChecklistRow(label: String, checked: Boolean) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val containerColor by animateColorAsState(
        targetValue = if (checked) colors.positiveContainer else colors.surfaceMuted,
        animationSpec = tween(300),
        label = "checklistContainer",
    )
    val contentColor = if (checked) colors.onPositiveContainer else colors.textTertiary

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.xl)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(containerColor),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = checked,
                enter = fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.6f),
                exit = fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.6f),
            ) {
                Icon(
                    imageVector = SkinthesiaIcons.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(spacing.iconSmall),
                )
            }
        }
        Spacer(Modifier.width(spacing.sm))
        Text(text = label, style = typography.body, color = if (checked) colors.textPrimary else colors.textTertiary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun CalibrationScreenPreparingPreview() {
    SkinthesiaTheme {
        CalibrationScreen(phase = ProbeConnectionPhase.CALIBRATING, onStartCalibration = {}, onBack = {}, onBeginMeasurement = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun CalibrationScreenReadyPreview() {
    SkinthesiaTheme {
        CalibrationScreen(phase = ProbeConnectionPhase.READY, onStartCalibration = {}, onBack = {}, onBeginMeasurement = {})
    }
}
