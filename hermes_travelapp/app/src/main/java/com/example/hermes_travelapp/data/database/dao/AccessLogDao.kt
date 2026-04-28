package com.example.hermes_travelapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hermes_travelapp.data.database.entities.AccessLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the access_log table.
 */
@Dao
interface AccessLogDao {

    /**
     * Inserts a new access log entry into the database.
     *
     * @param log The access log entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AccessLogEntity)

    /**
     * Retrieves all access logs for a specific user as a Flow.
     *
     * @param userId The ID of the user.
     * @return A Flow containing the list of access log entities.
     */
    @Query("SELECT * FROM access_log WHERE userId = :userId ORDER BY datetime DESC")
    fun getLogsByUser(userId: String): Flow<List<AccessLogEntity>>
}
