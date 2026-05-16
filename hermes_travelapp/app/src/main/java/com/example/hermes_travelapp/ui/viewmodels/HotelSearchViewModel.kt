package com.example.hermes_travelapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.domain.model.Hotel
import com.example.hermes_travelapp.domain.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HotelSearchViewModel @Inject constructor(
    private val repository: HotelRepository
) : ViewModel() {

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city.asStateFlow()

    private val _startDate = MutableStateFlow("")
    val startDate: StateFlow<String> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow("")
    val endDate: StateFlow<String> = _endDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cityError = MutableStateFlow<String?>(null)
    val cityError: StateFlow<String?> = _cityError.asStateFlow()

    private val _startDateError = MutableStateFlow<String?>(null)
    val startDateError: StateFlow<String?> = _startDateError.asStateFlow()

    private val _endDateError = MutableStateFlow<String?>(null)
    val endDateError: StateFlow<String?> = _endDateError.asStateFlow()

    private val _maxPrice = MutableStateFlow(500f)
    val maxPrice: StateFlow<Float> = _maxPrice.asStateFlow()

    private val _stars = MutableStateFlow(0)
    val stars: StateFlow<Int> = _stars.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Hotel>>(emptyList())
    val searchResults: StateFlow<List<Hotel>> = _searchResults.asStateFlow()

    fun onCitySelected(city: String) {
        _city.value = city
        _cityError.value = null
    }

    fun onStartDateSelected(date: String) {
        _startDate.value = date
        _startDateError.value = null
        _endDateError.value = null
    }

    fun onEndDateSelected(date: String) {
        _endDate.value = date
        _endDateError.value = null
    }

    fun onMaxPriceChanged(price: Float) {
        _maxPrice.value = price
    }

    fun onStarsChanged(stars: Int) {
        _stars.value = stars
    }

    fun searchHotels(onSuccess: () -> Unit = {}) {
        val currentCity = _city.value
        val start = _startDate.value
        val end = _endDate.value

        if (!validate(currentCity, start, end)) return

        _isLoading.value = true
        _error.value = null

        // Convert dd/MM/yyyy to yyyy-MM-dd for the API
        val apiStartDate = convertDateFormat(start)
        val apiEndDate = convertDateFormat(end)

        viewModelScope.launch {
            repository.checkAvailability(
                groupId = "G03",
                city = currentCity,
                startDate = apiStartDate,
                endDate = apiEndDate
            ).onSuccess { hotels ->
                _searchResults.value = hotels
                _isLoading.value = false
                onSuccess()
            }.onFailure { e ->
                _error.value = e.message ?: "Error al buscar hoteles"
                _isLoading.value = false
            }
        }
    }

    private fun convertDateFormat(date: String): String {
        return try {
            val inputSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateObj = inputSdf.parse(date)
            if (dateObj != null) outputSdf.format(dateObj) else date
        } catch (e: Exception) {
            date
        }
    }

    private fun validate(city: String, start: String, end: String): Boolean {
        var isValid = true
        _cityError.value = null
        _startDateError.value = null
        _endDateError.value = null

        if (city.isBlank()) {
            _cityError.value = "La ciudad es obligatoria"
            isValid = false
        }
        
        if (start.isBlank()) {
            _startDateError.value = "La fecha de entrada es obligatoria"
            isValid = false
        }
        
        if (end.isBlank()) {
            _endDateError.value = "La fecha de salida es obligatoria"
            isValid = false
        }

        if (start.isNotBlank() && end.isNotBlank()) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val startDateObj = sdf.parse(start)
                val endDateObj = sdf.parse(end)

                if (startDateObj != null && endDateObj != null && !startDateObj.before(endDateObj)) {
                    _endDateError.value = "La fecha de entrada debe ser anterior a la de salida"
                    isValid = false
                }
            } catch (e: Exception) {
                _endDateError.value = "Formato de fecha inválido"
                isValid = false
            }
        }

        return isValid
    }

    fun clearError() {
        _error.value = null
        _cityError.value = null
        _startDateError.value = null
        _endDateError.value = null
    }
}
