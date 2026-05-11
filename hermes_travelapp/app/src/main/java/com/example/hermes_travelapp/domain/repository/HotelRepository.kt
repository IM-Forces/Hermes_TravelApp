package com.example.hermes_travelapp.domain.repository

import com.example.hermes_travelapp.domain.model.Hotel
import com.example.hermes_travelapp.domain.model.HotelReservation

interface HotelRepository {
    suspend fun getHotels(groupId: String = "G03"): Result<List<Hotel>>
    
    suspend fun checkAvailability(
        groupId: String,
        city: String? = null,
        hotelId: String? = null,
        startDate: String,
        endDate: String
    ): Result<List<Hotel>>

    suspend fun reserveRoom(
        groupId: String = "G03",
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): Result<HotelReservation>

    suspend fun getGroupReservations(
        groupId: String = "G03",
        guestEmail: String? = null
    ): Result<List<HotelReservation>>

    suspend fun getReservationById(reservationId: String): Result<HotelReservation>

    suspend fun deleteReservation(reservationId: String): Result<Unit>
}
