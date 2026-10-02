package com.skinthesia.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import com.skinthesia.core.designsystem.theme.skinthesiaShadow

/** How a [SkinthesiaCard] separates itself from the page. */
enum class SkinthesiaCardStyle {
    /** Light surface lifted by a soft warm shadow. The default. */
    Elevated,

    /** Light surface with a hairline outline, for flat or dense layouts. */
    Outlined,

    /** Muted fill with no outline or shadow, for grouping secondary content. */
    Tonal,
}

/**
 * The standard content container. Pass [onClick] to make the whole card a single tappable target.
 */
@Composable
fun SkinthesiaCard(
    modifier: Modifier = Modifier,
    style: SkinthesiaCardStyle = SkinthesiaCardStyle.Elevated,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(SkinthesiaTheme.spacing.cardPadding),
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = SkinthesiaTheme.colors
    val shape = SkinthesiaTheme.shapes.card
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animatePressScale(interactionSource, pressedScale = 0.99f)

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .skinthesiaShadow(
                elevation = if (style == SkinthesiaCardStyle.Elevated) SkinthesiaTheme.elevation.low else SkinthesiaTheme.elevation.none,
                shape = shape,
                color = colors.shadow,
            )
            .clip(shape)
            .background(if (style == SkinthesiaCardStyle.Tonal) colors.surfaceMuted else colors.surface)
            .then(
                if (style == SkinthesiaCardStyle.Outlined) {
                    Modifier.border(SkinthesiaTheme.spacing.hairline, colors.outline, shape)
                } else {
                    Modifier
                },
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = colors.primary),
                        role = Role.Button,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .padding(contentPadding),
        content = content,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun CardPreview() {
    SkinthesiaTheme {
        Column(
            modifier = Modifier.padding(SkinthesiaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(SkinthesiaTheme.spacing.itemGap),
        ) {
            SkinthesiaCardStyle.entries.forEach { style ->
                SkinthesiaCard(style = style, modifier = Modifier.fillMaxWidth()) {
                    Text(text = style.name, style = SkinthesiaTheme.typography.title)
                    Text(
                        text = "Supporting copy sits comfortably inside the card.",
                        style = SkinthesiaTheme.typography.body,
                        color = SkinthesiaTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}
