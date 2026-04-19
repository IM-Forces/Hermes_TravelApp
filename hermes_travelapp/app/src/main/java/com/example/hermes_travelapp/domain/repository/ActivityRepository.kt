package com.example.hermes_travelapp.domain.repository

import com.example.hermes_travelapp.domain.model.ItineraryItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for managing itinerary activities.
 */
interface ActivityRepository {
    
    /**
     * Retrieves all activities scheduled for a specific day.
     *
     * @param dayId The ID of the specific day.
     * @return A flow emitting a list of [ItineraryItem] objects, typically sorted by time.
     */
    fun getActivitiesForDay(dayId: String): Flow<List<ItineraryItem>>

    /**
     * Adds a new activity to the itinerary.
     */
    suspend fun addActivity(activity: ItineraryItem)

    /**
     * Updates an existing activity's information.
     */
    suspend fun updateActivity(activity: ItineraryItem)

    /**
     * Removes an activity from the itinerary.
     */
    suspend fun deleteActivity(activityId: String)
}
