package com.mvp.watchdog.ui.monitorform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MonitorFormScreen(
    vm: MonitorFormViewModel,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Surface(tonalElevation = 2.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
                TextButton(onClick = onBack) { Text("返回") }
                Text("添加监控", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = vm::onNameChange,
                label = { Text("名称") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.url,
                onValueChange = vm::onUrlChange,
                label = { Text("网址 URL") },
                isError = state.error != null,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.keywords,
                onValueChange = vm::onKeywordsChange,
                label = { Text("关注点（可空，留空则页面变化即提醒）") },
                modifier = Modifier.fillMaxWidth()
            )
            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { vm.save(onBack) }, enabled = !state.saving, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.saving) "保存并建立基准…" else "保存")
            }
        }
    }
}
