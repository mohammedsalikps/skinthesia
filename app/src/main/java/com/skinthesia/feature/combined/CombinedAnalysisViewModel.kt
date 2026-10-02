package com.skinthesia.feature.combined

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinthesia.ai.combined.CombinedAnalysisFailedException
import com.skinthesia.ai.combined.CombinedAnalysisInput
import com.skinthesia.ai.combined.CombinedSkinAnalysisProvider
import com.skinthesia.ai.combined.FakeCombinedSkinAnalysisProvider
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.domain.model.SkinPrint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Everything the Combined AI Analysis and SkinPrint™ screens can be showing. */
sealed interface CombinedAnalysisUiState {
    /** One or more required earlier steps couldn't be found (e.g. reached this screen out of order). */
    data object MissingInputs : CombinedAnalysisUiState

    data class Combining(val progress: Float) : CombinedAnalysisUiState

    data class Complete(val skinPrint: SkinPrint) : CombinedAnalysisUiState

    /** The combination itself failed partway through; retrying re-runs it with the same inputs. */
    data object Failed : CombinedAnalysisUiState
}

/**
 * Drives both the Combined AI Analysis (processing) and SkinPrint™ (result) screens, sharing one
 * [SkinPrint] once produced. [combinedProvider] is a [FakeCombinedSkinAnalysisProvider] today; a
 * real implementation of [CombinedSkinAnalysisProvider] can replace it here without any screen change.
 */
class CombinedAnalysisViewModel : ViewModel() {

    private val combinedProvider: CombinedSkinAnalysisProvider = FakeCombinedSkinAnalysisProvider()
    private var hasStarted = false
    private var lastInput: CombinedAnalysisInput? = null

    private val _uiState = MutableStateFlow<CombinedAnalysisUiState>(CombinedAnalysisUiState.MissingInputs)
    val uiState: StateFlow<CombinedAnalysisUiState> = _uiState.asStateFlow()

    fun start(input: CombinedAnalysisInput?) {
        if (hasStarted) return
        hasStarted = true
        begin(input)
    }

    fun retry() {
        begin(lastInput)
    }

    private fun begin(input: CombinedAnalysisInput?) {
        lastInput = input
        if (input == null) {
            _uiState.value = CombinedAnalysisUiState.MissingInputs
            return
        }
        _uiState.value = CombinedAnalysisUiState.Combining(progress = 0f)
        viewModelScope.launch {
            try {
                combinedProvider.analyze(input).collect { progress ->
                    val result = progress.result
                    if (result != null) {
                        // Append only on the transition into Complete, so a Flow that re-emits the same
                        // result never appends the same legitimate SkinPrint into history twice.
                        if (_uiState.value !is CombinedAnalysisUiState.Complete) {
                            SkinthesiaRepository.addSkinPrint(result)
                        }
                        _uiState.value = CombinedAnalysisUiState.Complete(result)
                    } else {
                        _uiState.value = CombinedAnalysisUiState.Combining(progress.fraction)
                    }
                }
            } catch (failure: CombinedAnalysisFailedException) {
                _uiState.value = CombinedAnalysisUiState.Failed
            }
        }
    }
}
