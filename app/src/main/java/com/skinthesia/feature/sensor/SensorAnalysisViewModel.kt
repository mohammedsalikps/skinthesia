package com.skinthesia.feature.sensor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinthesia.domain.model.MeasurementSession
import com.skinthesia.domain.model.SensorAnalysisSession
import com.skinthesia.hardware.sensor.FakeSensorAnalysisProvider
import com.skinthesia.hardware.sensor.SensorAnalysisFailedException
import com.skinthesia.hardware.sensor.SensorAnalysisProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Everything the Sensor Analysis screen can be showing. */
sealed interface SensorAnalysisUiState {
    /** The measurement session from Step 6 could not be found (e.g. reached this screen out of order). */
    data object MissingSession : SensorAnalysisUiState

    /** The session exists but not all three regions were captured. */
    data object IncompleteRegions : SensorAnalysisUiState

    data class Analyzing(val progress: Float) : SensorAnalysisUiState

    data class Complete(val result: SensorAnalysisSession) : SensorAnalysisUiState

    /** The reading itself failed partway through; retrying re-runs it. */
    data object Failed : SensorAnalysisUiState
}

/**
 * Drives the Sensor Analysis screen for one completed [MeasurementSession]. [sensorProvider] is a
 * [FakeSensorAnalysisProvider] today; a real implementation of [SensorAnalysisProvider] can replace
 * it here without any screen change.
 */
class SensorAnalysisViewModel : ViewModel() {

    private val sensorProvider: SensorAnalysisProvider = FakeSensorAnalysisProvider()
    private var hasStarted = false
    private var lastSession: MeasurementSession? = null

    private val _uiState = MutableStateFlow<SensorAnalysisUiState>(SensorAnalysisUiState.MissingSession)
    val uiState: StateFlow<SensorAnalysisUiState> = _uiState.asStateFlow()

    /** Starts reading once for [session] (which may be null if it could not be found); call [retry] after a failure. */
    fun start(session: MeasurementSession?) {
        if (hasStarted) return
        hasStarted = true
        beginAnalysis(session)
    }

    fun retry() {
        beginAnalysis(lastSession)
    }

    private fun beginAnalysis(session: MeasurementSession?) {
        lastSession = session
        when {
            session == null -> _uiState.value = SensorAnalysisUiState.MissingSession
            !session.isComplete -> _uiState.value = SensorAnalysisUiState.IncompleteRegions
            else -> runAnalysis(session)
        }
    }

    private fun runAnalysis(session: MeasurementSession) {
        _uiState.value = SensorAnalysisUiState.Analyzing(progress = 0f)
        viewModelScope.launch {
            try {
                sensorProvider.analyze(session).collect { progress ->
                    _uiState.value = progress.result?.let { SensorAnalysisUiState.Complete(it) }
                        ?: SensorAnalysisUiState.Analyzing(progress.fraction)
                }
            } catch (failure: SensorAnalysisFailedException) {
                _uiState.value = SensorAnalysisUiState.Failed
            }
        }
    }
}
