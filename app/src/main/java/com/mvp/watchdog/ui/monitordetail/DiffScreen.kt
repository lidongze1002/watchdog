package com.mvp.watchdog.ui.monitordetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DiffScreen(
    vm: DiffViewModel,
    onBack: () -> Unit
) {
    val ui by vm.ui.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Surface(tonalElevation = 2.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
                TextButton(onClick = onBack) { Text("返回") }
                Text("变更对比", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
            }
        }
        val d = ui
        if (d == null) {
            Text("加载中…", Modifier.padding(16.dp))
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(d.title, style = MaterialTheme.typography.titleMedium)
                if (d.removed.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("删除", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
                    d.removed.forEach { Text("- $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
                }
                if (d.added.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("新增", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    d.added.forEach { Text("+ $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
                }
                if (d.added.isEmpty() && d.removed.isEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("（这是基准快照，还没有差异）", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
