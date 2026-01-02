package com.plantCare.plantcare.ui.screens.homeScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.ui.components.BottomBar
import com.plantCare.plantcare.ui.components.TitleTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScaffold(actionButton: @Composable () -> Unit = {}, content: Content) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TitleTopBar(
                title = "Plant Care",
                scrollBehavior = scrollBehavior,
                actionButton = actionButton
            )
        },
        bottomBar = { BottomBar() }
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}