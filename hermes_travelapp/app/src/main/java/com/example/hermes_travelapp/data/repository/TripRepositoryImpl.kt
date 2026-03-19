package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.fakeDB.FakeTripDataSource
import com.example.hermes_travelapp.domain.Trip
import com.example.hermes_travelapp.domain.TripRepository

/**
 * Implementation of the [TripRepository] interface.
 * This class acts as a bridge between the domain layer and the data source,
 * delegating all operations to [FakeTripDataSource].
 */
class TripRepositoryImpl : TripRepository {

    private companion object {
        const val TAG = "TripRepositoryImpl"
    }

    override fun getTrips(): List<Trip> {
        Log.d(TAG, "getTrips: fetching all trips")
        return FakeTripDataSource.getTrips()
    }

    override fun addTrip(trip: Trip) {
        Log.d(TAG, "addTrip: tripId=${trip.id}")
        FakeTripDataSource.addTrip(trip)
    }

    override fun editTrip(trip: Trip) {
        Log.d(TAG, "editTrip: tripId=${trip.id}")
        FakeTripDataSource.updateTrip(trip)
    }

    override fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip: tripId=$tripId")
        FakeTripDataSource.deleteTrip(tripId)
    }
}
