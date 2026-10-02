package com.skinthesia.data

import android.content.Context
import android.content.SharedPreferences
import com.skinthesia.domain.model.LifestyleProfile
import com.skinthesia.domain.model.OnboardingProfile
import com.skinthesia.domain.model.PersonalizedPlan
import com.skinthesia.domain.model.ProductCategory
import com.skinthesia.domain.model.RoutineStep
import com.skinthesia.domain.model.RoutineTime
import com.skinthesia.domain.model.SkinGoal
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinQuestionnaire
import com.skinthesia.domain.model.WeeklyCheckIn
import com.skinthesia.domain.model.suggestedGoalStatement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val PreferencesName = "skinthesia_state"
private const val StateKey = "app_state"

/**
 * The app's single source of truth for profile, goals, questionnaire, lifestyle, SkinPrint
 * history, PersonalizedPlan and WeeklyCheckIn — replacing the fragile cross-graph back-stack reads
 * earlier steps relied on. Holds the EXISTING domain models directly; never a copy of them.
 *
 * A plain singleton rather than a ViewModel, since it must outlive any single navigation graph.
 * [initialize] wires it to local SharedPreferences persistence exactly once, called from
 * [com.skinthesia.SkinthesiaApplication]; every mutation re-persists the whole (small) state as one
 * JSON blob via kotlinx.serialization. No raw image bytes or other sensitive capture data are ever
 * part of this state, and nothing here talks to a backend, a database engine, or the network.
 */
object SkinthesiaRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private var preferences: SharedPreferences? = null

    private val _state = MutableStateFlow(PersistedAppState())
    val state: StateFlow<PersistedAppState> = _state.asStateFlow()

    /** Loads any previously persisted state and wires future mutations to be saved. Safe to call more than once. */
    fun initialize(context: Context) {
        if (preferences != null) return
        val prefs = context.applicationContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        preferences = prefs
        prefs.getString(StateKey, null)?.let { stored ->
            runCatching { json.decodeFromString<PersistedAppState>(stored) }.getOrNull()?.let { _state.value = it }
        }
    }

    /** The most recent legitimate SkinPrint, or null if none has been produced yet. */
    val currentSkinPrint: SkinPrint? get() = _state.value.skinPrintHistory.lastOrNull()

    // ---- Onboarding: profile, goals, questionnaire, lifestyle ----

    fun updateProfile(transform: (OnboardingProfile) -> OnboardingProfile) {
        update { it.copy(onboardingProfile = transform(it.onboardingProfile)) }
    }

    /** Selecting/deselecting a goal also refreshes the suggested statement, unless the person has already edited it. */
    fun toggleGoal(goal: SkinGoal) {
        update { current ->
            val nextGoals = if (goal in current.selectedGoals) current.selectedGoals - goal else current.selectedGoals + goal
            val nextStatement = if (current.goalStatementEdited) current.goalStatement else suggestedGoalStatement(nextGoals.toList())
            current.copy(selectedGoals = nextGoals, goalStatement = nextStatement)
        }
    }

    fun updateGoalStatement(statement: String) {
        update { it.copy(goalStatement = statement, goalStatementEdited = true) }
    }

    fun resetGoalStatementToSuggested() {
        update { it.copy(goalStatement = suggestedGoalStatement(it.selectedGoals.toList()), goalStatementEdited = false) }
    }

    fun updateQuestionnaire(transform: (SkinQuestionnaire) -> SkinQuestionnaire) {
        update { it.copy(questionnaire = transform(it.questionnaire)) }
    }

    fun updateLifestyle(transform: (LifestyleProfile) -> LifestyleProfile) {
        update { it.copy(lifestyle = transform(it.lifestyle)) }
    }

    // ---- SkinPrint history ----

    /** Appends a new legitimate SkinPrint version; never overwrites or replaces an earlier one. */
    fun addSkinPrint(skinPrint: SkinPrint) {
        update { it.copy(skinPrintHistory = it.skinPrintHistory + skinPrint) }
    }

    // ---- Personalized Plan (routine completion and add-to-plan live on the plan itself) ----

    /** Sets the plan once; later calls are ignored so navigating back and forth doesn't reset progress. */
    fun initializePersonalizedPlan(plan: PersonalizedPlan) {
        update { if (it.personalizedPlan == null) it.copy(personalizedPlan = plan) else it }
    }

    fun toggleRoutineStep(time: RoutineTime, order: Int) {
        update { current ->
            val plan = current.personalizedPlan
            if (plan == null) {
                current
            } else {
                fun toggled(steps: List<RoutineStep>) = steps.map { step -> if (step.order == order) step.copy(isCompleted = !step.isCompleted) else step }
                val nextPlan = when (time) {
                    RoutineTime.MORNING -> plan.copy(morningRoutine = toggled(plan.morningRoutine))
                    RoutineTime.EVENING -> plan.copy(eveningRoutine = toggled(plan.eveningRoutine))
                }
                current.copy(personalizedPlan = nextPlan)
            }
        }
    }

    fun toggleRecommendation(category: ProductCategory) {
        update { current ->
            val plan = current.personalizedPlan
            if (plan == null) {
                current
            } else {
                val nextPlan = plan.copy(
                    recommendations = plan.recommendations.map {
                        if (it.category == category) it.copy(isAddedToRoutine = !it.isAddedToRoutine) else it
                    },
                )
                current.copy(personalizedPlan = nextPlan)
            }
        }
    }

    // ---- Weekly Check-in ----

    fun completeWeeklyCheckIn(checkIn: WeeklyCheckIn) {
        update { it.copy(weeklyCheckIn = checkIn) }
    }

    private fun update(transform: (PersistedAppState) -> PersistedAppState) {
        val next = transform(_state.value)
        _state.value = next
        persist(next)
    }

    private fun persist(state: PersistedAppState) {
        val prefs = preferences ?: return
        runCatching { prefs.edit().putString(StateKey, json.encodeToString(state)).apply() }
    }
}
