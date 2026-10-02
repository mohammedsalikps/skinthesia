package com.skinthesia.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.skinthesia.R

/** Editorial serif for display text, titles, section headings and hero numbers (Playfair Display, SIL OFL). */
val PlayfairDisplay: FontFamily = FontFamily(
    variableFont(R.font.playfair_display, FontWeight.Normal),
    variableFont(R.font.playfair_display, FontWeight.Medium),
    variableFont(R.font.playfair_display, FontWeight.SemiBold),
    variableFont(R.font.playfair_display_italic, FontWeight.Normal, FontStyle.Italic),
    variableFont(R.font.playfair_display_italic, FontWeight.Medium, FontStyle.Italic),
)

/** Neutral sans for body copy, labels, controls and data (Inter, SIL OFL). */
val Inter: FontFamily = FontFamily(
    variableFont(R.font.inter, FontWeight.Normal),
    variableFont(R.font.inter, FontWeight.Medium),
    variableFont(R.font.inter, FontWeight.SemiBold),
)

/**
 * Both families ship as variable fonts; pinning the `wght` axis renders true weights instead of
 * synthetic bold. Compose still marks font variation settings as experimental.
 */
@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: FontWeight, style: FontStyle = FontStyle.Normal): Font = Font(
    resId = resId,
    weight = weight,
    style = style,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private const val LiningFigures = "lnum"
private const val TabularFigures = "tnum"

private fun serif(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
    style: FontStyle = FontStyle.Normal,
    tracking: Float = 0f,
    features: String? = null,
) = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = weight,
    fontStyle = style,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.em,
    fontFeatureSettings = features,
)

private fun sans(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
    tracking: Float = 0f,
    features: String? = null,
) = TextStyle(
    fontFamily = Inter,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.em,
    fontFeatureSettings = features,
)

/**
 * The Skinthesia type scale: an editorial serif for voice and hierarchy, a quiet sans for
 * reading and interface text, and dedicated numeric styles for measurements.
 */
@Immutable
data class SkinthesiaTypography(
    /** Hero statements, e.g. a welcome headline. */
    val displayLarge: TextStyle = serif(size = 40, lineHeight = 46, tracking = -0.01f),
    /** Secondary hero lines and large editorial moments. */
    val displayMedium: TextStyle = serif(size = 32, lineHeight = 38, tracking = -0.005f),
    /** Screen titles. */
    val headline: TextStyle = serif(size = 26, lineHeight = 32),
    /** Card, sheet and dialog titles. */
    val title: TextStyle = serif(size = 20, lineHeight = 26, weight = FontWeight.Medium),
    /** Headings that introduce a group of content within a screen. */
    val sectionHeading: TextStyle = serif(size = 18, lineHeight = 24, weight = FontWeight.Medium),
    /** Quote-style editorial statements, set in italic. */
    val quote: TextStyle = serif(size = 24, lineHeight = 32, style = FontStyle.Italic),

    /** Lead paragraphs and short introductions. */
    val bodyLarge: TextStyle = sans(size = 17, lineHeight = 26),
    /** Default reading text. */
    val body: TextStyle = sans(size = 15, lineHeight = 22),
    /** Dense supporting text. */
    val bodySmall: TextStyle = sans(size = 13, lineHeight = 19),

    /** Emphasised interface labels: list rows, field values, tabs. */
    val label: TextStyle = sans(size = 14, lineHeight = 20, weight = FontWeight.Medium),
    /** Compact labels: chips, tile captions, field labels. */
    val labelSmall: TextStyle = sans(size = 12, lineHeight = 16, weight = FontWeight.Medium),
    /** Helper text, footnotes and timestamps. */
    val caption: TextStyle = sans(size = 12, lineHeight = 16),
    /** Uppercase micro-labels with generous tracking. Callers supply the uppercase text. */
    val overline: TextStyle = sans(size = 11, lineHeight = 14, weight = FontWeight.Medium, tracking = 0.14f),
    /** Button text. */
    val button: TextStyle = sans(size = 15, lineHeight = 20, weight = FontWeight.Medium, tracking = 0.01f),

    /** Hero metrics such as an overall score. */
    val numberDisplay: TextStyle = serif(size = 56, lineHeight = 60, features = LiningFigures),
    /** Prominent metrics inside cards. */
    val numberLarge: TextStyle = serif(size = 30, lineHeight = 34, features = LiningFigures),
    /** Inline values and changes that must line up in columns. */
    val numberMedium: TextStyle = sans(size = 17, lineHeight = 22, weight = FontWeight.Medium, features = TabularFigures),
    /** Units, deltas and compact data. */
    val numberSmall: TextStyle = sans(size = 12, lineHeight = 16, weight = FontWeight.Medium, features = TabularFigures),
)

val LocalSkinthesiaTypography = staticCompositionLocalOf { SkinthesiaTypography() }

/** Bridges the Skinthesia scale into Material 3 so Material components stay on brand. */
fun SkinthesiaTypography.toMaterialTypography(): Typography = Typography(
    displayLarge = displayLarge,
    displayMedium = displayMedium,
    displaySmall = headline,
    headlineLarge = headline,
    headlineMedium = title,
    headlineSmall = sectionHeading,
    titleLarge = title,
    titleMedium = label,
    titleSmall = labelSmall,
    bodyLarge = bodyLarge,
    bodyMedium = body,
    bodySmall = bodySmall,
    labelLarge = button,
    labelMedium = labelSmall,
    labelSmall = caption,
)
