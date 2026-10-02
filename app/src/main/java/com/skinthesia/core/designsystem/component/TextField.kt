package com.skinthesia.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.skinthesia.core.designsystem.theme.SkinthesiaTheme

/**
 * Standard single- or multi-line text input: a label above a softly bordered field, with optional
 * placeholder and supporting text. When [isError] is true the supporting text becomes the error
 * message and is announced to accessibility services.
 */
@Composable
fun SkinthesiaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val colors = SkinthesiaTheme.colors
    val typography = SkinthesiaTheme.typography
    val spacing = SkinthesiaTheme.spacing
    val shape = SkinthesiaTheme.shapes.field
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> colors.error
            focused -> colors.primary
            else -> colors.outlineStrong
        },
        animationSpec = tween(PressDurationMillis),
        label = "textFieldBorder",
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.semantics {
            if (isError && supportingText != null) error(supportingText)
        },
        enabled = enabled,
        textStyle = typography.body.copy(color = if (enabled) colors.textPrimary else colors.disabledContent),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(if (isError) colors.error else colors.primary),
        decorationBox = { innerTextField ->
            Column {
                if (label != null) {
                    Text(
                        text = label,
                        style = typography.labelSmall,
                        color = if (isError) colors.error else colors.textSecondary,
                    )
                    Spacer(Modifier.height(spacing.xs))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = spacing.fieldHeight)
                        .clip(shape)
                        .background(if (enabled) colors.surface else colors.surfaceMuted)
                        .border(spacing.hairline, borderColor, shape)
                        .padding(horizontal = spacing.md, vertical = spacing.sm),
                    contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = typography.body,
                            color = colors.textTertiary,
                        )
                    }
                    innerTextField()
                }
                if (supportingText != null) {
                    Spacer(Modifier.height(spacing.xs))
                    Text(
                        text = supportingText,
                        style = typography.caption,
                        color = if (isError) colors.error else colors.textTertiary,
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F1EA)
@Composable
private fun TextFieldPreview() {
    SkinthesiaTheme {
        var name by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.padding(SkinthesiaTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(SkinthesiaTheme.spacing.md),
        ) {
            SkinthesiaTextField(
                value = name,
                onValueChange = { name = it },
                label = "First name",
                placeholder = "How should we call you?",
                modifier = Modifier.fillMaxWidth(),
            )
            SkinthesiaTextField(
                value = "not-an-email",
                onValueChange = {},
                label = "Email",
                supportingText = "Enter a valid email address.",
                isError = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
