package com.skinthesia.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Elevation levels. Skinthesia surfaces stay close to the page; depth is suggested, never dramatic. */
@Immutable
data class SkinthesiaElevation(
    val none: Dp = 0.dp,
    /** Resting cards. */
    val low: Dp = 2.dp,
    /** Floating controls and bars. */
    val medium: Dp = 6.dp,
    /** Menus, sheets and dialogs. */
    val high: Dp = 16.dp,
)

val LocalSkinthesiaElevation = staticCompositionLocalOf { SkinthesiaElevation() }

/**
 * A soft shadow tinted with the warm [color] instead of neutral grey. Apply it before `clip` and
 * `background` so it renders outside the surface. Tinting needs API 28+; older devices fall back
 * to the default system shadow.
 */
fun Modifier.skinthesiaShadow(elevation: Dp, shape: Shape, color: Color): Modifier =
    if (elevation <= 0.dp) {
        this
    } else {
        shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = color,
            spotColor = color,
        )
    }
