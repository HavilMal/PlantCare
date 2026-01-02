package com.plantCare.plantcare.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.lerp
import com.plantCare.plantcare.ui.theme.typographySecondary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopBar(
    title: String,
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

    LargeTopAppBar(
        title = {
            Text(
                text = title,
                style = dynamicStyle
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = navigationButton,
        actions = { actionButton() },
    )
}