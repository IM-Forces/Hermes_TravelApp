package com.example.hermes_travelapp.domain.repository

import com.example.hermes_travelapp.data.database.entities.AccessLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining operations for access logging.
 */
interface AccessLogRepository {
    /**
     * Records an access event (Login/Logout).
     *
     * @param userId The ID of the user.
     * @param type The type of event ("IN" or "OUT").
     */
    suspend fun logAccess(userId: String, type: String)

    /**
     * Retrieves all access logs for a specific user as a Flow.
     *
     * @param userId The ID of the user.
     * @return A Flow containing the list of access logs.
     */
    fun getLogsByUser(userId: String): Flow<List<AccessLogEntity>>
}
