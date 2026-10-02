package com.skinthesia.feature.probe

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.core.designsystem.motion.rememberReducedMotion
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

private const val PulseRevolutionMillis = 1400

/**
 * An abstract, hand-drawn rendering of the handheld Skinthesia Probe: a slim rounded body with a
 * sensor tip and a status light, no photography involved. While [pulsing] a soft ring radiates
 * from the tip to suggest active scanning; it holds still when the user has reduced motion on.
 */
@Composable
fun ProbeIllustration(
    modifier: Modifier = Modifier,
    tint: Color = SkinthesiaTheme.colors.primary,
    pulsing: Boolean = false,
) {
    val colors = SkinthesiaTheme.colors
    val reducedMotion = rememberReducedMotion()
    val pulse: State<Float>? = if (pulsing && !reducedMotion) {
        rememberInfiniteTransition(label = "probePulse").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(PulseRevolutionMillis, easing = LinearEasing)),
            label = "pulse",
        )
    } else {
        null
    }

    Canvas(modifier = modifier) {
        val bodyWidth = size.width * 0.22f
        val bodyHeight = size.height * 0.58f
        val bodyLeft = (size.width - bodyWidth) / 2f
        val bodyTop = size.height * 0.10f
        val tipRadius = bodyWidth * 0.62f
        val tipCenter = Offset(size.width / 2f, bodyTop + bodyHeight + tipRadius * 0.2f)

        drawCircle(
            color = tint.copy(alpha = 0.06f),
            radius = size.minDimension * 0.42f,
            center = Offset(size.width / 2f, size.height / 2f),
        )

        pulse?.let { state ->
            val progress = state.value
            drawCircle(
                color = colors.positive.copy(alpha = (1f - progress) * 0.35f),
                radius = tipRadius + size.minDimension * 0.28f * progress,
                center = tipCenter,
                style = Stroke(width = 1.5.dp.toPx()),
            )
        }

        drawRoundRect(
            color = colors.surface,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(bodyWidth / 2f),
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(bodyWidth / 2f),
            style = Stroke(width = 1.5.dp.toPx()),
        )

        drawCircle(
            color = colors.positive,
            radius = bodyWidth * 0.14f,
            center = Offset(size.width / 2f, bodyTop + bodyHeight * 0.22f),
        )

        drawCircle(color = tint, radius = tipRadius, center = tipCenter)
        drawCircle(color = colors.surface, radius = tipRadius * 0.45f, center = tipCenter)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun ProbeIllustrationPreview() {
    SkinthesiaTheme {
        ProbeIllustration(
            pulsing = true,
            modifier = Modifier
                .padding(SkinthesiaTheme.spacing.lg)
                .size(220.dp),
        )
    }
}
