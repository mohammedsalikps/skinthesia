package com.skinthesia.feature.selfie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object RetakeGuidanceRoute

/** Calm, actionable guidance shown when a photo needs to be retaken. Not a numbered onboarding step. */
@Composable
fun RetakeGuidanceScreen(
    onBack: () -> Unit,
    onRetakePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(
        onBack = onBack,
        modifier = modifier,
        bottomBar = {
            SkinthesiaPrimaryButton(
                text = stringResource(R.string.retake_cta),
                onClick = onRetakePhoto,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = SkinthesiaIcons.Camera,
                contentDescription = null,
                tint = colors.onPrimaryContainer,
                modifier = Modifier.size(spacing.iconLarge),
            )
        }
        Spacer(Modifier.height(spacing.lg))
        Text(
            text = stringResource(R.string.retake_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xl))

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            TipRow(text = stringResource(R.string.retake_tip_face_camera))
            TipRow(text = stringResource(R.string.retake_tip_even_lighting))
            TipRow(text = stringResource(R.string.retake_tip_face_visible))
            TipRow(text = stringResource(R.string.retake_tip_avoid_shadows))
        }
    }
}

@Composable
private fun TipRow(text: String) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(spacing.xs)
                .clip(SkinthesiaTheme.shapes.pill)
                .background(colors.primary),
        )
        Spacer(Modifier.width(spacing.sm))
        Text(text = text, style = SkinthesiaTheme.typography.body, color = colors.textSecondary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun RetakeGuidanceScreenPreview() {
    SkinthesiaTheme {
        RetakeGuidanceScreen(onBack = {}, onRetakePhoto = {})
    }
}
