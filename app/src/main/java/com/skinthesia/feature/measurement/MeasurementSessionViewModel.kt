package com.skinthesia.feature.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinthesia.domain.model.FaceRegion
import com.skinthesia.domain.model.MeasurementSession
import com.skinthesia.domain.model.MeasurementStatus
import com.skinthesia.domain.model.RegionMeasurement
import com.skinthesia.hardware.measurement.FakeSkinRegionMeasurementProvider
import com.skinthesia.hardware.measurement.MeasurementFailedException
import com.skinthesia.hardware.measurement.MeasurementFailureReason
import com.skinthesia.hardware.measurement.SkinRegionMeasurementProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/** What the measurement screens need to render: the durable session plus the in-flight reading's live progress. */
data class MeasurementUiState(
    val session: MeasurementSession,
    val activeRegionProgress: Float = 0f,
    val failureReason: MeasurementFailureReason? = null,
) {
    fun statusOf(region: FaceRegion): MeasurementStatus = session.regions[region]?.status ?: MeasurementStatus.PENDING

    val completedRegions: Set<FaceRegion>
        get() = session.regions.filterValues { it.status == MeasurementStatus.CAPTURED }.keys
}

/**
 * Drives one probe measurement sitting across the Forehead, Left Cheek and Right Cheek screens,
 * sharing a single [MeasurementSession]. [measurementProvider] is a [FakeSkinRegionMeasurementProvider]
 * today; a real implementation of [SkinRegionMeasurementProvider] can replace it here without any
 * screen change.
 */
class MeasurementSessionViewModel : ViewModel() {

    private val measurementProvider: SkinRegionMeasurementProvider = FakeSkinRegionMeasurementProvider()

    private val _uiState = MutableStateFlow(
        MeasurementUiState(
            session = MeasurementSession(
                id = UUID.randomUUID().toString(),
                profileId = UUID.randomUUID().toString(),
                startedAtMillis = System.currentTimeMillis(),
            ),
        ),
    )
    val uiState: StateFlow<MeasurementUiState> = _uiState.asStateFlow()

    fun measure(region: FaceRegion, deviceId: String?) {
        if (_uiState.value.statusOf(region) == MeasurementStatus.IN_PROGRESS) return
        viewModelScope.launch {
            recordStatus(region, MeasurementStatus.IN_PROGRESS, deviceId)
            _uiState.update { it.copy(activeRegionProgress = 0f, failureReason = null) }
            try {
                measurementProvider.measure(region).collect { progress ->
                    _uiState.update { it.copy(activeRegionProgress = progress.fraction) }
                }
                recordStatus(region, MeasurementStatus.CAPTURED, deviceId, capturedAtMillis = System.currentTimeMillis())
            } catch (failure: MeasurementFailedException) {
                recordStatus(region, MeasurementStatus.FAILED, deviceId)
                _uiState.update { it.copy(failureReason = failure.reason) }
            }
        }
    }

    private fun recordStatus(
        region: FaceRegion,
        status: MeasurementStatus,
        deviceId: String?,
        capturedAtMillis: Long? = null,
    ) {
        _uiState.update { state ->
            val record = RegionMeasurement(
                sessionId = state.session.id,
                region = region,
                status = status,
                capturedAtMillis = capturedAtMillis,
                deviceId = deviceId,
            )
            state.copy(session = state.session.copy(regions = state.session.regions + (region to record)))
        }
    }
}
