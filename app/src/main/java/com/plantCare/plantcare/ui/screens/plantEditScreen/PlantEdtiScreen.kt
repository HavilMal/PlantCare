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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
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
import com.plantCare.plantcare.common.getLocale
import com.plantCare.plantcare.viewModel.PlantEditViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date

@Composable
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", getLocale())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlantEditScreen(
    viewModel: PlantEditViewModel = hiltViewModel()
) {
    val state by viewModel.plantEditState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = getLocale()
    val formatter = remember(locale) {DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)}

    PlantEditScaffold(viewModel) { modifier ->
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

            // todo click on field
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Planted On") },
                value = state.plantedOn.format(formatter),
                readOnly = true,
                onValueChange = {},
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
            )

            if (showDatePicker) {
                DatePickerModal(
                    onDateSelected = { it?.let { viewModel.setPlantedOn(it) } },
                    onDismiss = { showDatePicker = false }
                )
            }

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
            var selected by remember { mutableStateOf(WateringInterval.WEEKLY) }

            IntervalDropdown(
                modifier = Modifier.fillMaxWidth(),
                selected = selected,
                onSelect = { s ->
                    selected = s
                }
            )

            when (selected) {
                WateringInterval.WEEKLY -> {
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

                WateringInterval.MONTHLY -> {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.getSelectedDate())
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}