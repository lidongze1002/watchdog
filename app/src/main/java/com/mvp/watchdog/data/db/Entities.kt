package com.mvp.watchdog.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "monitors")
data class Monitor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val name: String,
    val keywords: String,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastCheckedAt: Long = 0,
    val lastSnapshotId: Long = 0
)

@Entity(tableName = "snapshots", indices = [Index("monitorId")])
data class Snapshot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monitorId: Long,
    val fetchedAt: Long,
    val title: String,
    val textHash: String,
    val textContent: String,
    val changed: Boolean
)

@Entity(tableName = "change_events", indices = [Index("monitorId")])
data class ChangeEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monitorId: Long,
    val snapshotId: Long,
    val occurredAt: Long,
    val matchedKeywords: String,
    val snippet: String,
    val seen: Boolean = false
)
