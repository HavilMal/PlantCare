package com.plantCare.plantcare.ui.screens.plantEditScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun PlantEditScreen() {
    PlantEditScaffold { modifier ->
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Information")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = rememberTextFieldState(initialText = "hello"),
                trailingIcon = { /*todo*/ },
                label = { Text("Name") },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = rememberTextFieldState(initialText = "hello"),
                label = { Text("Species") }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = rememberTextFieldState(initialText = "hello"),
                label = { Text("Planted On") }
            )

            var selectedIndex by remember { mutableIntStateOf(0) }

            val options = listOf("Indoor", "Outdoor")

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size,
                        ),
                        onClick = { selectedIndex = index },
                        selected = selectedIndex == index,
                        label = { Text(label) }
                    )
                }
            }

            Text("Sensor")

            Button(
                onClick = { /*todo*/ },
                modifier = Modifier.fillMaxWidth(),
                shape = ButtonDefaults.squareShape
            ) {
                Icon(Icons.Filled.Add, "Add Sensor")
                Text("Add Sensor")
            }


            Text("Watering schedule")
            var expanded by remember { mutableStateOf(true) }

            // todo
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Option 1") },
                    onClick = { /* Do something... */ }
                )
                DropdownMenuItem(
                    text = { Text("Option 2") },
                    onClick = { /* Do something... */ }
                )
            }
        }
    }
}