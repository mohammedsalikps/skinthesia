package com.skinthesia.core.designsystem.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

private val DefaultTypography = SkinthesiaTypography()
private val DefaultShapes = SkinthesiaShapes()
private val DefaultSpacing = SkinthesiaSpacing()
private val DefaultElevation = SkinthesiaElevation()
private val MaterialTypography = DefaultTypography.toMaterialTypography()
private val MaterialShapes = DefaultShapes.toMaterialShapes()

/**
 * Root theme. Provides the Skinthesia tokens and configures Material 3 with the same palette,
 * type scale and shapes, so any Material component used inside inherits the brand.
 *
 * Only the light identity exists today; a dark scheme is another [SkinthesiaColors] instance.
 */
@Composable
fun SkinthesiaTheme(
    colors: SkinthesiaColors = SkinthesiaLightColors,
    content: @Composable () -> Unit,
) {
    val materialColors = remember(colors) { colors.toMaterialColorScheme() }
    CompositionLocalProvider(
        LocalSkinthesiaColors provides colors,
        LocalSkinthesiaTypography provides DefaultTypography,
        LocalSkinthesiaShapes provides DefaultShapes,
        LocalSkinthesiaSpacing provides DefaultSpacing,
        LocalSkinthesiaElevation provides DefaultElevation,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = MaterialTypography,
            shapes = MaterialShapes,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides colors.textPrimary,
                LocalTextStyle provides DefaultTypography.body,
                content = content,
            )
        }
    }
}

/** Token accessors: `SkinthesiaTheme.colors.primary`, `SkinthesiaTheme.spacing.md`, and so on. */
object SkinthesiaTheme {
    val colors: SkinthesiaColors
        @Composable @ReadOnlyComposable get() = LocalSkinthesiaColors.current

    val typography: SkinthesiaTypography
        @Composable @ReadOnlyComposable get() = LocalSkinthesiaTypography.current

    val shapes: SkinthesiaShapes
        @Composable @ReadOnlyComposable get() = LocalSkinthesiaShapes.current

    val spacing: SkinthesiaSpacing
        @Composable @ReadOnlyComposable get() = LocalSkinthesiaSpacing.current

    val elevation: SkinthesiaElevation
        @Composable @ReadOnlyComposable get() = LocalSkinthesiaElevation.current
}
