package com.mvp.watchdog.ui.monitorlist

import com.mvp.watchdog.ui.i18n.LanguageDialog
import com.mvp.watchdog.ui.i18n.Lang
import com.mvp.watchdog.ui.i18n.T

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mvp.watchdog.R
import com.mvp.watchdog.ui.common.AppCard
import com.mvp.watchdog.ui.common.Avatar
import com.mvp.watchdog.ui.common.SkeletonCard
import com.mvp.watchdog.ui.common.SearchBar
import com.mvp.watchdog.ui.common.StatusChip
import com.mvp.watchdog.ui.common.staggeredAppear
import com.mvp.watchdog.ui.formatTime
import com.mvp.watchdog.ui.intervalLabel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MonitorListScreen(
    vm: MonitorListViewModel,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    onOpenAllChanges: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val monitors by vm.monitors.collectAsState()
    val unseenIds by vm.unseenIds.collectAsState()
    var loading by remember { mutableStateOf(true) }
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var showLanguage by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    val filtered = if (query.isBlank()) monitors else monitors.filter {
        it.name.contains(query, ignoreCase = true) || it.url.contains(query, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        delay(420)
        loading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(T("添加监控", "Add monitor"))
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        )
                    )
                    .padding(22.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(T("盯梢", "Watchdog"),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        T(
                            "${monitors.size} 个监控 · ${unseenIds.size} 个有更新",
                            "${monitors.size} monitor${if (monitors.size == 1) "" else "s"} · " +
                                "${unseenIds.size} updated"
                        ),
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(T("查看全部变化 ›", "See all changes ›"),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable(onClick = onOpenAllChanges)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                IconButton(onClick = { showLanguage = true }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = T("语言设置", "Language settings"),
                        tint = Color.White
                    )
                }
                }
            }
            Box(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    placeholder = T("搜索监控名称或网址", "Search monitor name or URL")
                )
            }
            PullToRefreshBox(
                isRefreshing = refreshing,
                onRefresh = {
                    refreshing = true
                    scope.launch {
                        delay(700)
                        refreshing = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    loading -> Column(
                        Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(3) { SkeletonCard() }
                    }

                    monitors.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_pulse))
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(120.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(T("还没有监控项", "No monitors yet"), style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(T("点右下角「添加监控」开始", "Tap “Add monitor” at the bottom right"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    filtered.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "没有匹配「$query」的监控",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(filtered, key = { _, m -> m.id }) { index, m ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { v ->
                                    if (v == SwipeToDismissBoxValue.EndToStart) {
                                        pendingDeleteId = m.id
                                    }
                                    false
                                }
                            )
                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.errorContainer)
                                            .padding(horizontal = 24.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = T("删除监控", "Delete monitor"),
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                },
                                modifier = Modifier.staggeredAppear(index)
                            ) {
                                AppCard(onClick = { onOpen(m.id) }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Avatar(m.name)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        val nameModifier = with(sharedTransitionScope) {
                                            Modifier.sharedElement(
                                                rememberSharedContentState("monitor_name_${m.id}"),
                                                animatedVisibilityScope
                                            )
                                        }
                                        Text(
                                            m.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = nameModifier,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            m.url,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            "${intervalLabel(m.intervalMinutes)} · ${formatTime(m.lastCheckedAt)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (m.lastError.isNotBlank()) {
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                "上次失败：${m.lastError}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    if (m.id in unseenIds) {
                                        StatusChip(
                                            text = "有更新",
                                            container = MaterialTheme.colorScheme.primaryContainer,
                                            content = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    } else if (m.lastError.isNotBlank()) {
                                        StatusChip(
                                            text = "失败",
                                            container = MaterialTheme.colorScheme.errorContainer,
                                            content = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (pendingDeleteId != null) {
                AlertDialog(
                    onDismissRequest = { pendingDeleteId = null },
                    title = { Text(T("删除监控", "Delete monitor")) },
                    text = { Text(T("确定删除这个监控？相关快照与变化记录也会一并删除。", "Delete this monitor? Its snapshots and change history will also be removed.")) },
                    confirmButton = {
                        TextButton(onClick = {
                            pendingDeleteId?.let { vm.delete(it) }
                            pendingDeleteId = null
                        }) { Text(T("删除", "Delete")) }
                    },
                    dismissButton = { TextButton(onClick = { pendingDeleteId = null }) { Text(T("取消", "Cancel")) } }
                )
            }
            if (showLanguage) {
                LanguageDialog(onDismiss = { showLanguage = false })
            }
        }
    }
}
}
