package com.plantCare.plantcare.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.ui.components.BottomBar
import com.plantCare.plantcare.ui.components.TopBar

@Composable
fun MainScaffold(label: String, actionButton: @Composable () -> Unit = {}, content: Content) {
    Scaffold(
        topBar = {
            TopBar(
                text = label,
                actionButton = actionButton,
            )
        },
        bottomBar = { BottomBar() }
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}