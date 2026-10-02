package com.skinthesia.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing and sizing on a 4 dp grid. Layouts use these tokens instead of literal dp values
 * so rhythm stays consistent across every screen.
 */
@Immutable
data class SkinthesiaSpacing(
    // Scale
    val none: Dp = 0.dp,
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
    val xxxl: Dp = 64.dp,

    // Layout
    /** Horizontal page margin. */
    val screenHorizontal: Dp = 24.dp,
    /** Gap between major sections of a screen. */
    val sectionGap: Dp = 32.dp,
    /** Inner padding of cards. */
    val cardPadding: Dp = 20.dp,
    /** Gap between stacked items such as cards or list rows. */
    val itemGap: Dp = 12.dp,
    /** Widest a screen's content column grows, so tablets and foldables keep a readable measure. */
    val contentMaxWidth: Dp = 560.dp,

    // Sizes
    /** Minimum size of any interactive element. */
    val touchTarget: Dp = 48.dp,
    val buttonHeight: Dp = 52.dp,
    val fieldHeight: Dp = 52.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 20.dp,
    val iconLarge: Dp = 24.dp,
    /** Hairline borders and dividers. */
    val hairline: Dp = 1.dp,
)

val LocalSkinthesiaSpacing = staticCompositionLocalOf { SkinthesiaSpacing() }
