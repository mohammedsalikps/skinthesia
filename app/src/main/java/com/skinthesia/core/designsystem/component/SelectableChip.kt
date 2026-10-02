package com.skinthesia.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
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
 * A pill-shaped selectable option for multi- or single-choice pickers (skin goals, demographic
 * options). Selecting tints the chip with the primary container colour and reveals a small check.
 */
@Composable
fun SkinthesiaSelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    val shape = SkinthesiaTheme.shapes.pill
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animatePressScale(interactionSource, pressedScale = 0.97f)
    val containerColor by animateColorAsState(
        targetValue = if (selected) colors.primaryContainer else colors.surface,
        animationSpec = tween(PressDurationMillis),
        label = "chipContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) colors.primary else colors.outline,
        animationSpec = tween(PressDurationMillis),
        label = "chipBorder",
    )
    val contentColor = if (selected) colors.onPrimaryContainer else colors.textSecondary
    val selectedState = stringResource(R.string.designsystem_selected)
    val notSelectedState = stringResource(R.string.designsystem_not_selected)

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .heightIn(min = spacing.touchTarget)
            .clip(shape)
            .background(containerColor)
            .border(spacing.hairline, borderColor, shape)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .semantics { stateDescription = if (selected) selectedState else notSelectedState }
            .padding(horizontal = spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(tween(PressDurationMillis)) + expandHorizontally(tween(PressDurationMillis)),
            exit = fadeOut(tween(PressDurationMillis)) + shrinkHorizontally(tween(PressDurationMillis)),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = SkinthesiaIcons.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(spacing.iconSmall),
                )
                Spacer(Modifier.width(spacing.xxs))
            }
        }
        Text(
            text = label,
            style = SkinthesiaTheme.typography.label,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun SelectableChipPreview() {
    SkinthesiaTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SkinthesiaTheme.spacing.xs),
            modifier = Modifier.padding(SkinthesiaTheme.spacing.lg),
        ) {
            SkinthesiaSelectableChip(label = "Acne", selected = true, onClick = {})
            SkinthesiaSelectableChip(label = "Dryness", selected = false, onClick = {})
        }
    }
}
