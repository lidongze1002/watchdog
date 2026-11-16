package com.mvp.watchdog.ui.monitorlist

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mvp.watchdog.ui.common.AppTopBar
import com.mvp.watchdog.ui.formatTime

@Composable
fun MonitorListScreen(
    vm: MonitorListViewModel,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit
) {
    val monitors by vm.monitors.collectAsState()
    val unseenIds by vm.unseenIds.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = "盯梢") },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd) { Text("+ 添加监控") }
        }
    ) { padding ->
        if (monitors.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("还没有监控项", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "点右下角「+ 添加监控」开始",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(monitors, key = { it.id }) { m ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpen(m.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(m.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                if (m.id in unseenIds) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    )
                                }
                            }
                            Text(
                                m.url,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                formatTime(m.lastCheckedAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
