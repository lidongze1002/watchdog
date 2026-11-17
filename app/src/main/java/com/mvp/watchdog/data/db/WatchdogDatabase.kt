package com.mvp.watchdog.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE monitors ADD COLUMN intervalMinutes INTEGER NOT NULL DEFAULT 60")
        db.execSQL("ALTER TABLE monitors ADD COLUMN lastError TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE monitors ADD COLUMN lastErrorAt INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(
    entities = [Monitor::class, Snapshot::class, ChangeEvent::class],
    version = 2,
    exportSchema = false
)
abstract class WatchdogDatabase : RoomDatabase() {
    abstract fun monitorDao(): MonitorDao
    abstract fun snapshotDao(): SnapshotDao
    abstract fun changeEventDao(): ChangeEventDao
}
