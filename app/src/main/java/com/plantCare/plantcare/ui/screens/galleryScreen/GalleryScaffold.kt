package com.plantCare.plantcare.ui.screens.galleryScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.ui.components.TopBar

@Composable
fun GalleryScaffold(content: Content) {
    Scaffold(
        topBar = {
            TopBar(
                text = "Gallery"
            )
        }
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}