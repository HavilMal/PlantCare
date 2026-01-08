package com.plantCare.plantcare.ui.screens.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plantCare.plantcare.utils.RandomUtil


@Composable
fun Flower(
){
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color.Yellow, CircleShape)
            .graphicsLayer{
                translationX = RandomUtil.genUniNormFloat()
                translationY = RandomUtil.genUniNormFloat()
            }
    ){}
}

@Composable
fun RandomFlowersAnimation(
    itemCount: Int,

){

    var i: Int = 0
    while(i<itemCount){
        Flower()

        i++
    }
}