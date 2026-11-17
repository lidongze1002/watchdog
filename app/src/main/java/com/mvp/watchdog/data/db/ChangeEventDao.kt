package com.mvp.watchdog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class ChangeEventRow(
    val id: Long,
    val monitorId: Long,
    val snapshotId: Long,
    val occurredAt: Long,
    val matchedKeywords: String,
    val snippet: String,
    val seen: Boolean,
    val monitorName: String
)

@Dao
interface ChangeEventDao {
    @Query("SELECT * FROM change_events WHERE monitorId = :id ORDER BY occurredAt DESC")
    fun observeByMonitor(id: Long): Flow<List<ChangeEvent>>

    @Query(
        "SELECT e.id, e.monitorId, e.snapshotId, e.occurredAt, e.matchedKeywords, " +
            "e.snippet, e.seen, m.name AS monitorName " +
            "FROM change_events e JOIN monitors m ON m.id = e.monitorId " +
            "ORDER BY e.occurredAt DESC LIMIT 300"
    )
    fun observeAllWithMonitor(): Flow<List<ChangeEventRow>>

    @Insert
    suspend fun insert(e: ChangeEvent)

    @Query("UPDATE change_events SET seen = 1 WHERE monitorId = :id")
    suspend fun markSeen(id: Long)

    @Query("SELECT DISTINCT monitorId FROM change_events WHERE seen = 0")
    fun observeUnseenMonitorIds(): Flow<List<Long>>

    @Query("DELETE FROM change_events WHERE monitorId = :monitorId")
    suspend fun deleteByMonitor(monitorId: Long)
}
