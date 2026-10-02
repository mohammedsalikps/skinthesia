package com.skinthesia.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.domain.model.ActivityLevel
import com.skinthesia.domain.model.DietPattern
import com.skinthesia.domain.model.Gender
import com.skinthesia.domain.model.LifestyleProfile
import com.skinthesia.domain.model.OnboardingProfile
import com.skinthesia.domain.model.OutdoorActivityFrequency
import com.skinthesia.domain.model.PhotoQualityCheck
import com.skinthesia.domain.model.ProductSensitivity
import com.skinthesia.domain.model.QualityLevel
import com.skinthesia.domain.model.SelfiePhoto
import com.skinthesia.domain.model.SkinConcern
import com.skinthesia.domain.model.SkinGoal
import com.skinthesia.domain.model.SkinQuestionnaire
import com.skinthesia.domain.model.SkinSensitivity
import com.skinthesia.domain.model.SkincareRoutineLevel
import com.skinthesia.domain.model.SkinType
import com.skinthesia.domain.model.SleepQuality
import com.skinthesia.domain.model.SmokingStatus
import com.skinthesia.domain.model.StressLevel
import com.skinthesia.domain.model.SunExposure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

/** Everything the Create Profile through Lifestyle screens read and write. */
data class OnboardingUiState(
    val profile: OnboardingProfile = OnboardingProfile(),
    val photo: SelfiePhoto? = null,
    val photoQuality: PhotoQualityCheck? = null,
    val selectedGoals: Set<SkinGoal> = emptySet(),
    val goalStatement: String = "",
    val goalStatementEdited: Boolean = false,
    val questionnaire: SkinQuestionnaire = SkinQuestionnaire(),
    val lifestyle: LifestyleProfile = LifestyleProfile(),
) {
    val canContinueFromProfile: Boolean get() = profile.name.isNotBlank() && profile.age != null
    val canContinueFromSelfie: Boolean get() = photo != null
    val canContinueFromGoals: Boolean get() = selectedGoals.isNotEmpty()
    val canConfirmGoalStatement: Boolean get() = goalStatement.isNotBlank()
}

/**
 * The Create Profile through Lifestyle screens' facade over [SkinthesiaRepository]: profile,
 * goals, goal statement, questionnaire and lifestyle all live centrally now, so Dashboard, Profile,
 * Journey and the plan screens can read the same state directly instead of a sibling back-stack
 * read. [photo] and [photoQuality] stay local and unpersisted — transient capture state that only
 * this flow itself needs, and never raw image bytes to begin with.
 */
class OnboardingViewModel : ViewModel() {

    private val _photo = MutableStateFlow<SelfiePhoto?>(null)
    private val _photoQuality = MutableStateFlow<PhotoQualityCheck?>(null)

    val uiState: StateFlow<OnboardingUiState> = combine(
        SkinthesiaRepository.state,
        _photo,
        _photoQuality,
    ) { repositoryState, photo, photoQuality ->
        OnboardingUiState(
            profile = repositoryState.onboardingProfile,
            photo = photo,
            photoQuality = photoQuality,
            selectedGoals = repositoryState.selectedGoals,
            goalStatement = repositoryState.goalStatement,
            goalStatementEdited = repositoryState.goalStatementEdited,
            questionnaire = repositoryState.questionnaire,
            lifestyle = repositoryState.lifestyle,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, OnboardingUiState())

    // ---- Create Profile ------------------------------------------------

    fun updateName(name: String) {
        SkinthesiaRepository.updateProfile { it.copy(name = name) }
    }

    fun updateAge(ageInput: String) {
        val digitsOnly = ageInput.filter(Char::isDigit).take(3)
        SkinthesiaRepository.updateProfile { it.copy(ageInput = digitsOnly) }
    }

    fun selectGender(gender: Gender) {
        SkinthesiaRepository.updateProfile { it.copy(gender = gender) }
    }

    // ---- Take Selfie / Photo Quality Check / Retake Guidance -----------

    /** Simulates a capture. A real CameraX capture can call this with the saved file's identity instead. */
    fun captureSelfie() {
        _photo.value = SelfiePhoto(id = UUID.randomUUID().toString(), capturedAtMillis = System.currentTimeMillis())
    }

    fun retakeSelfie() {
        _photo.value = null
        _photoQuality.value = null
    }

    /** Simulates an analyzer. A real implementation can populate the same [PhotoQualityCheck] shape. */
    fun runQualityCheck() {
        if (_photoQuality.value != null) return
        _photoQuality.value = PhotoQualityCheck(lighting = QualityLevel.GOOD, facePositioning = QualityLevel.GOOD, clarity = QualityLevel.GOOD)
    }

    // ---- Skin Goals ------------------------------------------------------

    fun toggleGoal(goal: SkinGoal) {
        SkinthesiaRepository.toggleGoal(goal)
    }

    // ---- 12-Week Goal ------------------------------------------------------

    fun updateGoalStatement(statement: String) {
        SkinthesiaRepository.updateGoalStatement(statement)
    }

    fun resetGoalStatementToSuggested() {
        SkinthesiaRepository.resetGoalStatementToSuggested()
    }

    // ---- Skin Questionnaire ------------------------------------------------

    fun selectSkinType(skinType: SkinType) = updateQuestionnaire { it.copy(skinType = skinType) }

    fun selectSkinSensitivity(sensitivity: SkinSensitivity) = updateQuestionnaire { it.copy(sensitivity = sensitivity) }

    fun selectRoutineLevel(level: SkincareRoutineLevel) = updateQuestionnaire { it.copy(routineLevel = level) }

    fun toggleSkinConcern(concern: SkinConcern) = updateQuestionnaire {
        it.copy(concerns = if (concern in it.concerns) it.concerns - concern else it.concerns + concern)
    }

    /** Selecting "None / not sure" clears any other choice, and vice versa. */
    fun toggleProductSensitivity(sensitivity: ProductSensitivity) = updateQuestionnaire {
        val current = it.productSensitivities
        val next = when {
            sensitivity == ProductSensitivity.NONE_OR_NOT_SURE ->
                if (ProductSensitivity.NONE_OR_NOT_SURE in current) emptySet() else setOf(ProductSensitivity.NONE_OR_NOT_SURE)
            sensitivity in current -> current - sensitivity
            else -> (current - ProductSensitivity.NONE_OR_NOT_SURE) + sensitivity
        }
        it.copy(productSensitivities = next)
    }

    // ---- Lifestyle ------------------------------------------------------

    fun selectSleepQuality(quality: SleepQuality) = updateLifestyle { it.copy(sleepQuality = quality) }

    fun updateSleepDuration(hours: Int) = updateLifestyle { it.copy(sleepDurationHours = hours) }

    fun updateWaterIntake(glasses: Int) = updateLifestyle { it.copy(waterIntakeGlasses = glasses) }

    fun selectStressLevel(level: StressLevel) = updateLifestyle { it.copy(stressLevel = level) }

    fun selectSunExposure(exposure: SunExposure) = updateLifestyle { it.copy(sunExposure = exposure) }

    fun selectOutdoorActivity(frequency: OutdoorActivityFrequency) = updateLifestyle { it.copy(outdoorActivity = frequency) }

    fun selectActivityLevel(level: ActivityLevel) = updateLifestyle { it.copy(activityLevel = level) }

    fun selectSmokingStatus(status: SmokingStatus) = updateLifestyle { it.copy(smokingStatus = status) }

    fun selectDietPattern(pattern: DietPattern) = updateLifestyle { it.copy(dietPattern = pattern) }

    private fun updateQuestionnaire(transform: (SkinQuestionnaire) -> SkinQuestionnaire) {
        SkinthesiaRepository.updateQuestionnaire(transform)
    }

    private fun updateLifestyle(transform: (LifestyleProfile) -> LifestyleProfile) {
        SkinthesiaRepository.updateLifestyle(transform)
    }
}
