package com.skinthesia.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.R
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

/**
 * The main call to action: a warm clay pill with light text and, by default, a thin forward arrow.
 * While [loading] the button keeps its size, shows a spinner and ignores taps.
 */
@Composable
fun SkinthesiaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = SkinthesiaIcons.ArrowForward,
) {
    val colors = SkinthesiaTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val containerColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledContainer
            pressed -> colors.primaryPressed
            else -> colors.primary
        },
        animationSpec = tween(PressDurationMillis),
        label = "primaryButtonContainer",
    )
    val contentColor = if (enabled) colors.textOnPrimary else colors.disabledContent
    val loadingDescription = stringResource(R.string.designsystem_loading)

    ButtonSurface(
        onClick = onClick,
        enabled = enabled && !loading,
        containerColor = containerColor,
        border = null,
        interactionSource = interactionSource,
        modifier = if (loading) modifier.semantics { stateDescription = loadingDescription } else modifier,
    ) {
        ButtonContent(
            text = text,
            contentColor = contentColor,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            modifier = Modifier.alpha(if (loading) 0f else 1f),
        )
        if (loading) {
            SkinthesiaLoadingIndicator(
                size = SkinthesiaTheme.spacing.iconMedium,
                color = contentColor,
            )
        }
    }
}

/** A quieter alternative action: light surface, hairline outline and dark text. */
@Composable
fun SkinthesiaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    val colors = SkinthesiaTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val containerColor by animateColorAsState(
        targetValue = if (pressed && enabled) colors.surfaceMuted else colors.surface,
        animationSpec = tween(PressDurationMillis),
        label = "secondaryButtonContainer",
    )

    ButtonSurface(
        onClick = onClick,
        enabled = enabled,
        containerColor = containerColor,
        border = BorderStroke(
            width = SkinthesiaTheme.spacing.hairline,
            color = if (enabled) colors.outlineStrong else colors.outline,
        ),
        interactionSource = interactionSource,
        modifier = modifier,
    ) {
        ButtonContent(
            text = text,
            contentColor = if (enabled) colors.textPrimary else colors.disabledContent,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
    }
}

/** An inline, low-emphasis action such as "Skip for now" or "View all". */
@Composable
fun SkinthesiaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = SkinthesiaTheme.colors.primary,
    trailingIcon: ImageVector? = null,
) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressedFill by animateColorAsState(
        targetValue = if (pressed && enabled) colors.primaryContainer else Color.Transparent,
        animationSpec = tween(PressDurationMillis),
        label = "textButtonFill",
    )
    val contentColor = if (enabled) color else colors.disabledContent

    Row(
        modifier = modifier
            .heightIn(min = spacing.touchTarget)
            .clip(SkinthesiaTheme.shapes.button)
            .background(pressedFill)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = spacing.sm),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = SkinthesiaTheme.typography.label,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (trailingIcon != null) {
            Spacer(Modifier.width(spacing.xxs))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(spacing.iconSmall),
            )
        }
    }
}

/** Shared pill frame for filled and outlined buttons: size, shape, press feedback and click handling. */
@Composable
private fun ButtonSurface(
    onClick: () -> Unit,
    enabled: Boolean,
    containerColor: Color,
    border: BorderStroke?,
    interactionSource: MutableInteractionSource,
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = SkinthesiaTheme.shapes.button
    val spacing = SkinthesiaTheme.spacing
    val scale by animatePressScale(interactionSource)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .heightIn(min = spacing.buttonHeight)
            .clip(shape)
            .background(containerColor)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = spacing.lg),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
private fun ButtonContent(
    text: String,
    contentColor: Color,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    modifier: Modifier = Modifier,
) {
    val spacing = SkinthesiaTheme.spacing
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(spacing.iconMedium),
            )
            Spacer(Modifier.width(spacing.xs))
        }
        Text(
            text = text,
            style = SkinthesiaTheme.typography.button,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (trailingIcon != null) {
            Spacer(Modifier.width(spacing.xs))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(spacing.iconMedium),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun ButtonsPreview() {
    SkinthesiaTheme {
        Column(
            modifier = Modifier.padding(SkinthesiaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(SkinthesiaTheme.spacing.sm),
        ) {
            SkinthesiaPrimaryButton(text = "Continue", onClick = {}, modifier = Modifier.fillMaxWidth())
            SkinthesiaPrimaryButton(text = "Continue", onClick = {}, loading = true, modifier = Modifier.fillMaxWidth())
            SkinthesiaPrimaryButton(text = "Continue", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
            SkinthesiaSecondaryButton(text = "Maybe later", onClick = {}, modifier = Modifier.fillMaxWidth())
            SkinthesiaTextButton(text = "Skip for now", onClick = {})
        }
    }
}
