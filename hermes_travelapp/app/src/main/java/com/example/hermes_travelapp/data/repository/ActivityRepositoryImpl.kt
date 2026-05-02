package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.database.dao.ItineraryItemDao
import com.example.hermes_travelapp.data.database.mapper.toDomain
import com.example.hermes_travelapp.data.database.mapper.toEntity
import com.example.hermes_travelapp.domain.model.ItineraryItem
import com.example.hermes_travelapp.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val itineraryItemDao: ItineraryItemDao
) : ActivityRepository {

    private companion object {
        const val TAG = "ActivityRepositoryImpl"
    }

    override fun getActivitiesForDay(dayId: String): Flow<List<ItineraryItem>> {
        Log.d(TAG, "getActivitiesForDay: dayId=$dayId")
        return itineraryItemDao.getActivitiesForDay(dayId).map { entities ->
            entities.map { it.toDomain() }
        }.also {
            Log.i(TAG, "getActivitiesForDay: Flow created successfully for dayId=$dayId")
        }
    }

    override suspend fun addActivity(activity: ItineraryItem) {
        try {
            Log.d(TAG, "addActivity: activityId=${activity.id}")
            itineraryItemDao.insertActivity(activity.toEntity())
            Log.i(TAG, "addActivity successful: activityId=${activity.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding activity: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateActivity(activity: ItineraryItem) {
        try {
            Log.d(TAG, "updateActivity: activityId=${activity.id}")
            itineraryItemDao.updateActivity(activity.toEntity())
            Log.i(TAG, "updateActivity successful: activityId=${activity.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating activity: ${e.message}", e)
            throw e
        }
    }

    override suspend fun deleteActivity(activityId: String) {
        try {
            Log.d(TAG, "deleteActivity: activityId=$activityId")
            itineraryItemDao.deleteActivityById(activityId)
            Log.i(TAG, "deleteActivity successful: activityId=$activityId")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting activity: ${e.message}", e)
            throw e
        }
    }
}
