package com.mvp.watchdog.ui.i18n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun LanguageDialog(onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val options = listOf(
        "system" to T("跟随系统", "System"),
        "zh" to "中文",
        "en" to "English"
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(T("语言", "Language")) },
        text = {
            Column {
                options.forEach { (code, label) ->
                    Row(
                        Modifier
                            .clickable {
                                Lang.set(ctx, code)
                                onDismiss()
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = Lang.preference == code,
                            onClick = {
                                Lang.set(ctx, code)
                                onDismiss()
                            }
                        )
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Text(
                    T("切换后界面立即生效。", "The UI updates immediately."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(T("完成", "Done")) }
        }
    )
}
