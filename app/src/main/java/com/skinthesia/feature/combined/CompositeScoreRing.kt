package com.skinthesia.feature.combined

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

/**
 * A calm circular tracking ring for a 0..100 composite indicator — a quiet health-app-style ring,
 * never a game score or a clinical gauge. A single restrained colour fills the ring; no
 * red/yellow/green grading is implied.
 */
@Composable
fun CompositeScoreRing(score: Int, modifier: Modifier = Modifier) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = size.minDimension * 0.065f
            val ringRadius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(size.width / 2f - ringRadius, size.height / 2f - ringRadius)
            val arcSize = Size(ringRadius * 2f, ringRadius * 2f)

            drawArc(
                color = colors.surfaceMuted,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                color = colors.primary,
                startAngle = -90f,
                sweepAngle = 360f * (score / 100f).coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$score", style = typography.numberDisplay, color = colors.textPrimary)
            Text(text = "/ 100", style = typography.caption, color = colors.textTertiary)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun CompositeScoreRingPreview() {
    SkinthesiaTheme {
        CompositeScoreRing(score = 68, modifier = Modifier.size(180.dp))
    }
}
