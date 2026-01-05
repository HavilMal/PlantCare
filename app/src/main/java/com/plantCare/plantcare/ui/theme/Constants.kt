package com.plantCare.plantcare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Size (
    val smallInner: Dp = 16.dp,
    val small: Dp = 24.dp,
    val mediumInner: Dp = 40.dp,
    val medium: Dp = 48.dp,
    val largeInner: Dp = 88.dp,
    val large: Dp = 96.dp,
)

val LocalSize = compositionLocalOf { Size() }

val MaterialTheme.size: Size
    @Composable
    @ReadOnlyComposable
    get() = LocalSize.current

data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 32.dp,
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

data class Radius(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 32.dp,
)

val LocalRadius = compositionLocalOf { Radius() }

val MaterialTheme.radius: Radius
    @Composable
    @ReadOnlyComposable
    get() = LocalRadius.current
