package com.plantCare.plantcare.ui.screens.plantEditScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.plantCare.plantcare.viewModel.PlantEditViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlantEditScreen(
    viewModel: PlantEditViewModel = hiltViewModel()
) {
    val state by viewModel.plantEditState.collectAsState()

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
                trailingIcon = { /*todo*/ },
                label = { Text("Name") },
                value = state.plantName,
                onValueChange = viewModel::setPlantName,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Species") },
                value = state.species,
                onValueChange = viewModel::setSpecties,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Planted On") },
                value = state.plantedOn.toString(),
                onValueChange = viewModel::setPlantName,
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
            var selected by remember { mutableStateOf(Interval.WEEKLY) }

            IntervalDropdown(
                modifier = Modifier.fillMaxWidth(),
                selected = selected,
                onSelect = { s ->
                    selected = s
                }
            )

            when (selected) {
                Interval.WEEKLY -> {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        for (i in 1..7) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(48.dp),
                            ) {
                                Text(i.toString())
                            }
                        }
                    }
                }

                Interval.MONTHLY -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        for (j in 1..4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                for (i in 1..7) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .width(48.dp)
                                            .height(48.dp),
                                    ) {
                                        Text(i.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}