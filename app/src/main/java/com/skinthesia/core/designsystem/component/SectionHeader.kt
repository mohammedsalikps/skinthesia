package com.skinthesia.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme
import java.util.Locale

/**
 * Introduces a group of content: a serif heading, an optional uppercase [overline] above it and
 * an optional trailing action such as "View all". The heading is exposed as a semantic heading.
 */
@Composable
fun SkinthesiaSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    overline: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .alignBy(LastBaseline),
        ) {
            if (overline != null) {
                Text(
                    text = overline.uppercase(Locale.getDefault()),
                    style = typography.overline,
                    color = colors.textTertiary,
                )
                Spacer(Modifier.height(spacing.xxs))
            }
            Text(
                text = title,
                style = typography.sectionHeading,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
        }
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.width(spacing.sm))
            SkinthesiaTextButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.alignBy(LastBaseline),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun SectionHeaderPreview() {
    SkinthesiaTheme {
        SkinthesiaSectionHeader(
            title = "Recommended for you",
            overline = "Learn",
            actionLabel = "View all",
            onAction = {},
            modifier = Modifier.padding(SkinthesiaTheme.spacing.lg),
        )
    }
}
