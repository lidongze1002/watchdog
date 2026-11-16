package com.mvp.watchdog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SnapshotDao {
    @Query("SELECT * FROM snapshots WHERE monitorId = :id ORDER BY fetchedAt DESC")
    fun observeByMonitor(id: Long): Flow<List<Snapshot>>

    @Query("SELECT * FROM snapshots WHERE monitorId = :id ORDER BY fetchedAt DESC LIMIT 1")
    suspend fun latest(id: Long): Snapshot?

    @Insert
    suspend fun insert(s: Snapshot): Long

    @Query("DELETE FROM snapshots WHERE monitorId = :monitorId")
    suspend fun deleteByMonitor(monitorId: Long)
}
