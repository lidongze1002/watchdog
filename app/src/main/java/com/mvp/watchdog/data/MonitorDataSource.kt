package com.mvp.watchdog.data

import com.mvp.watchdog.data.db.ChangeEvent
import com.mvp.watchdog.data.db.Monitor
import com.mvp.watchdog.data.db.Snapshot
import kotlinx.coroutines.flow.Flow

interface MonitorDataSource {
    fun listMonitors(): Flow<List<Monitor>>
    suspend fun saveMonitor(m: Monitor): Long
    suspend fun deleteMonitor(id: Long)
    suspend fun getMonitor(id: Long): Monitor?
    suspend fun saveSnapshot(s: Snapshot): Long
    suspend fun latestSnapshot(monitorId: Long): Snapshot?
    fun getSnapshots(monitorId: Long): Flow<List<Snapshot>>
    suspend fun saveChangeEvent(e: ChangeEvent)
    fun getChangeEvents(monitorId: Long): Flow<List<ChangeEvent>>
    suspend fun markEventsSeen(monitorId: Long)
    fun observeUnseenMonitorIds(): Flow<List<Long>>
    fun observeAllChangeEvents(): Flow<List<com.mvp.watchdog.data.db.ChangeEventRow>>
}
