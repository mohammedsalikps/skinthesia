package com.skinthesia.feature.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaPrimaryButton
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

private val LogoMaxWidth = 260.dp
private val LogoMinHeight = 120.dp
private val SupportingTextMaxWidth = 340.dp

/** Below this width the headline steps down one size so each line still fits comfortably. */
private val CompactWidth = 380.dp

private const val RevealMillis = 700
private const val RevealStaggerMillis = 90

/**
 * The first screen: introduces Skinthesia, then offers the way into onboarding and a quieter path
 * for returning users. It fills the screen when everything fits and scrolls when it doesn't (small
 * phones, large font sizes); in that case the logo hero is the first thing to give way.
 */
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        val viewportHeight = maxHeight
        val headlineStyle = if (maxWidth < CompactWidth) typography.headline else typography.displayMedium
        val contentWidth = Modifier
            .widthIn(max = spacing.contentMaxWidth)
            .fillMaxWidth()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = viewportHeight)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Reveal(
                order = 0,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                LogoHero()
            }

            Reveal(order = 1) {
                WelcomeMessage(
                    headlineStyleIsCompact = headlineStyle == typography.headline,
                    modifier = contentWidth,
                )
            }

            Spacer(Modifier.height(spacing.xl))

            Reveal(order = 2) {
                Column(
                    modifier = contentWidth,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SkinthesiaPrimaryButton(
                        text = stringResource(R.string.welcome_get_started),
                        onClick = onGetStarted,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(spacing.xs))
                    SignInPrompt(onSignIn = onSignIn)
                }
            }
        }
    }
}

/**
 * The real Skinthesia mark and wordmark, the app's hero brand identity here — sized to the widest
 * comfortable measure that fits the space left over, or hidden entirely on very short viewports
 * rather than cramped.
 */
@Composable
private fun LogoHero() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = SkinthesiaTheme.spacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        val width = minOf(maxWidth, LogoMaxWidth)
        if (maxHeight >= LogoMinHeight) {
            Image(
                painter = painterResource(R.drawable.skinthesia_logo_full),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.widthIn(max = width),
            )
        }
    }
}

@Composable
private fun WelcomeMessage(
    headlineStyleIsCompact: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val understand = stringResource(R.string.welcome_headline_understand)
    val transform = stringResource(R.string.welcome_headline_transform)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = buildAnnotatedString {
                append(understand)
                append('\n')
                withStyle(SpanStyle(color = colors.primary, fontStyle = FontStyle.Italic)) {
                    append(transform)
                }
            },
            style = if (headlineStyleIsCompact) typography.headline else typography.displayMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(SkinthesiaTheme.spacing.md))
        Text(
            text = stringResource(R.string.welcome_supporting),
            style = typography.body,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = SupportingTextMaxWidth),
        )
    }
}

/** "Already have an account? Sign in" as one comfortably sized touch target. */
@Composable
private fun SignInPrompt(onSignIn: () -> Unit) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    val prompt = stringResource(R.string.welcome_sign_in_prompt)
    val action = stringResource(R.string.welcome_sign_in)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressedFill by animateColorAsState(
        targetValue = if (pressed) colors.primaryContainer else Color.Transparent,
        label = "signInPressedFill",
    )

    Box(
        modifier = Modifier
            .heightIn(min = spacing.touchTarget)
            .clip(SkinthesiaTheme.shapes.button)
            .background(pressedFill)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onSignIn,
            )
            .padding(horizontal = spacing.md, vertical = spacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = buildAnnotatedString {
                append(prompt)
                append(' ')
                withStyle(SpanStyle(color = colors.primary, fontWeight = FontWeight.Medium)) {
                    append(action)
                }
            },
            style = SkinthesiaTheme.typography.body,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

/** Calm entrance: content fades in and settles upwards, cascading by [order]. Static in previews. */
@Composable
private fun Reveal(
    order: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier) { content() }
        return
    }
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
    val delay = order * RevealStaggerMillis
    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = fadeIn(tween(RevealMillis, delay, LinearOutSlowInEasing)) +
            slideInVertically(tween(RevealMillis, delay, LinearOutSlowInEasing)) { fullHeight -> fullHeight / 12 },
    ) {
        content()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA, widthDp = 411, heightDp = 914)
@Composable
private fun WelcomeScreenPreview() {
    SkinthesiaTheme {
        WelcomeScreen(onGetStarted = {}, onSignIn = {})
    }
}

@Preview(
    name = "Small phone, large text",
    showBackground = true,
    backgroundColor = 0xFFF7F1EA,
    widthDp = 360,
    heightDp = 640,
    fontScale = 1.3f,
)
@Composable
private fun WelcomeScreenCompactPreview() {
    SkinthesiaTheme {
        WelcomeScreen(onGetStarted = {}, onSignIn = {})
    }
}
