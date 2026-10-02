package com.skinthesia.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Raw Skinthesia palette: warm neutrals with restrained clay, sage and rose accents.
 * Components and features use the semantic roles in [SkinthesiaColors], not these values.
 */
object SkinthesiaPalette {
    // Neutrals
    val Cream = Color(0xFFF7F1EA)
    val Porcelain = Color(0xFFFFFCF8)
    val White = Color(0xFFFFFFFF)
    val Sand = Color(0xFFEFE6DC)
    val Linen = Color(0xFFE8DDD2)
    val Stone = Color(0xFF978577)

    // Ink
    val Espresso = Color(0xFF2E231E)
    val Cocoa = Color(0xFF5E4E45)
    val Taupe = Color(0xFF716258)

    // Clay: primary actions
    val Clay = Color(0xFF8A5A45)
    val ClayDeep = Color(0xFF704634)
    val ClayMist = Color(0xFFF4E4DA)

    // Sage: progress and positive change
    val Sage = Color(0xFF4F6B57)
    val SageDeep = Color(0xFF46644E)
    val SageMist = Color(0xFFE4ECE3)

    // Rose: gentle highlights and attention
    val Rose = Color(0xFFD39A88)
    val RoseDeep = Color(0xFF9A5546)
    val RoseMist = Color(0xFFF8E3DA)

    // Brick: errors
    val Brick = Color(0xFFA23F35)
    val BrickMist = Color(0xFFF7DFDA)

    // Utility
    val Mist = Color(0xFFE9E0D7)
    val MistInk = Color(0xFFA2948A)
    val Umber = Color(0xFF5B3A2A)
    val Night = Color(0xFF1F1512)
}

/**
 * Semantic color roles. Every text role meets WCAG AA (4.5:1) on [background], [surface] and
 * [surfaceMuted]; [outlineStrong] meets the 3:1 non-text minimum for input boundaries.
 */
@Immutable
data class SkinthesiaColors(
    /** The page. */
    val background: Color,
    /** Cards, sheets and fields. */
    val surface: Color,
    /** Tonal fills: grouped content, tracks, pressed states. */
    val surfaceMuted: Color,
    /** Decorative hairlines and dividers. */
    val outline: Color,
    /** Boundaries that identify a control, such as text fields and secondary buttons. */
    val outlineStrong: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    /** Least emphasis: captions, placeholders, metadata. */
    val textTertiary: Color,
    /** Text and icons on [primary] and the other strong accents. */
    val textOnPrimary: Color,

    /** Primary actions. */
    val primary: Color,
    val primaryPressed: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,

    /** Progress, improvement and success. Safe for text on the page. */
    val positive: Color,
    val positiveContainer: Color,
    val onPositiveContainer: Color,

    /** Gentle highlights and points of attention. Safe for text on the page. */
    val highlight: Color,
    val highlightContainer: Color,
    val onHighlightContainer: Color,

    val error: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,

    val disabledContainer: Color,
    val disabledContent: Color,
    /** Dims content behind sheets and dialogs. */
    val scrim: Color,
    /** Warm tint for shadows. */
    val shadow: Color,
)

val SkinthesiaLightColors = SkinthesiaColors(
    background = SkinthesiaPalette.Cream,
    surface = SkinthesiaPalette.Porcelain,
    surfaceMuted = SkinthesiaPalette.Sand,
    outline = SkinthesiaPalette.Linen,
    outlineStrong = SkinthesiaPalette.Stone,
    textPrimary = SkinthesiaPalette.Espresso,
    textSecondary = SkinthesiaPalette.Cocoa,
    textTertiary = SkinthesiaPalette.Taupe,
    textOnPrimary = SkinthesiaPalette.Porcelain,
    primary = SkinthesiaPalette.Clay,
    primaryPressed = SkinthesiaPalette.ClayDeep,
    primaryContainer = SkinthesiaPalette.ClayMist,
    onPrimaryContainer = SkinthesiaPalette.ClayDeep,
    positive = SkinthesiaPalette.Sage,
    positiveContainer = SkinthesiaPalette.SageMist,
    onPositiveContainer = SkinthesiaPalette.SageDeep,
    highlight = SkinthesiaPalette.RoseDeep,
    highlightContainer = SkinthesiaPalette.RoseMist,
    onHighlightContainer = SkinthesiaPalette.RoseDeep,
    error = SkinthesiaPalette.Brick,
    errorContainer = SkinthesiaPalette.BrickMist,
    onErrorContainer = SkinthesiaPalette.Brick,
    disabledContainer = SkinthesiaPalette.Mist,
    disabledContent = SkinthesiaPalette.MistInk,
    scrim = SkinthesiaPalette.Night.copy(alpha = 0.32f),
    shadow = SkinthesiaPalette.Umber,
)

val LocalSkinthesiaColors = staticCompositionLocalOf { SkinthesiaLightColors }

/**
 * Bridges the Skinthesia roles into a Material 3 [ColorScheme] so Material components inherit the
 * palette. Surface tint is neutralised so elevated Material surfaces never pick up a colour cast.
 */
fun SkinthesiaColors.toMaterialColorScheme(): ColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = textOnPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    inversePrimary = primaryContainer,
    secondary = positive,
    onSecondary = textOnPrimary,
    secondaryContainer = positiveContainer,
    onSecondaryContainer = onPositiveContainer,
    tertiary = highlight,
    onTertiary = textOnPrimary,
    tertiaryContainer = highlightContainer,
    onTertiaryContainer = onHighlightContainer,
    background = background,
    onBackground = textPrimary,
    surface = surface,
    onSurface = textPrimary,
    surfaceVariant = surfaceMuted,
    onSurfaceVariant = textSecondary,
    surfaceTint = surface,
    inverseSurface = textPrimary,
    inverseOnSurface = surface,
    error = error,
    onError = textOnPrimary,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    outline = outlineStrong,
    outlineVariant = outline,
    scrim = scrim,
    surfaceBright = surface,
    surfaceDim = surfaceMuted,
    surfaceContainerLowest = surface,
    surfaceContainerLow = surface,
    surfaceContainer = background,
    surfaceContainerHigh = surfaceMuted,
    surfaceContainerHighest = surfaceMuted,
)
