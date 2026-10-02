package com.skinthesia.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.skinthesia.R
import com.skinthesia.core.designsystem.component.SkinthesiaProgressBar
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

/**
 * The shared frame for every onboarding step: an optional back arrow and step progress, a
 * scrolling content column bounded to a comfortable reading width, and a pinned call-to-action
 * area that respects the keyboard and the navigation bar.
 */
@Composable
fun OnboardingScaffold(
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    currentStep: Int? = null,
    totalSteps: Int? = null,
    bottomBar: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        OnboardingTopBar(onBack = onBack, currentStep = currentStep, totalSteps = totalSteps)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = spacing.contentMaxWidth)
                    .fillMaxWidth(),
                content = content,
            )
            Spacer(Modifier.height(spacing.lg))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = spacing.contentMaxWidth)
                    .fillMaxWidth(),
                content = bottomBar,
            )
        }
    }
}

@Composable
private fun OnboardingTopBar(
    onBack: (() -> Unit)?,
    currentStep: Int?,
    totalSteps: Int?,
) {
    val spacing = SkinthesiaTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = spacing.xs),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(spacing.touchTarget)) {
            if (onBack != null) {
                OnboardingBackButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart))
            }
        }
        if (currentStep != null && totalSteps != null) {
            Spacer(Modifier.height(spacing.xs))
            SkinthesiaProgressBar(
                progress = currentStep.toFloat() / totalSteps,
                modifier = Modifier.padding(horizontal = spacing.sm),
            )
            Spacer(Modifier.height(spacing.md))
        } else {
            Spacer(Modifier.height(spacing.sm))
        }
    }
}

@Composable
private fun OnboardingBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SkinthesiaTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(SkinthesiaTheme.spacing.touchTarget)
            .clip(SkinthesiaTheme.shapes.pill)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = SkinthesiaIcons.ArrowBack,
            contentDescription = stringResource(R.string.action_back),
            tint = colors.textPrimary,
            modifier = Modifier.size(SkinthesiaTheme.spacing.iconMedium),
        )
    }
}
