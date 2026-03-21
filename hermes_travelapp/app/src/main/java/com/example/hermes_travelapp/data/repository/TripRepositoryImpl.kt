package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.fakeDB.FakeTripDataSource
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.TripRepository

/**
 * Implementation of the [TripRepository] interface.
 * This class acts as a bridge between the domain layer and the data source,
 * delegating all operations to [FakeTripDataSource].
 */
class TripRepositoryImpl : TripRepository {

    private companion object {
        const val TAG = "TripRepository"
    }

    override fun getTrips(): List<Trip> {
        Log.d(TAG, "getTrips called: fetching all trips from data source.")
        val trips = FakeTripDataSource.getTrips()
        Log.d(TAG, "getTrips returned ${trips.size} trips.")
        return trips
    }

    override fun addTrip(trip: Trip) {
        Log.d(TAG, "addTrip called with title='${trip.title}', id=${trip.id}")
        FakeTripDataSource.addTrip(trip)
        Log.i(TAG, "addTrip successful: trip '${trip.title}' added.")
    }

    override fun editTrip(trip: Trip) {
        Log.d(TAG, "editTrip called for id=${trip.id}")
        // En este repositorio Fake, delegamos la verificación al DataSource
        val oldSize = FakeTripDataSource.getTrips().size
        FakeTripDataSource.updateTrip(trip)
        
        // Verificamos si se actualizó buscando el ID (lógica simple para el log)
        if (FakeTripDataSource.getTrips().any { it.id == trip.id }) {
            Log.i(TAG, "editTrip successful: trip id=${trip.id} updated.")
        } else {
            Log.e(TAG, "editTrip failed: trip ID ${trip.id} not found in data source.")
        }
    }

    override fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip called for tripId=$tripId")
        val tripsBefore = FakeTripDataSource.getTrips().size
        FakeTripDataSource.deleteTrip(tripId)
        val tripsAfter = FakeTripDataSource.getTrips().size

        if (tripsBefore > tripsAfter) {
            Log.i(TAG, "deleteTrip successful: tripId=$tripId removed.")
        } else {
            Log.e(TAG, "deleteTrip failed: tripId=$tripId not found.")
        }
    }
}
