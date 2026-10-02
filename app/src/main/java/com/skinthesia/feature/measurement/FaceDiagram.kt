package com.skinthesia.feature.measurement

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.core.designsystem.motion.rememberReducedMotion
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.domain.model.FaceRegion

private const val PulseRevolutionMillis = 1100

/**
 * A calm, abstract face outline used to orient the person during probe measurement — no
 * photography, no clinical detail. Drawn as if looking at the face head-on rather than mirrored,
 * so the person's own left cheek renders on the right side of the diagram, matching the usual
 * anatomical-diagram convention. Only [activeRegion] is highlighted; [completedRegions] show a
 * small checkmark instead.
 */
@Composable
fun FaceDiagram(
    activeRegion: FaceRegion?,
    completedRegions: Set<FaceRegion>,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val reducedMotion = rememberReducedMotion()
    val pulse: State<Float>? = if (activeRegion != null && !reducedMotion) {
        rememberInfiniteTransition(label = "measurementPulse").animateFloat(
            initialValue = 0.55f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(PulseRevolutionMillis), repeatMode = RepeatMode.Reverse),
            label = "zoneAlpha",
        )
    } else {
        null
    }

    Canvas(modifier = modifier) {
        val faceWidth = size.width * 0.62f
        val faceHeight = size.height * 0.82f
        val faceLeft = (size.width - faceWidth) / 2f
        val faceTop = (size.height - faceHeight) / 2f
        val dashedOutline = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))

        drawOval(color = colors.surface, topLeft = Offset(faceLeft, faceTop), size = Size(faceWidth, faceHeight))
        drawOval(
            color = colors.outlineStrong.copy(alpha = 0.5f),
            topLeft = Offset(faceLeft, faceTop),
            size = Size(faceWidth, faceHeight),
            style = Stroke(width = 1.5.dp.toPx()),
        )

        // Simple, abstract features: just enough to read as a face, never photographic or clinical.
        val eyeY = faceTop + faceHeight * 0.42f
        val eyeSize = Size(faceWidth * 0.10f, faceHeight * 0.045f)
        listOf(faceLeft + faceWidth * 0.24f, faceLeft + faceWidth * 0.66f).forEach { eyeX ->
            drawOval(color = colors.outlineStrong.copy(alpha = 0.6f), topLeft = Offset(eyeX, eyeY), size = eyeSize)
        }
        drawLine(
            color = colors.outlineStrong.copy(alpha = 0.4f),
            start = Offset(size.width / 2f, faceTop + faceHeight * 0.48f),
            end = Offset(size.width / 2f, faceTop + faceHeight * 0.58f),
            strokeWidth = 1.5.dp.toPx(),
        )
        drawLine(
            color = colors.outlineStrong.copy(alpha = 0.4f),
            start = Offset(faceLeft + faceWidth * 0.40f, faceTop + faceHeight * 0.70f),
            end = Offset(faceLeft + faceWidth * 0.60f, faceTop + faceHeight * 0.70f),
            strokeWidth = 1.5.dp.toPx(),
        )

        FaceRegion.entries.forEach { region ->
            val (zoneTopLeft, zoneSize) = regionZone(region, faceLeft, faceTop, faceWidth, faceHeight)
            val zoneCenter = Offset(zoneTopLeft.x + zoneSize.width / 2f, zoneTopLeft.y + zoneSize.height / 2f)
            when {
                region == activeRegion -> {
                    drawOval(color = colors.primaryContainer.copy(alpha = pulse?.value ?: 1f), topLeft = zoneTopLeft, size = zoneSize)
                    drawOval(color = colors.primary, topLeft = zoneTopLeft, size = zoneSize, style = Stroke(width = 1.5.dp.toPx()))
                }
                region in completedRegions -> {
                    drawOval(color = colors.outline, topLeft = zoneTopLeft, size = zoneSize, style = dashedOutline)
                    drawCircle(color = colors.positive, radius = 9.dp.toPx(), center = zoneCenter)
                    drawCircle(color = colors.surface, radius = 4.dp.toPx(), center = zoneCenter)
                }
                else -> drawOval(color = colors.outline, topLeft = zoneTopLeft, size = zoneSize, style = dashedOutline)
            }
        }
    }
}

/** The on-screen bounds of one region's highlight zone, expressed as (topLeft, size). */
private fun regionZone(region: FaceRegion, faceLeft: Float, faceTop: Float, faceWidth: Float, faceHeight: Float): Pair<Offset, Size> = when (region) {
    FaceRegion.FOREHEAD -> Offset(faceLeft + faceWidth * 0.15f, faceTop + faceHeight * 0.08f) to Size(faceWidth * 0.70f, faceHeight * 0.20f)
    // The person's own left cheek renders on the right of a face viewed head-on.
    FaceRegion.LEFT_CHEEK -> Offset(faceLeft + faceWidth * 0.52f, faceTop + faceHeight * 0.50f) to Size(faceWidth * 0.36f, faceHeight * 0.28f)
    FaceRegion.RIGHT_CHEEK -> Offset(faceLeft + faceWidth * 0.12f, faceTop + faceHeight * 0.50f) to Size(faceWidth * 0.36f, faceHeight * 0.28f)
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun FaceDiagramPreview() {
    SkinthesiaTheme {
        FaceDiagram(
            activeRegion = FaceRegion.LEFT_CHEEK,
            completedRegions = setOf(FaceRegion.FOREHEAD),
            modifier = Modifier
                .padding(SkinthesiaTheme.spacing.lg)
                .size(220.dp),
        )
    }
}
