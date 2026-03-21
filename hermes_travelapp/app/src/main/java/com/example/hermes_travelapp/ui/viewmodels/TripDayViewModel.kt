package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hermes_travelapp.domain.TripDay
import com.example.hermes_travelapp.domain.TripDayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing the state and business logic of trip days.
 * Follows the same pattern as ActivityViewModel.
 */
class TripDayViewModel(private val repository: TripDayRepository) : ViewModel() {

    private companion object {
        const val TAG = "TripDayViewModel"
    }

    private val _tripDays = MutableStateFlow<List<TripDay>>(emptyList())
    /**
     * Observable stream of trip days for the currently selected trip.
     */
    val tripDays: StateFlow<List<TripDay>> = _tripDays.asStateFlow()

    /**
     * Loads days for a specific trip and updates the [tripDays] StateFlow.
     * @param tripId The unique identifier of the trip.
     */
    fun loadDaysForTrip(tripId: String) {
        val result = repository.getDaysForTrip(tripId)
        _tripDays.value = result
        Log.d(TAG, "loadDaysForTrip: loaded ${result.size} days for trip $tripId")
    }

    fun addDay(tripId: String, onUpdateEndDate: (String) -> Unit) {
        val lastDay = repository.getLastDayForTrip(tripId)
        val newDayNumber = (lastDay?.dayNumber ?: 0) + 1
        val newDate = lastDay?.date?.plusDays(1) ?: java.time.LocalDate.now()
        val newDay = TripDay(
            id = java.util.UUID.randomUUID().toString(),
            tripId = tripId,
            dayNumber = newDayNumber,
            date = newDate
        )
        repository.addDay(newDay)
        loadDaysForTrip(tripId)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
        onUpdateEndDate(newDate.format(formatter))
        Log.d(TAG, "addDay: added day $newDayNumber for trip $tripId")
    }

    fun deleteDay(dayId: String, tripId: String, tripStartDate: String, onUpdateEndDate: (String) -> Unit) {
        repository.deleteDay(dayId)

        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val startDate = java.time.LocalDate.parse(tripStartDate, formatter)

        // Reordenar todos los días restantes
        val remainingDays = repository.getDaysForTrip(tripId)
        repository.clearDaysForTrip(tripId)

        remainingDays.forEachIndexed { index, day ->
            repository.addDay(
                day.copy(
                    dayNumber = index + 1,
                    date = startDate.plusDays(index.toLong())
                )
            )
        }

        // Actualizar fecha fin con el último día
        val lastDay = repository.getLastDayForTrip(tripId)
        if (lastDay != null) {
            onUpdateEndDate(lastDay.date.format(formatter))
        }

        loadDaysForTrip(tripId)
        Log.d(TAG, "deleteDay: deleted day $dayId, reordered ${remainingDays.size} days for trip $tripId")
    }
}
