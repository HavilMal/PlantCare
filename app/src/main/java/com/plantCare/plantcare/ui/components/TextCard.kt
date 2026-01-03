package com.plantCare.plantcare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.ui.theme.spacing

@Composable
fun TextCard(
    modifier: Modifier = Modifier,
    title: String,
    content: String?,
    noContent: String,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = title,
            )
            if (content == null || content.isEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    text = noContent,
                )
            } else {
                content
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = content,
                )
            }
        }
    }
}

@Composable
fun TextCard(
    modifier: Modifier = Modifier,
    title: String,
    hasContent: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = title,
            )
            ContentCompositionRow(hasContent) {
                content()
            }
        }
    }
}

@Composable
fun ContentCompositionRow(
    hasContent: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides if (hasContent) {
            Color.Unspecified
        } else {
            MaterialTheme.colorScheme.outline
        },
        LocalTextStyle provides MaterialTheme.typography.bodyLarge
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = MaterialTheme.spacing.extraSmall,
                alignment = Alignment.CenterHorizontally,
            )
        ) {
            content()
        }
    }
}