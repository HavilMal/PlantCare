package com.plantCare.plantcare.ui.screens.plantScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.common.capitalize
import com.plantCare.plantcare.common.getLocale
import com.plantCare.plantcare.database.PlantDetails
import com.plantCare.plantcare.ui.components.TextCard
import com.plantCare.plantcare.utils.ifNotEmpty
import com.plantCare.plantcare.utils.listToStringWith
import java.util.Locale


@Composable
fun PlantTipsCard(
    modifier: Modifier = Modifier,
    details: PlantDetails?,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        if (details == null) {
            TextCard(
                title = "Tips",
                content = null,
                noContent = "No tips for this plant"
            )
        } else {
            val tips = detailsToTips(details, getLocale())
            val pagerState = rememberPagerState(pageCount = { tips.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                pageSpacing = 16.dp
            ) { page ->
                TextCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    title = tips[page].title,
                    content = tips[page].content,
                    noContent = "Failed to load tip"
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        shape = RoundedCornerShape(16.dp)
                    ),
            ) {
                repeat(tips.size) { index ->
                    val isSelected = pagerState.currentPage == index

                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                            )
                    )
                }
            }
        }
    }
}

data class Tip(
    val title: String,
    val content: String,
)

fun detailsToTips(details: PlantDetails, locale: Locale): List<Tip> {
    val tips = mutableListOf<Tip>()

    details.wateringValue.filter { it != '"' }.ifNotEmpty { value ->
        tips.add(
            Tip(
                "Watering",
                "${details.commonName.capitalize(locale)} should be watered every $value ${
                    details.wateringUnit.lowercase(
                        locale
                    )
                }."
            )
        )
    }


    listToStringWith(details.sunlight, "or").ifNotEmpty { sunlight ->
        tips.add(
            Tip(
                "Sunlight",
                "${details.commonName.capitalize(locale)} likes $sunlight."
            )
        )
    }


    listToStringWith(details.pruningMonths, "or").ifNotEmpty { pruning ->
        tips.add(
            Tip(
                "Pruning",
                "${details.commonName.capitalize(locale)} should be pruned in $pruning."
            )
        )
    }

    listToStringWith(details.soil, "or").ifNotEmpty { soil ->
        tips.add(
            Tip(
                "Soil",
                "${details.commonName.capitalize(locale)} likes $soil"
            )
        )
    }

    return tips
}
