package com.skinthesia.hardware.probe

import kotlinx.coroutines.flow.StateFlow

/** Where a Skinthesia Probe is in the discovery-to-ready lifecycle. */
enum class ProbeConnectionPhase { IDLE, SEARCHING, DISCOVERED, CONNECTING, CONNECTED, CALIBRATING, READY, FAILED }

/** A coarse, non-numeric battery reading. Deliberately not a percentage: nothing here is a real measurement yet. */
enum class ProbeBatteryLevel { LOW, MEDIUM, HIGH }

/**
 * What the UI needs to know about a Skinthesia Probe at any moment. [isSimulated] is true for
 * every implementation until real hardware exists, and screens should say so wherever this state
 * is shown, rather than implying an actual physical connection.
 */
data class ProbeState(
    val phase: ProbeConnectionPhase = ProbeConnectionPhase.IDLE,
    val deviceName: String? = null,
    /** A simulated stable identifier for the connected device, set once connected. */
    val deviceId: String? = null,
    val batteryLevel: ProbeBatteryLevel? = null,
    val isSimulated: Boolean = true,
)

/**
 * Boundary between the UI and the Skinthesia Probe hardware. [FakeSkinthesiaProbeManager] is the
 * only implementation today; a future BLE-backed implementation can satisfy this same contract —
 * discovery, connection, battery, calibration and measurement readiness — without any screen
 * needing to change. No real BLE UUIDs, packet formats or sensor specifications are assumed here.
 */
interface SkinthesiaProbeManager {
    val state: StateFlow<ProbeState>

    /** Begins looking for a nearby probe. Safe to call again after [ProbeConnectionPhase.FAILED]. */
    suspend fun search()

    /** Connects to the probe found by [search]. No-ops unless a probe was [ProbeConnectionPhase.DISCOVERED]. */
    suspend fun connect()

    /** Runs sensor calibration. No-ops unless the probe is [ProbeConnectionPhase.CONNECTED]. */
    suspend fun calibrate()

    /** Clears any error and returns to [ProbeConnectionPhase.IDLE]. */
    fun reset()
}
