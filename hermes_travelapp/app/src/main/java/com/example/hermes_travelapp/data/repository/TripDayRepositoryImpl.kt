package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.database.dao.TripDayDao
import com.example.hermes_travelapp.data.database.mapper.toDomain
import com.example.hermes_travelapp.data.database.mapper.toEntity
import com.example.hermes_travelapp.domain.model.TripDay
import com.example.hermes_travelapp.domain.repository.TripDayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the [TripDayRepository] interface using Room database.
 */
class TripDayRepositoryImpl @Inject constructor(
    private val tripDayDao: TripDayDao
) : TripDayRepository {

    private companion object {
        const val TAG = "TripDayRepositoryImpl"
    }

    override fun getDaysForTrip(tripId: String): Flow<List<TripDay>> {
        Log.d(TAG, "getDaysForTrip: tripId=$tripId")
        return tripDayDao.getDaysForTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addDay(day: TripDay) {
        Log.d(TAG, "addDay: dayId=${day.id}, tripId=${day.tripId}")
        tripDayDao.insertTripDay(day.toEntity())
    }

    override suspend fun clearDaysForTrip(tripId: String) {
        Log.d(TAG, "clearDaysForTrip: tripId=$tripId")
        tripDayDao.deleteDaysByTripId(tripId)
    }

    override suspend fun getLastDayForTrip(tripId: String): TripDay? {
        Log.d(TAG, "getLastDayForTrip: tripId=$tripId")
        return tripDayDao.getLastDayForTrip(tripId)?.toDomain()
    }

    override suspend fun deleteDay(dayId: String) {
        Log.d(TAG, "deleteDay: dayId=$dayId")
        tripDayDao.deleteDayById(dayId)
    }
}
