package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextOverflow
import com.plantCare.plantcare.ui.theme.typographySecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopBarWithSubtext(
    title: String,
    subtext: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationButton: @Composable () -> Unit = {},
    actionButton: @Composable () -> Unit = {},
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction

    val dynamicStyle = lerp(
        start = MaterialTheme.typographySecondary.displayLarge,
        stop = MaterialTheme.typographySecondary.titleLarge,
        fraction = collapsedFraction
    )

    val subtextHeightLerp = (1f - (collapsedFraction * 2.5f)).coerceIn(0f, 1f)
    val subtextAlpha = (1f - (collapsedFraction * 2f)).coerceIn(0f, 1f)

    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = dynamicStyle,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = subtextAlpha
                            // Slight upward slide to make it feel more fluid
                            translationY = -collapsedFraction * 10f
                        }
                        // This prevents the "jump" by smoothly transitioning the scale
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            // We adjust the height of the slot based on our fraction
                            val currentHeight = (placeable.height * subtextHeightLerp).toInt()
                            layout(placeable.width, currentHeight) {
                                placeable.placeRelative(0, 0)
                            }
                        }
                ) {
                    Text(
                        text = subtext,
                        style = MaterialTheme.typographySecondary.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = navigationButton,
        actions = { actionButton() },
    )
}