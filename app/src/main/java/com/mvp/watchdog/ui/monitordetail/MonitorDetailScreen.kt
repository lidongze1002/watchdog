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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mvp.watchdog.ui.formatTime

@Composable
fun MonitorDetailScreen(
    vm: MonitorDetailViewModel,
    onBack: () -> Unit,
    onDiff: (Long) -> Unit
) {
    val monitor by vm.monitor.collectAsState()
    val events by vm.events.collectAsState()
    val checking by vm.checking.collectAsState()
    val checkMessage by vm.checkMessage.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Surface(tonalElevation = 2.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
                TextButton(onClick = onBack) { Text("返回") }
                Text(monitor?.name ?: "详情", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
            }
        }
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
                events.forEach { e ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(formatTime(e.occurredAt), style = MaterialTheme.typography.bodySmall)
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
        }
    }
}
