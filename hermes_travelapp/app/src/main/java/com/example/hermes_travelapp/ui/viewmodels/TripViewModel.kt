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
        Log.d(TAG, "loadTrips: initiating trip loading from repository")
        val result = repository.getTrips()
        _trips.value = result
        Log.d(TAG, "Trip list updated, total: ${_trips.value.size}")
        Log.i(TAG, "loadTrips: successfully loaded ${result.size} trips")
    }

    /**
     * Validates trip dates and adds the trip if valid.
     */
    fun addTrip(trip: Trip): Boolean {
        Log.d(TAG, "addTrip: attempting to add trip '${trip.title}'")
        if (validateTrip(trip)) {
            repository.addTrip(trip)
            _errorMessage.value = null
            loadTrips()
            Log.i(TAG, "Trip created successfully: ${trip.title} (id=${trip.id})")
            return true
        }
        return false
    }

    /**
     * Validates trip dates and updates the trip if valid.
     */
    fun editTrip(updatedTrip: Trip): Boolean {
        Log.d(TAG, "editTrip: attempting to edit trip id=${updatedTrip.id}")
        if (validateTrip(updatedTrip)) {
            repository.editTrip(updatedTrip)
            _errorMessage.value = null
            loadTrips()
            Log.i(TAG, "Trip updated successfully: ${updatedTrip.title} (id=${updatedTrip.id})")
            return true
        }
        return false
    }

    fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip: attempting to delete trip id=$tripId")
        repository.deleteTrip(tripId)
        loadTrips()
        Log.i(TAG, "Trip deleted successfully: id=$tripId")
    }

    fun updateTripEndDate(tripId: String, newEndDate: String) {
        Log.d(TAG, "updateTripEndDate: updating end date for trip id=$tripId to $newEndDate")
        val trip = _trips.value.find { it.id == tripId } ?: return
        val updatedTrip = trip.copy(endDate = newEndDate)
        repository.editTrip(updatedTrip)
        loadTrips()
        Log.i(TAG, "Trip end date updated successfully for trip id=$tripId")
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
        if (trip.title.isBlank()) {
            val error = "El título del viaje es obligatorio"
            _errorMessage.value = error
            Log.e(TAG, "Validation failed: title is blank")
            return false
        }

        if (trip.startDate.isBlank() || trip.endDate.isBlank()) {
            val error = "Ambas fechas son obligatorias"
            _errorMessage.value = error
            Log.e(TAG, "Validation failed: missing dates (startDate='${trip.startDate}', endDate='${trip.endDate}')")
            return false
        }

        try {
            val start = LocalDate.parse(trip.startDate, DATE_FORMATTER)
            val end = LocalDate.parse(trip.endDate, DATE_FORMATTER)

            if (!start.isBefore(end)) {
                val error = "La fecha de inicio debe ser anterior a la de fin"
                _errorMessage.value = error
                Log.e(TAG, "Validation failed: startDate ($start) is not before endDate ($end)")
                return false
            }
        } catch (e: DateTimeParseException) {
            val error = "Formato de fecha inválido. Usa DD/MM/YYYY"
            _errorMessage.value = error
            Log.e(TAG, "Validation failed: date parsing failed: ${e.message}")
            return false
        }

        return true
    }
}
