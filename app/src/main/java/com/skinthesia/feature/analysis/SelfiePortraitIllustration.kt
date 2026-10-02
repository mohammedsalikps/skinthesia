package com.skinthesia.feature.analysis

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.core.designsystem.motion.rememberReducedMotion
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

private const val ScanRevolutionMillis = 2200

/**
 * An abstract, anonymous headshot silhouette standing in for the selfie captured earlier in
 * onboarding — never a real or stock photograph. While [scanning] a thin line sweeps the frame to
 * suggest the (simulated) analysis examining the image; it holds still when reduced motion is on.
 */
@Composable
fun SelfiePortraitIllustration(
    modifier: Modifier = Modifier,
    scanning: Boolean = false,
) {
    val colors = SkinthesiaTheme.colors
    val reducedMotion = rememberReducedMotion()
    val scanPosition: State<Float>? = if (scanning && !reducedMotion) {
        rememberInfiniteTransition(label = "selfieScan").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(ScanRevolutionMillis, easing = LinearEasing)),
            label = "scanPosition",
        )
    } else {
        null
    }

    Canvas(modifier = modifier) {
        val cardRadius = CornerRadius(20.dp.toPx())
        drawRoundRect(color = colors.surfaceMuted, cornerRadius = cardRadius)
        drawRoundRect(color = colors.outline, cornerRadius = cardRadius, style = Stroke(width = 1.dp.toPx()))

        val headWidth = size.width * 0.40f
        val headHeight = size.height * 0.34f
        val headCenter = Offset(size.width / 2f, size.height * 0.36f)
        val shoulderTop = headCenter.y + headHeight * 0.42f

        val silhouette = Path().apply {
            addOval(
                Rect(
                    left = headCenter.x - headWidth / 2f,
                    top = headCenter.y - headHeight / 2f,
                    right = headCenter.x + headWidth / 2f,
                    bottom = headCenter.y + headHeight / 2f,
                ),
            )
            moveTo(size.width * 0.22f, size.height)
            quadraticTo(size.width * 0.22f, shoulderTop, size.width * 0.5f, shoulderTop - size.height * 0.03f)
            quadraticTo(size.width * 0.78f, shoulderTop, size.width * 0.78f, size.height)
            close()
        }
        drawPath(path = silhouette, color = colors.outline.copy(alpha = 0.55f))

        scanPosition?.let { state ->
            val y = size.height * state.value
            drawLine(
                color = colors.primary.copy(alpha = 0.6f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5.dp.toPx(),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun SelfiePortraitIllustrationPreview() {
    SkinthesiaTheme {
        SelfiePortraitIllustration(
            scanning = true,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f),
        )
    }
}
