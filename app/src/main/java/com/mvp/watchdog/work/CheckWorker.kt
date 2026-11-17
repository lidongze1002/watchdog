package com.mvp.watchdog.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mvp.watchdog.WatchdogApp
import com.mvp.watchdog.data.CheckPipeline
import com.mvp.watchdog.notify.NotificationHelper
import kotlinx.coroutines.flow.first

class CheckWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as WatchdogApp
        val dataSource = app.container.monitorDataSource
        val pipeline = CheckPipeline(dataSource)
        val now = System.currentTimeMillis()

        // 每个监控有自己的检查频率；worker 每 15 分钟跑一次，只处理到期的
        val due = dataSource.listMonitors().first()
            .filter { CheckSchedule.isDue(it, now) }

        val changed = due.mapNotNull { monitor ->
            val outcome = pipeline.check(monitor.id)
            if (outcome.notify) monitor to outcome else null
        }

        when {
            changed.size == 1 -> {
                val (monitor, outcome) = changed.first()
                NotificationHelper.notify(
                    applicationContext,
                    monitor.name,
                    com.mvp.watchdog.ui.i18n.T("检测到变化：", "Change detected: ") +
                        outcome.matchedKeywords.joinToString("、")
                )
            }
            changed.size > 1 -> {
                NotificationHelper.notifySummary(
                    applicationContext,
                    changed.map { (monitor, outcome) -> monitor.name to outcome.matchedKeywords }
                )
            }
        }
        return Result.success()
    }
}
