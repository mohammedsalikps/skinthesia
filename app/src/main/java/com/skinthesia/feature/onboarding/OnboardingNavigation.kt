package com.skinthesia.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.skinthesia.feature.goals.SkinGoalsRoute
import com.skinthesia.feature.goals.SkinGoalsScreen
import com.skinthesia.feature.goals.TwelveWeekGoalRoute
import com.skinthesia.feature.goals.TwelveWeekGoalScreen
import com.skinthesia.feature.lifestyle.LifestyleRoute
import com.skinthesia.feature.lifestyle.LifestyleScreen
import com.skinthesia.feature.profile.CreateProfileRoute
import com.skinthesia.feature.profile.CreateProfileScreen
import com.skinthesia.feature.questionnaire.SkinQuestionnaireRoute
import com.skinthesia.feature.questionnaire.SkinQuestionnaireScreen
import com.skinthesia.feature.selfie.PhotoQualityCheckRoute
import com.skinthesia.feature.selfie.PhotoQualityCheckScreen
import com.skinthesia.feature.selfie.RetakeGuidanceRoute
import com.skinthesia.feature.selfie.RetakeGuidanceScreen
import com.skinthesia.feature.selfie.TakeSelfieRoute
import com.skinthesia.feature.selfie.TakeSelfieScreen
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingGraphRoute

/**
 * Create Profile through 12-Week Goal as one nested graph sharing a single [OnboardingViewModel].
 * Later onboarding steps (questionnaire, lifestyle, ...) can extend this same graph without
 * changing how the shared answers are scoped or read.
 */
fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    onOnboardingComplete: () -> Unit,
) {
    navigation<OnboardingGraphRoute>(startDestination = CreateProfileRoute) {
        composable<CreateProfileRoute> { entry ->
            val viewModel = entry.onboardingViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            CreateProfileScreen(
                name = state.profile.name,
                ageInput = state.profile.ageInput,
                gender = state.profile.gender,
                canContinue = state.canContinueFromProfile,
                onNameChange = viewModel::updateName,
                onAgeChange = viewModel::updateAge,
                onGenderSelect = viewModel::selectGender,
                onBack = { navController.navigateUp() },
                onContinue = { navController.navigate(TakeSelfieRoute) },
            )
        }
        composable<TakeSelfieRoute> { entry ->
            val viewModel = entry.onboardingViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            TakeSelfieScreen(
                hasPhoto = state.photo != null,
                onCapture = viewModel::captureSelfie,
                onRetake = viewModel::retakeSelfie,
                onBack = { navController.navigateUp() },
                onContinue = { navController.navigate(PhotoQualityCheckRoute) },
            )
        }
        composable<PhotoQualityCheckRoute> { entry ->
            val viewModel = entry.onboardingViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            PhotoQualityCheckScreen(
                quality = state.photoQuality,
                onQualityCheckReady = viewModel::runQualityCheck,
                onBack = { navController.navigateUp() },
                onUsePhoto = { navController.navigate(SkinGoalsRoute) },
                onRetake = { navController.navigate(RetakeGuidanceRoute) },
            )
        }
        composable<RetakeGuidanceRoute> { entry ->
            val viewModel = entry.onboardingViewModel(navController)
            RetakeGuidanceScreen(
                onBack = { navController.navigateUp() },
                onRetakePhoto = {
                    viewModel.retakeSelfie()
                    navController.navigate(TakeSelfieRoute) {
                        popUpTo(TakeSelfieRoute) { inclusive = true }
                    }
                },
            )
        }
        composable<SkinGoalsRoute> { entry ->
            val viewModel = entry.onboardingViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            SkinGoalsScreen(
                selectedGoals = state.selectedGoals,
                canContinue = state.canContinueFromGoals,
                onToggleGoal = viewModel::toggleGoal,
                onBack = { navController.navigateUp() },
                onContinue = { navController.navigate(TwelveWeekGoalRoute) },
            )
        }
        composable<TwelveWeekGoalRoute> { entry ->
            val viewModel = entry.onboardingViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            TwelveWeekGoalScreen(
                selectedGoals = state.selectedGoals,
                statement = state.goalStatement,
                isEdited = state.goalStatementEdited,
                canContinue = state.canConfirmGoalStatement,
                onStatementChange = viewModel::updateGoalStatement,
                onResetToSuggested = viewModel::resetGoalStatementToSuggested,
                onBack = { navController.navigateUp() },
                onContinue = { navController.navigate(SkinQuestionnaireRoute) },
            )
        }
        composable<SkinQuestionnaireRoute> { entry ->
            val viewModel = entry.onboardingViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            SkinQuestionnaireScreen(
                skinType = state.questionnaire.skinType,
                sensitivity = state.questionnaire.sensitivity,
                routineLevel = state.questionnaire.routineLevel,
                concerns = state.questionnaire.concerns,
                productSensitivities = state.questionnaire.productSensitivities,
                onSkinTypeSelect = viewModel::selectSkinType,
                onSensitivitySelect = viewModel::selectSkinSensitivity,
                onRoutineLevelSelect = viewModel::selectRoutineLevel,
                onToggleConcern = viewModel::toggleSkinConcern,
                onToggleProductSensitivity = viewModel::toggleProductSensitivity,
                onBack = { navController.navigateUp() },
                onContinue = { navController.navigate(LifestyleRoute) },
            )
        }
        composable<LifestyleRoute> { entry ->
            val viewModel = entry.onboardingViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            LifestyleScreen(
                lifestyle = state.lifestyle,
                onSleepQualitySelect = viewModel::selectSleepQuality,
                onSleepDurationChange = viewModel::updateSleepDuration,
                onWaterIntakeChange = viewModel::updateWaterIntake,
                onStressLevelSelect = viewModel::selectStressLevel,
                onSunExposureSelect = viewModel::selectSunExposure,
                onOutdoorActivitySelect = viewModel::selectOutdoorActivity,
                onActivityLevelSelect = viewModel::selectActivityLevel,
                onSmokingStatusSelect = viewModel::selectSmokingStatus,
                onDietPatternSelect = viewModel::selectDietPattern,
                onBack = { navController.navigateUp() },
                onContinue = onOnboardingComplete,
            )
        }
    }
}

/**
 * Resolves the [OnboardingViewModel] shared by every destination in [OnboardingGraphRoute], scoped
 * to that graph's own back stack entry rather than to the individual screen.
 */
@Composable
private fun NavBackStackEntry.onboardingViewModel(navController: NavHostController): OnboardingViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(OnboardingGraphRoute) }
    return remember(parentEntry) { ViewModelProvider(parentEntry)[OnboardingViewModel::class.java] }
}
