package com.skinthesia.feature.probe

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.component.SkinthesiaLoadingIndicator
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.feature.onboarding.OnboardingScaffold
import com.skinthesia.hardware.probe.ProbeBatteryLevel
import com.skinthesia.hardware.probe.ProbeConnectionPhase
import com.skinthesia.hardware.probe.ProbeState
import kotlinx.serialization.Serializable

@Serializable
data object DevicePairingRoute

private val IllustrationSize = 180.dp

/**
 * A realistic (simulated) pairing experience: searching, discovered, connecting, connected, or
 * failed. Search begins the moment this screen is reached; the bottom action changes with the state.
 */
@Composable
fun DevicePairingScreen(
    probeState: ProbeState,
    onConnect: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            when (probeState.phase) {
                ProbeConnectionPhase.DISCOVERED ->
                    SkinthesiaPrimaryButton(text = stringResource(R.string.pairing_connect_cta), onClick = onConnect, modifier = Modifier.fillMaxWidth())
                ProbeConnectionPhase.CONNECTED ->
                    SkinthesiaPrimaryButton(text = stringResource(R.string.action_continue), onClick = onContinue, modifier = Modifier.fillMaxWidth())
                ProbeConnectionPhase.FAILED ->
                    SkinthesiaPrimaryButton(text = stringResource(R.string.pairing_retry_cta), onClick = onRetry, modifier = Modifier.fillMaxWidth())
                else -> Unit
            }
        },
    ) {
        Text(
            text = stringResource(R.string.pairing_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xl))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ProbeIllustration(
                tint = probeState.phase.illustrationTint(),
                pulsing = probeState.phase == ProbeConnectionPhase.SEARCHING || probeState.phase == ProbeConnectionPhase.CONNECTING,
                modifier = Modifier.size(IllustrationSize),
            )
        }
        Spacer(Modifier.height(spacing.xl))

        PairingStatusCard(probeState = probeState)
    }
}

@Composable
private fun ProbeConnectionPhase.illustrationTint(): Color = when (this) {
    ProbeConnectionPhase.FAILED -> SkinthesiaTheme.colors.error
    ProbeConnectionPhase.CONNECTED, ProbeConnectionPhase.CALIBRATING, ProbeConnectionPhase.READY -> SkinthesiaTheme.colors.positive
    else -> SkinthesiaTheme.colors.primary
}

@Composable
private fun PairingStatusCard(probeState: ProbeState) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    SkinthesiaCard {
        when (probeState.phase) {
            ProbeConnectionPhase.SEARCHING -> LoadingRow(text = stringResource(R.string.pairing_searching))

            ProbeConnectionPhase.DISCOVERED -> DeviceRow(
                name = probeState.deviceName ?: stringResource(R.string.pairing_device_name),
                status = stringResource(R.string.pairing_ready_to_connect),
                statusColor = colors.textSecondary,
            )

            ProbeConnectionPhase.CONNECTING -> LoadingRow(text = stringResource(R.string.pairing_connecting))

            ProbeConnectionPhase.CONNECTED -> Column {
                DeviceRow(
                    name = probeState.deviceName ?: stringResource(R.string.pairing_device_name),
                    status = stringResource(R.string.pairing_connected),
                    statusColor = colors.onPositiveContainer,
                )
                probeState.batteryLevel?.let { level ->
                    Spacer(Modifier.height(spacing.sm))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(SkinthesiaIcons.Battery, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(spacing.iconMedium))
                        Spacer(Modifier.width(spacing.xs))
                        Text(
                            text = stringResource(R.string.pairing_battery_format, level.displayLabel()),
                            style = typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                }
            }

            ProbeConnectionPhase.FAILED -> Column {
                Text(text = stringResource(R.string.pairing_failed_title), style = typography.label, color = colors.error)
                Spacer(Modifier.height(spacing.xxs))
                Text(text = stringResource(R.string.pairing_failed_body), style = typography.bodySmall, color = colors.textSecondary)
            }

            ProbeConnectionPhase.IDLE, ProbeConnectionPhase.CALIBRATING, ProbeConnectionPhase.READY -> Unit
        }
    }
}

@Composable
private fun LoadingRow(text: String) {
    val spacing = SkinthesiaTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically) {
        SkinthesiaLoadingIndicator(size = spacing.lg)
        Spacer(Modifier.width(spacing.sm))
        Text(text = text, style = SkinthesiaTheme.typography.body, color = SkinthesiaTheme.colors.textSecondary)
    }
}

@Composable
private fun DeviceRow(name: String, status: String, statusColor: Color) {
    val typography = SkinthesiaTheme.typography
    Column {
        Text(text = name, style = typography.title, color = SkinthesiaTheme.colors.textPrimary)
        Text(text = status, style = typography.label, color = statusColor)
    }
}

@Composable
private fun ProbeBatteryLevel.displayLabel(): String = stringResource(
    when (this) {
        ProbeBatteryLevel.LOW -> R.string.pairing_battery_low
        ProbeBatteryLevel.MEDIUM -> R.string.pairing_battery_medium
        ProbeBatteryLevel.HIGH -> R.string.pairing_battery_high
    },
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun DevicePairingScreenSearchingPreview() {
    SkinthesiaTheme {
        DevicePairingScreen(
            probeState = ProbeState(phase = ProbeConnectionPhase.SEARCHING),
            onConnect = {},
            onRetry = {},
            onBack = {},
            onContinue = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun DevicePairingScreenConnectedPreview() {
    SkinthesiaTheme {
        DevicePairingScreen(
            probeState = ProbeState(
                phase = ProbeConnectionPhase.CONNECTED,
                deviceName = "Skinthesia Probe",
                batteryLevel = ProbeBatteryLevel.HIGH,
            ),
            onConnect = {},
            onRetry = {},
            onBack = {},
            onContinue = {},
        )
    }
}
