package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.data.database.mapper.toDomain
import com.example.hermes_travelapp.data.database.mapper.toEntity
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the [TripRepository] interface.
 * This class acts as a bridge between the domain layer and the data source,
 * delegating all operations to [TripDao].
 */
@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao
) : TripRepository {

    private companion object {
        const val TAG = "TripRepository"
    }

    // TripRepositoryImpl.kt
    override fun getTrips(): Flow<List<Trip>> {
        Log.d(TAG, "getTrips called: fetching all trips from database.")
        return tripDao.getTripsByUser("default_user")
            .map { list ->
                Log.d(TAG, "getTrips returned ${list.size} trips.")
                list.map { it.toDomain() }
            }
    }

    override suspend fun addTrip(trip: Trip) {
        Log.d(TAG, "addTrip called with title='${trip.title}', id=${trip.id}")
        tripDao.insertTrip(trip.toEntity("default_user"))
        Log.i(TAG, "addTrip successful: trip '${trip.title}' added.")
    }

    override suspend fun editTrip(trip: Trip) {
        Log.d(TAG, "editTrip called for id=${trip.id}")
        tripDao.updateTrip(trip.toEntity("default_user"))
        Log.i(TAG, "editTrip successful: trip id=${trip.id} updated.")
    }

    override suspend fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip called for tripId=$tripId")
        tripDao.deleteTripById(tripId)
        Log.i(TAG, "deleteTrip successful: tripId=$tripId removed.")
    }
}
