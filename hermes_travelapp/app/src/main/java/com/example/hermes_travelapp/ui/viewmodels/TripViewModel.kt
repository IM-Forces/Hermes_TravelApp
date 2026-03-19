package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hermes_travelapp.domain.Trip
import com.example.hermes_travelapp.domain.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TripViewModel(private val repository: TripRepository) : ViewModel() {
    
    private companion object {
        const val TAG = "TripViewModel"
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    /**
     * Observable stream of all trips.
     */
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    init {
        loadTrips()
    }

    /**
     * Loads trips from the repository and updates the [trips] StateFlow.
     */
    fun loadTrips() {
        _trips.value = repository.getTrips()
        Log.d(TAG, "loadTrips: loaded ${_trips.value.size} trips")
    }

    /**
     * Validates trip dates and adds the trip if valid.
     */
    fun addTrip(trip: Trip): Boolean {
        if (validateTrip(trip)) {
            repository.addTrip(trip)
            _errorMessage.value = null
            loadTrips()
            return true
        }
        return false
    }

    /**
     * Validates trip dates and updates the trip if valid.
     */
    fun editTrip(updatedTrip: Trip): Boolean {
        if (validateTrip(updatedTrip)) {
            repository.editTrip(updatedTrip)
            _errorMessage.value = null
            loadTrips()
            return true
        }
        return false
    }

    fun deleteTrip(tripId: String) {
        repository.deleteTrip(tripId)
        loadTrips()
    }

    /**
     * Clears any active error messages.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Core validation logic for trips.
     * Checks for mandatory dates and ensures start date is before end date.
     */
    private fun validateTrip(trip: Trip): Boolean {
        // Check for empty/null dates (since they are Strings in the domain model)
        if (trip.startDate.isBlank() || trip.endDate.isBlank()) {
            val error = "Ambas fechas son obligatorias"
            _errorMessage.value = error
            Log.e(TAG, error)
            return false
        }

        try {
            val start = LocalDate.parse(trip.startDate, DATE_FORMATTER)
            val end = LocalDate.parse(trip.endDate, DATE_FORMATTER)

            if (!start.isBefore(end)) {
                val error = "La fecha de inicio debe ser anterior a la de fin"
                _errorMessage.value = error
                Log.e(TAG, error)
                return false
            }
        } catch (e: DateTimeParseException) {
            val error = "Formato de fecha inválido. Usa DD/MM/YYYY"
            _errorMessage.value = error
            Log.e(TAG, "$error: ${e.message}")
            return false
        }

        return true
    }
}
