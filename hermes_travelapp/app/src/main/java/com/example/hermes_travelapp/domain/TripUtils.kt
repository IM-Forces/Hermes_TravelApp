package com.example.hermes_travelapp.domain

import android.util.Log
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.model.TripDay
import com.example.hermes_travelapp.domain.repository.TripDayRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Generates all [TripDay] entries for a given [Trip] based on its start and end dates.
 * Existing days for the trip are cleared before generation to ensure a clean state.
 */
suspend fun generateDaysForTrip(trip: Trip, repository: TripDayRepository) {
    repository.clearDaysForTrip(trip.id)

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    try {
        val start = LocalDate.parse(trip.startDate, formatter)
        val end = LocalDate.parse(trip.endDate, formatter)

        if (start.isAfter(end)) {
            Log.w("TripUtils", "generateDaysForTrip: Start date is after end date for trip ${trip.id}")
            return
        }

        var currentDate = start
        var dayNumber = 1

        while (!currentDate.isAfter(end)) {
            val tripDay = TripDay(
                id = UUID.randomUUID().toString(),
                tripId = trip.id,
                dayNumber = dayNumber,
                date = currentDate
            )

            repository.addDay(tripDay)

            currentDate = currentDate.plusDays(1)
            dayNumber++
        }

        Log.d("TripUtils", "generateDaysForTrip: Successfully generated ${dayNumber - 1} days for trip ${trip.id}")

    } catch (e: Exception) {
        Log.e("TripUtils", "generateDaysForTrip: Error generating days for trip ${trip.id}: ${e.message}")
    }
}
