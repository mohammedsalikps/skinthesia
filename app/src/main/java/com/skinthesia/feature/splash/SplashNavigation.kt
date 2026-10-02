package com.skinthesia.feature.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

/** Registers the branded opening. The caller decides where it leads once the animation finishes. */
fun NavGraphBuilder.splashScreen(onFinished: () -> Unit) {
    composable<SplashRoute> {
        SplashScreen(onFinished = onFinished)
    }
}
