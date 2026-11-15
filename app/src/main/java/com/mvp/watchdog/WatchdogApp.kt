package com.mvp.watchdog

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mvp.watchdog.di.AppContainer
import com.mvp.watchdog.work.CheckWorker
import java.util.concurrent.TimeUnit

class WatchdogApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        scheduleChecks()
    }

    private fun scheduleChecks() {
        val request = PeriodicWorkRequestBuilder<CheckWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("watchdog_checks", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
