package com.skinthesia.feature.consultation

import com.skinthesia.R

/**
 * A fictional, presentation-only expert profile used to preview the consultation experience —
 * never a real person, a real credential, or a real medical professional.
 */
data class SimulatedExpert(
    val id: String,
    val roleRes: Int,
    val focusRes: Int,
)

val SimulatedExperts: List<SimulatedExpert> = listOf(
    SimulatedExpert(id = "expert-1", roleRes = R.string.consultation_expert_1_role, focusRes = R.string.consultation_expert_1_focus),
    SimulatedExpert(id = "expert-2", roleRes = R.string.consultation_expert_2_role, focusRes = R.string.consultation_expert_2_focus),
    SimulatedExpert(id = "expert-3", roleRes = R.string.consultation_expert_3_role, focusRes = R.string.consultation_expert_3_focus),
)
