package com.mvp.watchdog.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

/**
 * 列表逐条淡入 + 上滑（错峰）。参考 Material / 各类开源列表入场动效。
 */
@Composable
fun Modifier.staggeredAppear(index: Int): Modifier {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((index.coerceAtMost(12)) * 35L)
        shown = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(280),
        label = "staggeredAlpha"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (shown) 0f else 24f,
        animationSpec = tween(280),
        label = "staggeredY"
    )
    return this.graphicsLayer {
        this.alpha = alpha
        translationY = offsetY
    }
}
