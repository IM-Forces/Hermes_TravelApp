package com.example.hermes_travelapp.data.database.dao

import androidx.room.*
import com.example.hermes_travelapp.data.database.entities.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryItemDao {
    @Query("SELECT * FROM itinerary_items WHERE day_id = :dayId ORDER BY time ASC")
    fun getActivitiesForDay(dayId: String): Flow<List<ItineraryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ItineraryItemEntity)

    @Update
    suspend fun updateActivity(activity: ItineraryItemEntity)

    @Delete
    suspend fun deleteActivity(activity: ItineraryItemEntity)

    @Query("DELETE FROM itinerary_items WHERE id = :activityId")
    suspend fun deleteActivityById(activityId: String)
}
