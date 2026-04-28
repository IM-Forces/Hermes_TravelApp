package com.example.hermes_travelapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing an access log (login/logout) record.
 */
@Entity(tableName = "access_log")
data class AccessLogEntity(
    /**
     * Unique identifier for the log entry.
     */
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    /**
     * The ID of the user who performed the action.
     */
    val userId: String,

    /**
     * The timestamp of the event in epoch milliseconds.
     */
    val datetime: Long = System.currentTimeMillis(),

    /**
     * The type of access event: "IN" for login, "OUT" for logout.
     */
    val type: String
)
