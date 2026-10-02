package com.skinthesia.feature.probe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinthesia.hardware.probe.FakeSkinthesiaProbeManager
import com.skinthesia.hardware.probe.ProbeState
import com.skinthesia.hardware.probe.SkinthesiaProbeManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Drives the three probe connection screens from one shared [SkinthesiaProbeManager]. The manager
 * is a [FakeSkinthesiaProbeManager] today; swapping in a real BLE-backed implementation only means
 * changing the assignment below, not the screens or this class's public surface.
 */
class ProbeConnectionViewModel : ViewModel() {

    private val probeManager: SkinthesiaProbeManager = FakeSkinthesiaProbeManager()

    val state: StateFlow<ProbeState> = probeManager.state

    fun search() {
        viewModelScope.launch { probeManager.search() }
    }

    fun connect() {
        viewModelScope.launch { probeManager.connect() }
    }

    fun calibrate() {
        viewModelScope.launch { probeManager.calibrate() }
    }

    fun retry() {
        probeManager.reset()
        search()
    }
}
