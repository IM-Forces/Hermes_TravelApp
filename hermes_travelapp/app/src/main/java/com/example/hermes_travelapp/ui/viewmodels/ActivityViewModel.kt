package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.domain.model.ItineraryItem
import com.example.hermes_travelapp.domain.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the state and business logic of activities within a trip.
 * This class follows the MVVM pattern by interacting with the Repository layer.
 */
@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repository: ActivityRepository
) : ViewModel() {

    private companion object {
        const val TAG = "ActivityViewModel"
    }

    private val _activities = MutableStateFlow<List<ItineraryItem>>(emptyList())
    /**
     * Observable stream of activities for the currently selected day.
     */
    val activities: StateFlow<List<ItineraryItem>> = _activities.asStateFlow()

    private val _dayCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    /**
     * Observable stream of activity counts for each day ID.
     */
    val dayCounts: StateFlow<Map<String, Int>> = _dayCounts.asStateFlow()

    private var loadActivitiesJob: Job? = null

    /**
     * Loads activities for a specific day and updates the [activities] StateFlow.
     * @param dayId The identifier of the day within the trip.
     */
    fun loadActivitiesForDay(dayId: String) {
        loadActivitiesJob?.cancel()
        loadActivitiesJob = viewModelScope.launch {
            repository.getActivitiesForDay(dayId).collect { result ->
                _activities.value = result
                Log.d(TAG, "loadActivitiesForDay: collected ${result.size} activities for day=$dayId")
                
                // Update individual day count as well
                val currentCounts = _dayCounts.value.toMutableMap()
                currentCounts[dayId] = result.size
                _dayCounts.value = currentCounts
            }
        }
    }

    /**
     * Loads counts for all days of a trip.
     * @param dayIds List of all day IDs to fetch counts for.
     */
    fun loadAllDayCounts(dayIds: List<String>) {
        viewModelScope.launch {
            val counts = mutableMapOf<String, Int>()
            dayIds.forEach { dayId ->
                // Use first() to get the current list of activities once
                val currentActivities = repository.getActivitiesForDay(dayId).first()
                counts[dayId] = currentActivities.size
            }
            _dayCounts.value = counts
            Log.d(TAG, "loadAllDayCounts: loaded counts for ${dayIds.size} days")
        }
    }

    /**
     * Adds a new activity after validation.
     * @param activity The [ItineraryItem] to add.
     */
    fun addActivity(activity: ItineraryItem) {
        Log.d(TAG, "addActivity called: ${activity.title} on ${activity.date} at ${activity.time}")
        
        if (!activity.isValid()) {
            Log.e(TAG, "addActivity: validation failed for activity '${activity.title}' (ID: ${activity.id})")
            return
        }
        
        viewModelScope.launch {
            repository.addActivity(activity)
            Log.i(TAG, "addActivity: successfully added activity '${activity.title}' to trip ${activity.tripId}")
        }
    }

    /**
     * Updates an existing activity after validation.
     * @param activity The [ItineraryItem] with updated information.
     */
    fun updateActivity(activity: ItineraryItem) {
        Log.d(TAG, "updateActivity called: ${activity.title}, location: ${activity.location}, cost: ${activity.cost}")
        
        if (!activity.isValid()) {
            Log.e(TAG, "updateActivity: validation failed for activity ID ${activity.id}")
            return
        }
        
        viewModelScope.launch {
            repository.updateActivity(activity)
            Log.i(TAG, "updateActivity: successfully updated activity '${activity.title}' (ID: ${activity.id})")
        }
    }

    /**
     * Deletes an activity by ID.
     * @param activityId The unique identifier of the activity to delete.
     */
    fun deleteActivity(activityId: String) {
        Log.d(TAG, "deleteActivity called for ID: $activityId")
        viewModelScope.launch {
            repository.deleteActivity(activityId)
            Log.i(TAG, "deleteActivity: successfully deleted $activityId")
        }
    }
}
