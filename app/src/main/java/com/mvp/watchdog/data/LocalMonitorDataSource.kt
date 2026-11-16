package com.mvp.watchdog.data

import com.mvp.watchdog.data.db.ChangeEvent
import com.mvp.watchdog.data.db.ChangeEventDao
import com.mvp.watchdog.data.db.Monitor
import com.mvp.watchdog.data.db.MonitorDao
import com.mvp.watchdog.data.db.Snapshot
import com.mvp.watchdog.data.db.SnapshotDao
import kotlinx.coroutines.flow.Flow

class LocalMonitorDataSource(
    private val monitorDao: MonitorDao,
    private val snapshotDao: SnapshotDao,
    private val changeEventDao: ChangeEventDao
) : MonitorDataSource {

    override fun listMonitors(): Flow<List<Monitor>> = monitorDao.observeAll()

    override suspend fun saveMonitor(m: Monitor): Long =
        if (m.id == 0L) monitorDao.insert(m) else {
            monitorDao.update(m)
            m.id
        }

    override suspend fun deleteMonitor(id: Long) {
        monitorDao.delete(id)
    }

    override suspend fun getMonitor(id: Long): Monitor? = monitorDao.get(id)

    override suspend fun saveSnapshot(s: Snapshot): Long = snapshotDao.insert(s)

    override suspend fun latestSnapshot(monitorId: Long): Snapshot? =
        snapshotDao.latest(monitorId)

    override fun getSnapshots(monitorId: Long): Flow<List<Snapshot>> =
        snapshotDao.observeByMonitor(monitorId)

    override suspend fun saveChangeEvent(e: ChangeEvent) {
        changeEventDao.insert(e)
    }

    override fun getChangeEvents(monitorId: Long): Flow<List<ChangeEvent>> =
        changeEventDao.observeByMonitor(monitorId)

    override suspend fun markEventsSeen(monitorId: Long) {
        changeEventDao.markSeen(monitorId)
    }

    override fun observeUnseenMonitorIds(): Flow<List<Long>> =
        changeEventDao.observeUnseenMonitorIds()
}
