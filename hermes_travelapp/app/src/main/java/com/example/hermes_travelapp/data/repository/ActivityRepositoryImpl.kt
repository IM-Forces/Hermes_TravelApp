package com.example.hermes_travelapp.data.repository

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

    override fun getActivitiesForDay(dayId: String): Flow<List<ItineraryItem>> {
        return itineraryItemDao.getActivitiesForDay(dayId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addActivity(activity: ItineraryItem) {
        itineraryItemDao.insertActivity(activity.toEntity())
    }

    override suspend fun updateActivity(activity: ItineraryItem) {
        itineraryItemDao.updateActivity(activity.toEntity())
    }

    override suspend fun deleteActivity(activityId: String) {
        itineraryItemDao.deleteActivityById(activityId)
    }
}
