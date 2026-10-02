package com.skinthesia.feature.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinthesia.data.SkinthesiaRepository
import com.skinthesia.domain.model.PersonalizedPlan
import com.skinthesia.domain.model.ProductCategory
import com.skinthesia.domain.model.RoutineTime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * A facade over [SkinthesiaRepository]'s [PersonalizedPlan]: routine-completion and add-to-plan
 * state now live centrally, so Dashboard, Marketplace, Journey and Adaptive Plan all read and
 * mutate the exact same plan without a sibling back-stack read — never a second copy of it.
 */
class PersonalizedPlanViewModel : ViewModel() {

    val plan: StateFlow<PersonalizedPlan?> = SkinthesiaRepository.state
        .map { it.personalizedPlan }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SkinthesiaRepository.state.value.personalizedPlan)

    /** Sets the plan once; later calls are ignored so navigating back and forth doesn't reset progress. */
    fun initialize(plan: PersonalizedPlan) {
        SkinthesiaRepository.initializePersonalizedPlan(plan)
    }

    fun toggleRoutineStep(time: RoutineTime, order: Int) {
        SkinthesiaRepository.toggleRoutineStep(time, order)
    }

    fun toggleRecommendation(category: ProductCategory) {
        SkinthesiaRepository.toggleRecommendation(category)
    }
}
