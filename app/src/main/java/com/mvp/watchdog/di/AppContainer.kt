package com.mvp.watchdog.di

import android.content.Context
import androidx.room.Room
import com.mvp.watchdog.data.LocalMonitorDataSource
import com.mvp.watchdog.data.MonitorDataSource
import com.mvp.watchdog.data.db.MIGRATION_1_2
import com.mvp.watchdog.data.db.WatchdogDatabase

class AppContainer(context: Context) {
    private val database: WatchdogDatabase = Room.databaseBuilder(
        context.applicationContext,
        WatchdogDatabase::class.java,
        "watchdog.db"
    ).addMigrations(MIGRATION_1_2).build()

    val monitorDataSource: MonitorDataSource = LocalMonitorDataSource(
        monitorDao = database.monitorDao(),
        snapshotDao = database.snapshotDao(),
        changeEventDao = database.changeEventDao()
    )
}
