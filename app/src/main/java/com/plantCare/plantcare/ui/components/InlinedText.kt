package com.plantCare.plantcare.ui.components

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle

@Composable
fun InlinedText(
    modifier: Modifier = Modifier,
    annotatedText: AnnotatedString,
    annotationDictionary: Map<String, ImageVector>,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(
        modifier = modifier,
        style = style,
        color = color,
        text = annotatedText,
        inlineContent = annotationDictionary.map { (entry, image) ->
            entry to InlineTextContent(
                placeholder = Placeholder(
                    width = style.fontSize,
                    height = style.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(
                    imageVector = image,
                    contentDescription = null,
                    tint = color,
                )
            }
        }.toMap()
    )
}