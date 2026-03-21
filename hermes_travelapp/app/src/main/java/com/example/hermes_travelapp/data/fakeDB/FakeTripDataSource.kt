package com.example.hermes_travelapp.data.fakeDB

import android.util.Log
import com.example.hermes_travelapp.domain.model.Trip

/**
 * Singleton data source providing in-memory storage for Trips.
 * Reactivity is now managed by the ViewModel's StateFlow.
 */
object FakeTripDataSource {
    private const val TAG = "FakeTripDataSource"
    
    private val _trips = mutableListOf<Trip>()

    init {
        Log.d(TAG, "Initializing FakeTripDataSource: Starting with 0 trips.")
    }

    /**
     * Returns all trips in the list.
     */
    fun getTrips(): List<Trip> {
        Log.d(TAG, "getTrips called: fetching all stored trips.")
        val result = _trips.toList()
        Log.i(TAG, "getTrips: successfully retrieved ${result.size} trips.")
        return result
    }

    /**
     * Adds a new trip to the in-memory list.
     */
    fun addTrip(trip: Trip) {
        Log.d(TAG, "addTrip called with title: ${trip.title}, id: ${trip.id}")
        if (_trips.any { it.id == trip.id }) {
            Log.e(TAG, "addTrip failed: trip with id ${trip.id} already exists.")
            return
        }
        _trips.add(trip)
        Log.i(TAG, "Trip added successfully with id: ${trip.id}")
    }

    /**
     * Updates an existing trip by matching its ID.
     */
    fun updateTrip(updatedTrip: Trip) {
        Log.d(TAG, "updateTrip called for id: ${updatedTrip.id} with title: ${updatedTrip.title}")
        val index = _trips.indexOfFirst { it.id == updatedTrip.id }
        if (index != -1) {
            _trips[index] = updatedTrip
            Log.i(TAG, "Trip id: ${updatedTrip.id} updated successfully.")
        } else {
            Log.e(TAG, "Trip not found for update, id: ${updatedTrip.id}")
        }
    }

    /**
     * Deletes a trip from the list by ID.
     */
    fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip called for id: $tripId")
        val removed = _trips.removeIf { it.id == tripId }
        if (removed) {
            Log.i(TAG, "Trip id: $tripId deleted successfully.")
        } else {
            Log.e(TAG, "Trip not found for deletion, id: $tripId")
        }
    }
}
