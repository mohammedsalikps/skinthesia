package com.skinthesia.feature.consultation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.feature.onboarding.OnboardingScaffold
import kotlinx.serialization.Serializable

@Serializable
data object ConsultationRoute

/** A discovery screen for simulated expert profiles — clearly labeled, never presented as real credentialed guidance. */
@Composable
fun ConsultationScreen(
    experts: List<SimulatedExpert>,
    onViewProfile: (SimulatedExpert) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    OnboardingScaffold(onBack = onBack, modifier = modifier, bottomBar = {}) {
        Text(
            text = stringResource(R.string.consultation_title),
            style = typography.headline,
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.consultation_subtitle), style = typography.body, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.sm))
        Text(text = stringResource(R.string.consultation_simulated_label), style = typography.caption, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.xl))

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            experts.forEach { expert -> ExpertCard(expert = expert, onViewProfile = { onViewProfile(expert) }) }
        }
    }
}

@Composable
private fun ExpertCard(expert: SimulatedExpert, onViewProfile: () -> Unit) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    SkinthesiaCard {
        Text(text = stringResource(R.string.consultation_generic_name), style = typography.title, color = colors.textPrimary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(expert.roleRes), style = typography.label, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xxs))
        Text(text = stringResource(expert.focusRes), style = typography.bodySmall, color = colors.textSecondary)
        Spacer(Modifier.height(spacing.xs))
        Text(text = stringResource(R.string.consultation_availability_placeholder), style = typography.caption, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.md))
        SkinthesiaSecondaryButton(text = stringResource(R.string.consultation_view_profile_cta), onClick = onViewProfile, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 1400)
@Composable
private fun ConsultationScreenPreview() {
    SkinthesiaTheme {
        ConsultationScreen(experts = SimulatedExperts, onViewProfile = {}, onBack = {})
    }
}
