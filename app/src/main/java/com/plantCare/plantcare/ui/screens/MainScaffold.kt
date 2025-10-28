package com.plantCare.plantcare.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.ui.components.BottomBar
import com.plantCare.plantcare.ui.components.TopBar

@Composable
fun MainScaffold(content: @Composable () -> Unit) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) { contentPadding ->
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            content()
        }
    }
}