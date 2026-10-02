package com.skinthesia.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

private val ProgressBarHeight = 6.dp
private const val ProgressAnimationMillis = 700

/** Indeterminate spinner for work of unknown length: a thin, rounded clay stroke on a faint track. */
@Composable
fun SkinthesiaLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = SkinthesiaTheme.spacing.lg,
    color: Color = SkinthesiaTheme.colors.primary,
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = size / 12,
        trackColor = color.copy(alpha = 0.14f),
        strokeCap = StrokeCap.Round,
    )
}

/**
 * Determinate progress from 0 to 1 as a slim rounded bar. Changes glide to the new value. It
 * fills the width it is given and fills from the start edge in right-to-left layouts too.
 */
@Composable
fun SkinthesiaProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = SkinthesiaTheme.colors.primary,
    trackColor: Color = SkinthesiaTheme.colors.surfaceMuted,
) {
    val fraction = progress.coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(ProgressAnimationMillis, easing = FastOutSlowInEasing),
        label = "progressBar",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ProgressBarHeight)
            .progressSemantics(fraction)
            .clip(SkinthesiaTheme.shapes.pill)
            .background(trackColor)
            .drawBehind {
                val width = size.width * animatedFraction
                val start = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                drawRoundRect(
                    color = color,
                    topLeft = Offset(start, 0f),
                    size = Size(width, size.height),
                    cornerRadius = CornerRadius(size.height / 2f),
                )
            },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun ProgressIndicatorsPreview() {
    SkinthesiaTheme {
        Column(
            modifier = Modifier.padding(SkinthesiaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(SkinthesiaTheme.spacing.md),
        ) {
            SkinthesiaLoadingIndicator()
            SkinthesiaProgressBar(progress = 0.25f)
            SkinthesiaProgressBar(progress = 0.7f, color = SkinthesiaTheme.colors.positive)
        }
    }
}
