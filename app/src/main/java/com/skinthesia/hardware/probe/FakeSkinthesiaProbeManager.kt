package com.skinthesia.hardware.probe

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

private const val SearchDelayMillis = 1600L
private const val ConnectDelayMillis = 1100L
private const val CalibrateDelayMillis = 1800L
private const val SimulatedDeviceName = "Skinthesia Probe"

/** A small, occasional chance that a search doesn't find a probe, purely so the failure/retry UI has something to show. */
private const val SimulatedFailureChance = 0.12f

/**
 * Simulates discovering, connecting to and calibrating a Skinthesia Probe with realistic timing,
 * so the connection screens can be built and demonstrated before real hardware exists. Every value
 * this exposes is fabricated locally on-device; nothing here talks to a physical probe, and
 * [ProbeState.isSimulated] is always true. A real BLE-backed implementation of
 * [SkinthesiaProbeManager] can replace this class wherever it is constructed without any UI change.
 */
class FakeSkinthesiaProbeManager : SkinthesiaProbeManager {

    private val _state = MutableStateFlow(ProbeState())
    override val state: StateFlow<ProbeState> = _state

    override suspend fun search() {
        val current = _state.value.phase
        if (current != ProbeConnectionPhase.IDLE && current != ProbeConnectionPhase.FAILED) return

        _state.update { ProbeState(phase = ProbeConnectionPhase.SEARCHING) }
        delay(SearchDelayMillis)

        if (Random.nextFloat() < SimulatedFailureChance) {
            _state.update { it.copy(phase = ProbeConnectionPhase.FAILED) }
            return
        }
        _state.update { it.copy(phase = ProbeConnectionPhase.DISCOVERED, deviceName = SimulatedDeviceName) }
    }

    override suspend fun connect() {
        if (_state.value.phase != ProbeConnectionPhase.DISCOVERED) return

        _state.update { it.copy(phase = ProbeConnectionPhase.CONNECTING) }
        delay(ConnectDelayMillis)
        _state.update {
            it.copy(
                phase = ProbeConnectionPhase.CONNECTED,
                deviceId = "SKN-" + Random.nextInt(0x1000, 0xFFFF).toString(16).uppercase(),
                batteryLevel = listOf(ProbeBatteryLevel.HIGH, ProbeBatteryLevel.MEDIUM).random(),
            )
        }
    }

    override suspend fun calibrate() {
        if (_state.value.phase != ProbeConnectionPhase.CONNECTED) return

        _state.update { it.copy(phase = ProbeConnectionPhase.CALIBRATING) }
        delay(CalibrateDelayMillis)
        _state.update { it.copy(phase = ProbeConnectionPhase.READY) }
    }

    override fun reset() {
        _state.update { ProbeState() }
    }
}
