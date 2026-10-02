package com.skinthesia.feature.combined

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.core.designsystem.motion.rememberReducedMotion
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

private const val PulseRevolutionMillis = 1300

/**
 * Four source nodes (camera, probe, profile, goals) converging on a centre point — the product's
 * "camera + probe + you -> AI" idea, without any robot or neon imagery. Each node lights up as
 * [sourcesReady] increases; the centre pulses gently while [synthesizing].
 */
@Composable
fun ConvergenceIllustration(
    sourcesReady: Int,
    synthesizing: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val reducedMotion = rememberReducedMotion()
    val pulse: State<Float>? = if (synthesizing && !reducedMotion) {
        rememberInfiniteTransition(label = "convergencePulse").animateFloat(
            initialValue = 0.7f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(PulseRevolutionMillis), repeatMode = RepeatMode.Reverse),
            label = "centerScale",
        )
    } else {
        null
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val nodeRadius = size.minDimension * 0.055f
        val orbitRadius = size.minDimension * 0.36f
        val centerRadius = size.minDimension * 0.09f * (pulse?.value ?: 1f)

        val nodeOffsets = listOf(
            Offset(center.x, center.y - orbitRadius),
            Offset(center.x + orbitRadius, center.y),
            Offset(center.x, center.y + orbitRadius),
            Offset(center.x - orbitRadius, center.y),
        )

        nodeOffsets.forEachIndexed { index, offset ->
            val ready = index < sourcesReady
            drawLine(
                color = if (ready) colors.primary.copy(alpha = 0.5f) else colors.outline,
                start = offset,
                end = center,
                strokeWidth = 1.dp.toPx(),
            )
            drawCircle(color = if (ready) colors.primaryContainer else colors.surface, radius = nodeRadius, center = offset)
            drawCircle(
                color = if (ready) colors.primary else colors.outlineStrong,
                radius = nodeRadius,
                center = offset,
                style = Stroke(width = 1.5.dp.toPx()),
            )
        }

        drawCircle(color = colors.primaryContainer.copy(alpha = 0.5f), radius = centerRadius * 1.6f, center = center)
        drawCircle(color = colors.primary, radius = centerRadius, center = center)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun ConvergenceIllustrationPreview() {
    SkinthesiaTheme {
        ConvergenceIllustration(
            sourcesReady = 3,
            synthesizing = true,
            modifier = Modifier
                .padding(SkinthesiaTheme.spacing.lg)
                .size(220.dp),
        )
    }
}
