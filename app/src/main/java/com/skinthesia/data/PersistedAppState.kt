package com.skinthesia.data

import com.skinthesia.domain.model.LifestyleProfile
import com.skinthesia.domain.model.OnboardingProfile
import com.skinthesia.domain.model.PersonalizedPlan
import com.skinthesia.domain.model.SkinGoal
import com.skinthesia.domain.model.SkinPrint
import com.skinthesia.domain.model.SkinQuestionnaire
import com.skinthesia.domain.model.WeeklyCheckIn
import kotlinx.serialization.Serializable

/**
 * A single, serializable snapshot of the app's centralized local state — the existing domain
 * models, held once here instead of scattered across per-graph ViewModels and rediscovered through
 * fragile sibling back-stack reads. Persisted as one JSON blob (see [SkinthesiaRepository]); never
 * holds raw image bytes or any other sensitive raw capture data.
 */
@Serializable
data class PersistedAppState(
    val onboardingProfile: OnboardingProfile = OnboardingProfile(),
    val selectedGoals: Set<SkinGoal> = emptySet(),
    val goalStatement: String = "",
    val goalStatementEdited: Boolean = false,
    val questionnaire: SkinQuestionnaire = SkinQuestionnaire(),
    val lifestyle: LifestyleProfile = LifestyleProfile(),
    /** Every legitimate SkinPrint ever produced, oldest to newest — appended to, never overwritten. */
    val skinPrintHistory: List<SkinPrint> = emptyList(),
    /** Carries its own routine-completion and add-to-plan flags; never a second routine model. */
    val personalizedPlan: PersonalizedPlan? = null,
    val weeklyCheckIn: WeeklyCheckIn? = null,
)
