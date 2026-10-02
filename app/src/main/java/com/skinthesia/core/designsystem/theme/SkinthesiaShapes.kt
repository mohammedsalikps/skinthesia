package com.skinthesia.core.designsystem.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** Soft, generous corner radii. Components use the semantic roles, not the raw scale. */
@Immutable
data class SkinthesiaShapes(
    val extraSmall: CornerBasedShape = RoundedCornerShape(8.dp),
    val small: CornerBasedShape = RoundedCornerShape(12.dp),
    val medium: CornerBasedShape = RoundedCornerShape(16.dp),
    val large: CornerBasedShape = RoundedCornerShape(20.dp),
    val extraLarge: CornerBasedShape = RoundedCornerShape(28.dp),
    /** Fully rounded ends. */
    val pill: CornerBasedShape = RoundedCornerShape(percent = 50),

    /** Primary, secondary and text buttons. */
    val button: CornerBasedShape = pill,
    /** Content cards. */
    val card: CornerBasedShape = large,
    /** Text fields and pickers. */
    val field: CornerBasedShape = small,
    /** The bottom navigation bar: a flat, full-width surface. */
    val navigationBar: Shape = RectangleShape,
    /** Selected-destination indicator and the raised centre action in the bottom navigation. */
    val navigationIndicator: CornerBasedShape = pill,
)

val LocalSkinthesiaShapes = staticCompositionLocalOf { SkinthesiaShapes() }

/** Bridges the corner scale into Material 3. */
fun SkinthesiaShapes.toMaterialShapes(): Shapes = Shapes(
    extraSmall = extraSmall,
    small = small,
    medium = medium,
    large = large,
    extraLarge = extraLarge,
)
