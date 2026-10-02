package com.skinthesia.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

/**
 * A labelled slider for coarse numeric lifestyle inputs, such as typical sleep duration or daily
 * water intake, where a handful of discrete steps read more naturally than typed numbers.
 */
@Composable
fun SkinthesiaSlider(
    label: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = typography.body, color = colors.textPrimary)
            Text(text = valueLabel, style = typography.label, color = colors.primary)
        }
        Spacer(Modifier.height(spacing.xxs))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary,
                inactiveTrackColor = colors.surfaceMuted,
                activeTickColor = colors.primary,
                inactiveTickColor = colors.outline,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun SkinthesiaSliderPreview() {
    SkinthesiaTheme {
        SkinthesiaSlider(
            label = "Typical sleep duration",
            valueLabel = "7 hrs",
            value = 7f,
            onValueChange = {},
            valueRange = 4f..10f,
            steps = 5,
            modifier = Modifier.fillMaxWidth().padding(SkinthesiaTheme.spacing.lg),
        )
    }
}
