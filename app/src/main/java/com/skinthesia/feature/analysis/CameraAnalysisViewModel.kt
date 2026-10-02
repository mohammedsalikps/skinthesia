package com.skinthesia.feature.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinthesia.ai.vision.CameraAnalysisFailedException
import com.skinthesia.ai.vision.CameraSkinAnalysisProvider
import com.skinthesia.ai.vision.FakeCameraSkinAnalysisProvider
import com.skinthesia.domain.model.CameraAnalysisStatus
import com.skinthesia.domain.model.CameraSkinAnalysis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private const val PreparingDelayMillis = 500L

/** What the Camera AI Analysis screen needs to render. */
data class CameraAnalysisUiState(
    val analysis: CameraSkinAnalysis,
    val progress: Float = 0f,
)

/**
 * Drives the Camera AI Analysis screen for one selfie. [analysisProvider] is a
 * [FakeCameraSkinAnalysisProvider] today; a real computer-vision/ML implementation of
 * [CameraSkinAnalysisProvider] can replace it here without any screen change.
 */
class CameraAnalysisViewModel : ViewModel() {

    private val analysisProvider: CameraSkinAnalysisProvider = FakeCameraSkinAnalysisProvider()
    private var hasStarted = false

    private val _uiState = MutableStateFlow(
        CameraAnalysisUiState(
            analysis = CameraSkinAnalysis(
                id = UUID.randomUUID().toString(),
                photoId = "",
                status = CameraAnalysisStatus.PREPARING,
                startedAtMillis = System.currentTimeMillis(),
            ),
        ),
    )
    val uiState: StateFlow<CameraAnalysisUiState> = _uiState.asStateFlow()

    /** Starts the analysis once; later calls while already started are ignored. Use [retry] after a failure. */
    fun analyze(photoId: String) {
        if (hasStarted) return
        hasStarted = true
        runAnalysis(photoId)
    }

    fun retry(photoId: String) {
        runAnalysis(photoId)
    }

    private fun runAnalysis(photoId: String) {
        _uiState.update {
            it.copy(
                progress = 0f,
                analysis = it.analysis.copy(
                    photoId = photoId,
                    status = CameraAnalysisStatus.PREPARING,
                    startedAtMillis = System.currentTimeMillis(),
                ),
            )
        }
        viewModelScope.launch {
            delay(PreparingDelayMillis)
            _uiState.update { it.copy(analysis = it.analysis.copy(status = CameraAnalysisStatus.ANALYZING)) }
            try {
                analysisProvider.analyze(photoId).collect { progress ->
                    _uiState.update { state ->
                        state.copy(
                            progress = progress.fraction,
                            analysis = progress.result?.let { result ->
                                state.analysis.copy(
                                    status = CameraAnalysisStatus.COMPLETE,
                                    completedAtMillis = System.currentTimeMillis(),
                                    indicators = result,
                                )
                            } ?: state.analysis,
                        )
                    }
                }
            } catch (failure: CameraAnalysisFailedException) {
                _uiState.update { it.copy(analysis = it.analysis.copy(status = CameraAnalysisStatus.FAILED)) }
            }
        }
    }
}
