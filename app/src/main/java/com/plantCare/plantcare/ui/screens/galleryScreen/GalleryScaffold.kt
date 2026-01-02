package com.plantCare.plantcare.ui.screens.galleryScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.ui.components.TitleTopBar
import com.plantCare.plantcare.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScaffold(
    onBack: () -> Unit,
    content: Content
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TitleTopBar(
                title = "Gallery",
                scrollBehavior = scrollBehavior,
                navigationButton = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                }
            )
        },
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}