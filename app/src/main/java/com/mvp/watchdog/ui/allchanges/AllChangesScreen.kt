package com.mvp.watchdog.ui.allchanges

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mvp.watchdog.data.db.ChangeEventRow
import com.mvp.watchdog.ui.common.AppCard
import com.mvp.watchdog.ui.common.AppTopBar
import com.mvp.watchdog.ui.common.SearchBar
import com.mvp.watchdog.ui.common.StatusChip
import com.mvp.watchdog.ui.common.staggeredAppear
import com.mvp.watchdog.ui.formatClock

@Composable
fun AllChangesScreen(
    vm: AllChangesViewModel,
    onBack: () -> Unit,
    onOpenMonitor: (Long) -> Unit
) {
    val query by vm.query.collectAsState()
    val groups by vm.groups.collectAsState()
    val total by vm.total.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { AppTopBar(title = "全部变化", onBack = onBack) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                SearchBar(
                    query = query,
                    onQueryChange = vm::onQueryChange,
                    placeholder = "搜索监控名、关键词或变化内容"
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "共 $total 条变化",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (groups.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (query.isBlank()) "还没有变化记录" else "没有匹配的变化",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groups.forEach { group ->
                        item(key = "day_${group.label}") {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp, bottom = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    group.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "${group.rows.size} 条",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        items(group.rows, key = { it.id }) { row ->
                            ChangeRowCard(row = row, onClick = { onOpenMonitor(row.monitorId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangeRowCard(row: ChangeEventRow, onClick: () -> Unit) {
    AppCard(onClick = onClick, modifier = Modifier.staggeredAppear(0)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                row.monitorName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                formatClock(row.occurredAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (row.matchedKeywords.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            StatusChip(
                text = row.matchedKeywords.replace(",", "、"),
                container = MaterialTheme.colorScheme.secondaryContainer,
                content = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        if (row.snippet.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                row.snippet,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
