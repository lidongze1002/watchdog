package com.mvp.watchdog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitorDao {
    @Query("SELECT * FROM monitors ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Monitor>>

    @Query("SELECT * FROM monitors WHERE id = :id")
    suspend fun get(id: Long): Monitor?

    @Insert
    suspend fun insert(m: Monitor): Long

    @Update
    suspend fun update(m: Monitor)

    @Query("DELETE FROM monitors WHERE id = :id")
    suspend fun delete(id: Long)
}
