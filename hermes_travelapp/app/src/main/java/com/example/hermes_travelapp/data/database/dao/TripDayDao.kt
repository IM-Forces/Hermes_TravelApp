package com.example.hermes_travelapp.data.database.dao

import androidx.room.*
import com.example.hermes_travelapp.data.database.entities.TripDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDayDao {
    @Query("SELECT * FROM trip_days WHERE trip_id = :tripId ORDER BY dayNumber ASC")
    fun getDaysForTrip(tripId: String): Flow<List<TripDayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripDay(tripDay: TripDayEntity)

    @Query("DELETE FROM trip_days WHERE id = :dayId")
    suspend fun deleteDayById(dayId: String)

    @Query("DELETE FROM trip_days WHERE trip_id = :tripId")
    suspend fun deleteDaysByTripId(tripId: String)

    @Query("SELECT * FROM trip_days WHERE trip_id = :tripId ORDER BY dayNumber DESC LIMIT 1")
    suspend fun getLastDayForTrip(tripId: String): TripDayEntity?
}
