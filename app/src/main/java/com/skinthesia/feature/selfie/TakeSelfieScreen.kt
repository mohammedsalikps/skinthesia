package com.skinthesia.feature.selfie

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object TakeSelfieRoute

private const val TotalSteps = 7
private const val Step = 2

/**
 * The second onboarding step: explains why a selfie is needed, then a large camera-oriented area
 * stands in for the viewfinder. There is no real camera yet; capture is simulated so the state and
 * navigation are already correct for CameraX to be dropped in later.
 */
@Composable
fun TakeSelfieScreen(
    hasPhoto: Boolean,
    onCapture: () -> Unit,
    onRetake: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(
        onBack = onBack,
        currentStep = Step,
        totalSteps = TotalSteps,
        modifier = modifier,
        bottomBar = {
            if (hasPhoto) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    SkinthesiaSecondaryButton(
                        text = stringResource(R.string.selfie_retake),
                        onClick = onRetake,
                        modifier = Modifier.weight(1f),
                    )
                    SkinthesiaPrimaryButton(
                        text = stringResource(R.string.action_continue),
                        onClick = onContinue,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                SkinthesiaPrimaryButton(
                    text = stringResource(R.string.selfie_take),
                    onClick = onCapture,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) {
        Text(
            text = stringResource(R.string.selfie_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = stringResource(R.string.selfie_subtitle),
            style = typography.body,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.xl))

        Viewfinder(hasPhoto = hasPhoto, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Viewfinder(hasPhoto: Boolean, modifier: Modifier = Modifier) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val shape = SkinthesiaTheme.shapes.card

    Box(
        modifier = modifier
            .aspectRatio(0.82f)
            .clip(shape)
            .background(if (hasPhoto) colors.primaryContainer else colors.surfaceMuted)
            .border(spacing.hairline, colors.outline, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (!hasPhoto) {
            FaceGuideOutline(modifier = Modifier.fillMaxSize())
        }
        Crossfade(targetState = hasPhoto, animationSpec = tween(320), label = "viewfinderState") { captured ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = if (captured) SkinthesiaIcons.Check else SkinthesiaIcons.Camera,
                    contentDescription = null,
                    tint = if (captured) colors.primary else colors.textTertiary,
                    modifier = Modifier.size(spacing.iconLarge + spacing.xs),
                )
                Spacer(Modifier.height(spacing.sm))
                Text(
                    text = stringResource(if (captured) R.string.selfie_captured else R.string.selfie_align_hint),
                    style = typography.label,
                    color = if (captured) colors.onPrimaryContainer else colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = spacing.xl),
                )
            }
        }
    }
}

/** A dashed oval guide, drawn rather than photographed, hinting at where a face should sit. */
@Composable
private fun FaceGuideOutline(modifier: Modifier = Modifier) {
    val guideColor = SkinthesiaTheme.colors.outlineStrong.copy(alpha = 0.45f)
    Canvas(modifier = modifier) {
        val ovalWidth = size.width * 0.55f
        val ovalHeight = size.height * 0.5f
        drawOval(
            color = guideColor,
            topLeft = Offset((size.width - ovalWidth) / 2f, size.height * 0.5f - ovalHeight / 2f),
            size = Size(ovalWidth, ovalHeight),
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun TakeSelfieScreenBeforeCapturePreview() {
    SkinthesiaTheme {
        TakeSelfieScreen(hasPhoto = false, onCapture = {}, onRetake = {}, onBack = {}, onContinue = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun TakeSelfieScreenAfterCapturePreview() {
    SkinthesiaTheme {
        TakeSelfieScreen(hasPhoto = true, onCapture = {}, onRetake = {}, onBack = {}, onContinue = {})
    }
}
