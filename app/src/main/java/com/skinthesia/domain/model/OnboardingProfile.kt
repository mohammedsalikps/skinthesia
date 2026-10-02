package com.skinthesia.domain.model

import kotlinx.serialization.Serializable

/** Broad demographic categories offered during profile setup, used only to personalise tone. */
enum class Gender { WOMAN, MAN, NON_BINARY, PREFER_NOT_TO_SAY }

/** The minimal identity information collected before onboarding continues. */
@Serializable
data class OnboardingProfile(
    val name: String = "",
    /** Raw text as typed; [age] parses it on demand so the field never rejects a keystroke. */
    val ageInput: String = "",
    val gender: Gender? = null,
) {
    val age: Int? get() = ageInput.toIntOrNull()
}
