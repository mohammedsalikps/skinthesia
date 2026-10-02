package com.skinthesia.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.skinthesia.core.designsystem.component.SkinthesiaSecondaryButton
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import kotlinx.serialization.Serializable

/*
 * TEMPORARY. Stand-ins for screens that later steps will build, so navigation from the current
 * flow can be exercised end to end. When a real feature lands, delete its route and entry here and
 * point the caller at the real destination. Text is inline on purpose: none of it ships.
 */

/** TEMPORARY: sign-in for returning users. */
@Serializable
data object TemporarySignInRoute

internal fun NavGraphBuilder.temporaryDestinations(onBack: () -> Unit) {
    composable<TemporarySignInRoute> {
        TemporaryDestination(title = "Sign in", onBack = onBack)
    }
}

@Composable
private fun TemporaryDestination(title: String, onBack: (() -> Unit)?) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = spacing.screenHorizontal),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "TEMPORARY PLACEHOLDER", style = typography.overline, color = colors.textTertiary)
        Spacer(Modifier.height(spacing.xs))
        Text(text = title, style = typography.headline, color = colors.textPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(spacing.sm))
        Text(
            text = "This screen is built in a later step.",
            style = typography.body,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        if (onBack != null) {
            Spacer(Modifier.height(spacing.lg))
            SkinthesiaSecondaryButton(text = "Back", onClick = onBack)
        }
    }
}
