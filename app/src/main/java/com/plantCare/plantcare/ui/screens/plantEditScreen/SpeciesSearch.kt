package com.plantCare.plantcare.ui.screens.plantEditScreen

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.plantCare.plantcare.common.capitalize
import com.plantCare.plantcare.common.getLocale
import com.plantCare.plantcare.service.PlantSearchResult
import com.plantCare.plantcare.ui.theme.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesSearch(
    queryString: String,
    expanded: Boolean,
    isSearching: Boolean,
    results: List<PlantSearchResult>,
    onSelect: (PlantSearchResult) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    error: String,
    modifier: Modifier = Modifier,
) {

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {},
    ) {
        OutlinedTextField(
            value = queryString,
            onValueChange = { onQueryChange(it) },
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            singleLine = true,
            label = { Text("Species") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(MaterialTheme.size.small)
                    )
                } else {
                    IconButton(
                        onClick = { onSearch() }
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }
            },
            isError = !error.isEmpty(),
                supportingText = {
                    if (!error.isEmpty()) {
                        Text(error)
                    }
                }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            results.ifEmpty {
                DropdownMenuItem(
                    text = { Text("No results", style = MaterialTheme.typography.bodyLarge) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        onExpandedChange(false)
                    },
                )
            }

            results.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.commonName.capitalize(getLocale()),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        onExpandedChange(false)
                        onSelect(option)
                    },
                )
            }
        }
    }
}