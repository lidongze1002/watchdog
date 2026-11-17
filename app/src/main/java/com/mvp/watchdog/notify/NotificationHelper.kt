package com.mvp.watchdog.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mvp.watchdog.MainActivity

object NotificationHelper {
    const val CHANNEL_ID = "watchdog_changes"

    fun ensureChannel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                com.mvp.watchdog.ui.i18n.T("监控变化", "Monitor changes"),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    private fun contentIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun allowed(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    fun notify(context: Context, title: String, text: String) {
        if (!allowed(context)) return
        ensureChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(contentIntent(context))
            .setAutoCancel(true)
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(1, notification)
        }
    }

    /** 一轮检查里多个监控都有变化时，合并成一条，避免通知轰炸。 */
    fun notifySummary(context: Context, items: List<Pair<String, List<String>>>) {
        if (items.isEmpty() || !allowed(context)) return
        ensureChannel(context)
        val lines = items.joinToString("\n") { (name, keywords) ->
            val hits = keywords.joinToString("、").ifBlank {
                com.mvp.watchdog.ui.i18n.T("页面变化", "Page changed")
            }
            val sep = if (com.mvp.watchdog.ui.i18n.Lang.current == "en") ": " else "："
            "• $name$sep$hits"
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(
                com.mvp.watchdog.ui.i18n.T(
                    "${items.size} 个监控有变化",
                    "${items.size} monitors changed"
                )
            )
            .setContentText(items.joinToString("、") { it.first })
            .setStyle(NotificationCompat.BigTextStyle().bigText(lines))
            .setContentIntent(contentIntent(context))
            .setAutoCancel(true)
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(100, notification)
        }
    }
}
