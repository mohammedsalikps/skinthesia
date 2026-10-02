package com.skinthesia.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue

/** Duration of press feedback on controls. Short enough to feel immediate, soft enough to feel calm. */
internal const val PressDurationMillis = 120

/**
 * Scale for a subtle press-in effect: the quiet alternative to a ripple on filled surfaces.
 * Read the returned state inside `graphicsLayer` so animation frames skip recomposition.
 */
@Composable
internal fun animatePressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.98f,
): State<Float> {
    val pressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = tween(PressDurationMillis),
        label = "pressScale",
    )
}
