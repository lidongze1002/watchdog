package com.mvp.watchdog.ui.monitordetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mvp.watchdog.ui.common.AppCard
import com.mvp.watchdog.ui.common.AppTopBar
import com.mvp.watchdog.ui.formatTime

@Composable
fun MonitorDetailScreen(
    vm: MonitorDetailViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDiff: (Long) -> Unit
) {
    val monitor by vm.monitor.collectAsState()
    val events by vm.events.collectAsState()
    val checking by vm.checking.collectAsState()
    val checkMessage by vm.checkMessage.collectAsState()
    val latestSnapshot by vm.latestSnapshot.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(
            title = monitor?.name ?: "详情",
            onBack = onBack,
            actions = {
                IconButton(onClick = { monitor?.let { onEdit(it.id) } }) {
                    Icon(Icons.Filled.Edit, contentDescription = "编辑")
                }
            }
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            monitor?.let { m ->
                Text(m.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("关注点：${m.keywords.ifBlank { "（任意变化）" }}", style = MaterialTheme.typography.bodyMedium)
                Text("上次检查：${formatTime(m.lastCheckedAt)}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = vm::check, enabled = !checking, modifier = Modifier.fillMaxWidth()) {
                Text(if (checking) "检查中…" else "立即检查")
            }
            checkMessage?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(16.dp))
            val snap = latestSnapshot
            if (snap != null) {
                Text("最近快照", style = MaterialTheme.typography.titleMedium)
                if (snap.title.isNotBlank()) {
                    Text(snap.title, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    snap.textContent.take(200),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
            }
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("变化历史", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { monitor?.let { onDiff(it.id) } }, enabled = events.isNotEmpty()) {
                    Text("查看 Diff")
                }
            }
            if (events.isEmpty()) {
                Text("还没有变化记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    events.forEach { e ->
                        AppCard {
                            Text(
                                formatTime(e.occurredAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (e.matchedKeywords.isNotEmpty()) {
                                Text("命中：${e.matchedKeywords}", style = MaterialTheme.typography.bodyMedium)
                            }
                            if (e.snippet.isNotEmpty()) {
                                Text(
                                    e.snippet,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("删除监控") }
        }
    }
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("删除监控") },
            text = { Text("确定删除「${monitor?.name}」？相关快照与变化记录也会一并删除。") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    vm.deleteMonitor(onBack)
                }) { Text("删除") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("取消") } }
        )
    }
}
