package com.skinthesia.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.domain.model.RoutineConsistency
import com.skinthesia.domain.model.SkinFeeling
import com.skinthesia.domain.model.WeeklyChange
import com.skinthesia.domain.model.WeeklyCheckIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** The in-progress answers to this week's check-in, before it is completed. */
data class WeeklyCheckInDraft(
    val skinFeeling: SkinFeeling? = null,
    val routineConsistency: RoutineConsistency? = null,
    val changes: Set<WeeklyChange> = emptySet(),
    val note: String = "",
) {
    val canComplete: Boolean get() = skinFeeling != null && routineConsistency != null
}

/**
 * Weekly Check-in's in-progress draft stays local (it isn't a real check-in until completed), but
 * the completed [checkIn] itself lives in [SkinthesiaRepository] — so it survives navigation, app
 * restart, and is the exact same check-in Skin Journey and Adaptive Plan read, never a duplicate.
 */
class ProgressViewModel : ViewModel() {

    private val _draft = MutableStateFlow(WeeklyCheckInDraft())
    val draft: StateFlow<WeeklyCheckInDraft> = _draft.asStateFlow()

    val checkIn: StateFlow<WeeklyCheckIn?> = SkinthesiaRepository.state
        .map { it.weeklyCheckIn }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SkinthesiaRepository.state.value.weeklyCheckIn)

    fun selectSkinFeeling(feeling: SkinFeeling) {
        _draft.update { it.copy(skinFeeling = feeling) }
    }

    fun selectRoutineConsistency(consistency: RoutineConsistency) {
        _draft.update { it.copy(routineConsistency = consistency) }
    }

    /** Selecting "Nothing significant" clears any other choice, and vice versa — same rule as onboarding's product sensitivities. */
    fun toggleChange(change: WeeklyChange) {
        _draft.update { draft ->
            val current = draft.changes
            val next = when {
                change == WeeklyChange.NOTHING_SIGNIFICANT ->
                    if (WeeklyChange.NOTHING_SIGNIFICANT in current) emptySet() else setOf(WeeklyChange.NOTHING_SIGNIFICANT)
                change in current -> current - change
                else -> (current - WeeklyChange.NOTHING_SIGNIFICANT) + change
            }
            draft.copy(changes = next)
        }
    }

    fun updateNote(note: String) {
        _draft.update { it.copy(note = note) }
    }

    fun completeCheckIn() {
        val draft = _draft.value
        val feeling = draft.skinFeeling ?: return
        val consistency = draft.routineConsistency ?: return
        SkinthesiaRepository.completeWeeklyCheckIn(
            WeeklyCheckIn(
                timestampMillis = System.currentTimeMillis(),
                skinFeeling = feeling,
                routineConsistency = consistency,
                changes = draft.changes,
                note = draft.note.trim().takeIf { it.isNotEmpty() },
            ),
        )
    }
}
