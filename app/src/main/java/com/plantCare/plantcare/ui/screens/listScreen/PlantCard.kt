package com.plantCare.plantcare.ui.screens.listScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plantCare.plantcare.R
import com.plantCare.plantcare.common.NavigationController
import com.plantCare.plantcare.common.Route
import com.plantCare.plantcare.model.Plant


@Composable
fun PlantCard(plant: Plant) {
    val navController = NavigationController.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.sunflower),
            contentDescription = "sunflower",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = plant.name,
                maxLines = 1,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = plant.description,
                maxLines = 3,
                minLines = 3,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = {},
                ) {
                    Text("Water")
                }
                Button(
                    onClick = {
                        navController?.navigate(Route.PLANT.route)
                    }
                ) {
                    Text("View")
                }

            }
        }
    }
}