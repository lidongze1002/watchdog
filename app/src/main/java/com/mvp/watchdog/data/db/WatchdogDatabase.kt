package com.mvp.watchdog.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Monitor::class, Snapshot::class, ChangeEvent::class],
    version = 1,
    exportSchema = false
)
abstract class WatchdogDatabase : RoomDatabase() {
    abstract fun monitorDao(): MonitorDao
    abstract fun snapshotDao(): SnapshotDao
    abstract fun changeEventDao(): ChangeEventDao
}
