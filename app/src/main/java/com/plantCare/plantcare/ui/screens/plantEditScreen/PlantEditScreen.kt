package com.plantCare.plantcare.ui.screens.plantEditScreen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.plantCare.plantcare.common.getLocale
import com.plantCare.plantcare.ui.theme.spacing
import com.plantCare.plantcare.viewModel.PlantEditViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalPermissionsApi::class)
@Composable
fun PlantEditScreen(
    viewModel: PlantEditViewModel = hiltViewModel()
) {
    val state by viewModel.plantEditState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = getLocale()
    val context = LocalContext.current
    val formatter =
        remember(locale) { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Indoor", "Outdoor")
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDatePicker = true
            }
        }
    }


    if (state.loadingError) {
        Toast.makeText(context, "Plant loading error", Toast.LENGTH_LONG).show()
    }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    PlantEditScaffold(viewModel) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Information")
            }

            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    value = state.plantName,
                    onValueChange = viewModel::setPlantName,
                    singleLine = true,
                    isError = !state.nameError.isEmpty(),
                    supportingText = {
                        if (!state.nameError.isEmpty()) {
                            Text(state.nameError)
                        }
                    }
                )
            }

            item {
                SpeciesSearch(
                    queryString = state.species,
                    expanded = state.showSearchResults,
                    results = state.searchResults,
                    onSelect = { viewModel.setSelectedPlant(it, locale) },
                    onQueryChange = viewModel::setSpeciesName,
                    onSearch = viewModel::searchSpecies,
                    onExpandedChange = { viewModel.setShowResults(it) },
                    isSearching = state.isSearching,
                    error = state.speciesError,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text("Planted On") },
                    value = state.plantedOn.format(formatter),
                    readOnly = true,
                    interactionSource = interactionSource,
                    onValueChange = {},
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    },
                )
            }

            item {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size,
                            ),
                            onClick = {
                                selectedIndex = index
                                viewModel.setIsIndoor(index == 0)
                            },
                            selected = selectedIndex == index,
                            label = { Text(label) }
                        )
                    }
                }
            }

            item {
                Text("Sensor")
            }

            item {
                SensorButton(
                    bluetoothOn = state.bluetoothOn,
                    state = state.sensorButtonState,
                    error = state.scanError,
                    onScanForSensor = { viewModel.scanForSensors() },
                    onRemoveSensor = { viewModel.removeSensor() },
                )
            }


            item {
                Text("Watering schedule")
            }

            item {
                IntervalDropdown(
                    modifier = Modifier.fillMaxWidth(),
                    onSelect = { s ->
                        viewModel.setInterval(s)
                    },
                    dropdownState = state.interval
                )
            }


            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    DayOfWeek.entries.forEach { dayOfWeek ->
                        ToggleButton(
                            modifier = Modifier
                                .padding(2.dp)
                                .aspectRatio(1f)
                                .weight(1f),
                            checked = state.selectedDays.contains(dayOfWeek),
                            onCheckedChange = { it ->
                                if (it) {
                                    viewModel.selectDay(dayOfWeek)
                                } else {
                                    viewModel.unselectDay(dayOfWeek)
                                }
                            },
                        ) {
                            Text(
                                dayOfWeek.getDisplayName(TextStyle.SHORT, getLocale()).first()
                                    .uppercase(),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }


            item {
                Text("Description")
            }

            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.description,
                    onValueChange = viewModel::setDescription,
                )
            }


        }
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { it?.let { viewModel.setPlantedOn(it) } },
                onDismiss = { showDatePicker = false }
            )
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