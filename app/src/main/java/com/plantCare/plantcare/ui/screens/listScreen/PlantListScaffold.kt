package com.plantCare.plantcare.ui.screens.listScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.plantCare.plantcare.common.Content
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.common.addQuery
import com.plantCare.plantcare.ui.components.BottomBar
import com.plantCare.plantcare.ui.components.TitleTopBar
import com.plantCare.plantcare.viewModel.EditMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScaffold(
    content: Content,
) {
    val navController = NavigationController.current
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TitleTopBar(
                title = "Plants",
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = { BottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController?.navigate(
                        Route.PLANT_EDIT.routeWithArgs(EditMode.ADD).addQuery("id", 0)
                    )
                }
            ) {
                Icon(Icons.Default.Add, "Add plant")
            }
        }
    ) { contentPadding ->
        content(Modifier.padding(contentPadding))
    }
}