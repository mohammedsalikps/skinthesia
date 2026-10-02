package com.skinthesia.feature.consultation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaCard
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data class ExpertProfileRoute(val expertId: String)

/** A booking-preview state — no real scheduling exists yet, so this is the honest, whole story. */
@Composable
fun ExpertProfileScreen(
    expert: SimulatedExpert,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(onBack = onBack, modifier = modifier, bottomBar = {}) {
        Text(
            text = stringResource(R.string.consultation_generic_name),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(expert.roleRes), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(expert.focusRes), style = typography.bodySmall, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.sm))
        Text(text = stringResource(R.string.consultation_simulated_label), style = typography.caption, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.xl))

        SkinthesiaCard {
            Text(text = stringResource(R.string.consultation_booking_placeholder), style = typography.body, color = colors.textPrimary)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 700)
@Composable
private fun ExpertProfileScreenPreview() {
    SkinthesiaTheme {
        ExpertProfileScreen(expert = SimulatedExperts.first(), onBack = {})
    }
}
