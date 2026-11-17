package com.mvp.watchdog.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** 列表逐条淡入 + 上滑（错峰）。 */
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

/** 数字滚动：从 0 增长到目标值。 */
@Composable
fun CountUpText(
    target: Int,
    style: TextStyle,
    color: Color = Color.Unspecified
) {
    val animated by animateIntAsState(
        targetValue = target,
        animationSpec = tween(650),
        label = "countUp"
    )
    Text("$animated", style = style, color = color)
}

/** 流光骨架屏背景。 */
@Composable
fun Modifier.shimmer(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = -300f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing)),
        label = "shimmerX"
    )
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surface
    return this.background(
        brush = Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(x, 0f),
            end = Offset(x + 320f, 0f)
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

/** 骨架屏卡片（加载占位）。 */
@Composable
fun SkeletonCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(18.dp)) {
            Box(Modifier.fillMaxWidth(0.55f).height(16.dp).shimmer())
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth(0.9f).height(12.dp).shimmer())
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth(0.35f).height(12.dp).shimmer())
        }
    }
}
