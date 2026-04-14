package com.example.hermes_travelapp.data.database.dao

import androidx.room.*
import com.example.hermes_travelapp.data.database.entities.TripEntity
import com.example.hermes_travelapp.data.database.entities.TripWithDays
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE user_id = :userId")
    fun getTripsByUser(userId: String): Flow<List<TripEntity>>

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripWithDays(tripId: String): Flow<TripWithDays?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)
}
