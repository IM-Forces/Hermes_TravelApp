package com.example.hermes_travelapp.domain

import android.util.Patterns
import com.example.hermes_travelapp.domain.model.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object ValidationUtils {
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Valida si un email tiene un formato correcto.
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida los campos obligatorios de un viaje.
     */
    fun validateTripFields(trip: Trip): String? {
        if (trip.title.isBlank() || trip.startDate.isBlank() || 
            trip.endDate.isBlank() || trip.description.isBlank()) {
            return "Todos los campos obligatorios deben estar rellenos"
        }
        return null
    }

    /**
     * Valida las fechas de un viaje.
     */
    fun validateTripDates(startDateStr: String, endDateStr: String): String? {
        return try {
            val start = LocalDate.parse(startDateStr, DATE_FORMATTER)
            val end = LocalDate.parse(endDateStr, DATE_FORMATTER)
            val today = LocalDate.now()

            if (start.isBefore(today)) {
                return "La fecha de inicio no puede estar en el pasado"
            }
            if (!start.isBefore(end)) {
                return "La fecha de inicio debe ser anterior a la de fin"
            }
            null
        } catch (e: DateTimeParseException) {
            "Formato de fecha inválido. Usa DD/MM/YYYY"
        }
    }

    /**
     * Valida que la fecha de una actividad esté dentro del rango del viaje.
     */
    fun validateActivityDate(activityDate: LocalDate, tripStartDateStr: String, tripEndDateStr: String): String? {
        return try {
            val tripStart = LocalDate.parse(tripStartDateStr, DATE_FORMATTER)
            val tripEnd = LocalDate.parse(tripEndDateStr, DATE_FORMATTER)

            if (activityDate.isBefore(tripStart) || activityDate.isAfter(tripEnd)) {
                return "La fecha de la actividad debe estar dentro del rango del viaje"
            }
            null
        } catch (e: DateTimeParseException) {
            "Formato de fecha del viaje inválido"
        }
    }
}
