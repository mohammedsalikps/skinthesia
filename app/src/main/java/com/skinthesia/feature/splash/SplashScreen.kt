package com.skinthesia.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.motion.rememberReducedMotion
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

private const val FullDurationMillis = 5500
private const val ReducedMotionDurationMillis = 1400
private const val HoldMillis = 500L

private val EmblemSize = 220.dp

/** Matches Welcome's own logo width so the mark doesn't visibly resize across the Splash → Welcome handoff. */
private val LogoDisplayWidth = 260.dp

/** Fraction of [LogoDisplayWidth]'s natural height occupied by the mark alone, above the wordmark, in [R.drawable.skinthesia_logo_full]. */
private const val MarkHeightFraction = 0.70f

/** height / width of skinthesia_logo_full.png (1106×667) — used to derive its natural display height. */
private const val LogoAspectInverse = 667f / 1106f

/** Positions of the three input "sources" this splash represents: camera, sensor, and personal context. */
private val NodeAngleDegrees = floatArrayOf(-90f, 30f, 150f)

/**
 * The premium ~5–6 second branded opening: three data sources (camera, probe, context) converge
 * into a single ring, which resolves into the real Skinthesia logo. One [Animatable] drives every
 * visual — pure native Compose animation, no animation library, no video file. Reduced-motion users
 * get a short, calm fade straight to the real logo instead of the full convergence choreography.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val colors = SkinthesiaTheme.colors
    val reducedMotion = rememberReducedMotion()
    val progress = remember { Animatable(0f) }

    LaunchedEffect(reducedMotion) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = if (reducedMotion) ReducedMotionDurationMillis else FullDurationMillis,
                easing = FastOutSlowInEasing,
            ),
        )
        delay(HoldMillis)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center,
    ) {
        if (reducedMotion) {
            ReducedMotionLogoReveal(progress = progress.value)
        } else {
            FullConvergenceReveal(progress = progress.value)
        }
    }
}

@Composable
private fun ReducedMotionLogoReveal(progress: Float) {
    Image(
        painter = painterResource(R.drawable.skinthesia_logo_full),
        contentDescription = stringResource(R.string.app_name),
        alpha = progress,
        modifier = Modifier.width(LogoDisplayWidth),
    )
}

@Composable
private fun FullConvergenceReveal(progress: Float) {
    val colors = SkinthesiaTheme.colors
    val nodeColors = remember(colors) { listOf(colors.primary, colors.positive, colors.highlight) }

    // Phase windows, all derived from the single driving `progress` value.
    val spread = 1f - smoothstep(0f, 0.5f, progress)
    val nodesAlpha = 1f - smoothstep(0.4f, 0.62f, progress)
    val ringAlpha = minOf(smoothstep(0.15f, 0.35f, progress), 1f - smoothstep(0.55f, 0.75f, progress))
    val markReveal = smoothstep(0.5f, 0.72f, progress)
    val wordmarkReveal = smoothstep(0.72f, 0.92f, progress)

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.width(EmblemSize).height(EmblemSize)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.minDimension / 2f
            val ringRadius = outerRadius * 0.62f
            val hairline = 1.5.dp.toPx()

            if (ringAlpha > 0f) {
                drawCircle(
                    color = colors.primary.copy(alpha = ringAlpha * 0.8f),
                    radius = ringRadius,
                    center = center,
                    style = Stroke(width = hairline),
                )
            }
            if (nodesAlpha > 0f) {
                val nodeRadius = outerRadius * (0.35f + 0.65f * spread)
                NodeAngleDegrees.forEachIndexed { index, degrees ->
                    val radians = Math.toRadians(degrees.toDouble())
                    val position = Offset(
                        x = center.x + nodeRadius * cos(radians).toFloat(),
                        y = center.y + nodeRadius * sin(radians).toFloat(),
                    )
                    drawLine(
                        color = colors.outlineStrong.copy(alpha = nodesAlpha * 0.35f),
                        start = center,
                        end = position,
                        strokeWidth = hairline,
                        cap = StrokeCap.Round,
                    )
                    drawCircle(color = nodeColors[index].copy(alpha = nodesAlpha), radius = 4.dp.toPx(), center = position)
                }
            }
        }

        if (markReveal > 0f) {
            Box(
                modifier = Modifier
                    .width(LogoDisplayWidth)
                    .height(logoRevealHeight(wordmarkReveal))
                    .clipToBounds(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Image(
                    painter = painterResource(R.drawable.skinthesia_logo_full),
                    contentDescription = stringResource(R.string.app_name),
                    alpha = markReveal,
                    modifier = Modifier
                        .width(LogoDisplayWidth)
                        .graphicsLayer {
                            val scale = 0.85f + 0.15f * markReveal
                            scaleX = scale
                            scaleY = scale
                        },
                )
            }
        }
    }
}

/** The full lockup's natural height at [LogoDisplayWidth], interpolated from mark-only to mark+wordmark as [wordmarkReveal] rises. */
private fun logoRevealHeight(wordmarkReveal: Float): Dp {
    val naturalHeight = LogoDisplayWidth * LogoAspectInverse
    val markOnlyHeight = naturalHeight * MarkHeightFraction
    return markOnlyHeight + (naturalHeight - markOnlyHeight) * wordmarkReveal
}

/** Smooth 0→1 ease between two edges, clamped outside the range. */
private fun smoothstep(edge0: Float, edge1: Float, x: Float): Float {
    val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun SplashScreenMidwayPreview() {
    SkinthesiaTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            FullConvergenceReveal(progress = 0.6f)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun SplashScreenResolvedPreview() {
    SkinthesiaTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            FullConvergenceReveal(progress = 1f)
        }
    }
}
