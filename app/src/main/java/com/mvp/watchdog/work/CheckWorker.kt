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
        val monitors = dataSource.listMonitors().first().filter { it.enabled }
        monitors.forEach { m ->
            val outcome = pipeline.check(m.id)
            if (outcome.notify) {
                NotificationHelper.notify(
                    applicationContext,
                    m.name,
                    "检测到变化：" + outcome.matchedKeywords.joinToString(",")
                )
            }
        }
        return Result.success()
    }
}
