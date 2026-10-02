package com.skinthesia.feature.welcome

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeRoute

/** Registers the Welcome destination. The caller decides where each action leads. */
fun NavGraphBuilder.welcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
) {
    composable<WelcomeRoute> {
        WelcomeScreen(onGetStarted = onGetStarted, onSignIn = onSignIn)
    }
}
