package com.mvp.watchdog.ui.monitordetail

import com.mvp.watchdog.ui.i18n.T

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mvp.watchdog.ui.common.AppTopBar

@Composable
fun DiffScreen(
    vm: DiffViewModel,
    onBack: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val keywords by vm.keywords.collectAsState()

    Column(Modifier.fillMaxSize()) {
        AppTopBar(title = T("变更对比", "Diff"), onBack = onBack)
        val d = ui
        if (d == null) {
            Text(T("加载中…", "Loading…"), Modifier.padding(16.dp))
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
                    Text(T("删除", "Delete"), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
                    d.removed.forEach {
                        Text("- ${highlight(it, keywords)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
                if (d.added.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(T("新增", "Added"), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    d.added.forEach {
                        Text("+ ${highlight(it, keywords)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                if (d.added.isEmpty() && d.removed.isEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(T("（这是基准快照，还没有差异）", "(Baseline snapshot — no differences yet)"), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun highlight(text: String, keywords: List<String>): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < text.length) {
        var best: Pair<Int, String>? = null
        for (k in keywords) {
            if (k.isEmpty()) continue
            val idx = text.indexOf(k, i, ignoreCase = true)
            if (idx >= 0 && (best == null || idx < best!!.first)) best = idx to k
        }
        if (best == null) {
            append(text.substring(i))
            break
        }
        val (idx, k) = best
        if (idx > i) append(text.substring(i, idx))
        withStyle(SpanStyle(background = Color.Yellow, color = Color.Black)) {
            append(text.substring(idx, idx + k.length))
        }
        i = idx + k.length
    }
}
