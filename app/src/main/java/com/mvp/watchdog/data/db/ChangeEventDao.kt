package com.mvp.watchdog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChangeEventDao {
    @Query("SELECT * FROM change_events WHERE monitorId = :id ORDER BY occurredAt DESC")
    fun observeByMonitor(id: Long): Flow<List<ChangeEvent>>

    @Insert
    suspend fun insert(e: ChangeEvent)

    @Query("UPDATE change_events SET seen = 1 WHERE monitorId = :id")
    suspend fun markSeen(id: Long)
}
