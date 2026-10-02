package com.skinthesia.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.skinthesia.core.designsystem.icon.SkinthesiaIcons
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

/** The five persistent destinations of the app's main shell. */
enum class SkinthesiaBottomNavTab(val icon: ImageVector) {
    HOME(SkinthesiaIcons.Home),
    ANALYZE(SkinthesiaIcons.Analyze),
    JOURNEY(SkinthesiaIcons.Journey),
    LEARN(SkinthesiaIcons.Learn),
    PROFILE(SkinthesiaIcons.Profile),
}

private val SelectedIndicatorSize = DpSize(width = 40.dp, height = 28.dp)

/**
 * The app's persistent bottom navigation: a compact, hairline-bordered bar with a quiet pill
 * indicator behind the selected icon. Deliberately smaller and quieter than a default Material 3
 * navigation bar, in keeping with the rest of the design system.
 */
@Composable
fun SkinthesiaBottomNavigationBar(
    selected: SkinthesiaBottomNavTab,
    labelFor: @Composable (SkinthesiaBottomNavTab) -> String,
    onTabClick: (SkinthesiaBottomNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.hairline)
                .background(colors.outline),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .navigationBarsPadding()
                .padding(vertical = spacing.xs),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SkinthesiaBottomNavTab.entries.forEach { tab ->
                BottomNavItem(
                    tab = tab,
                    label = labelFor(tab),
                    isSelected = tab == selected,
                    onClick = { onTabClick(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    tab: SkinthesiaBottomNavTab,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SkinthesiaTheme.colors
    val spacing = SkinthesiaTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (isSelected) colors.primary else colors.textTertiary

    Column(
        modifier = modifier
            .clip(SkinthesiaTheme.shapes.medium)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(vertical = spacing.xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(SelectedIndicatorSize.width, SelectedIndicatorSize.height)
                .clip(SkinthesiaTheme.shapes.navigationIndicator)
                .background(if (isSelected) colors.primaryContainer else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(spacing.iconMedium),
            )
        }
        Spacer(Modifier.height(spacing.xxs))
        Text(text = label, style = SkinthesiaTheme.typography.labelSmall, color = contentColor)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun BottomNavigationPreview() {
    SkinthesiaTheme {
        SkinthesiaBottomNavigationBar(
            selected = SkinthesiaBottomNavTab.HOME,
            labelFor = { tab ->
                when (tab) {
                    SkinthesiaBottomNavTab.HOME -> "Home"
                    SkinthesiaBottomNavTab.ANALYZE -> "Analyze"
                    SkinthesiaBottomNavTab.JOURNEY -> "Journey"
                    SkinthesiaBottomNavTab.LEARN -> "Learn"
                    SkinthesiaBottomNavTab.PROFILE -> "Profile"
                }
            },
            onTabClick = {},
        )
    }
}
