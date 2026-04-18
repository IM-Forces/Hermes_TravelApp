package com.example.hermes_travelapp.domain.repository

import com.example.hermes_travelapp.domain.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for managing trips.
 */
interface TripRepository {
    /**
     * Retrieves all trips associated with the current user.
     */
    fun getTrips(): Flow<List<Trip>>

    /**
     * Adds a new trip to the data source.
     */
    suspend fun addTrip(trip: Trip)

    /**
     * Updates an existing trip's details.
     */
    suspend fun editTrip(trip: Trip)

    /**
     * Permanently removes a trip by its ID.
     */
    suspend fun deleteTrip(tripId: String)
}
