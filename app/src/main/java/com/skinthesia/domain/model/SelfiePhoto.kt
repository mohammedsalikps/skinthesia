package com.skinthesia.domain.model

/**
 * A captured baseline selfie. [id] and [capturedAtMillis] are placeholders until real CameraX
 * capture supplies a file path; nothing else in the flow needs to change when it does.
 */
data class SelfiePhoto(
    val id: String,
    val capturedAtMillis: Long,
)
