package com.example.hermes_travelapp.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val imageUrl: String,
    val rooms: List<HotelRoom>
)

data class HotelRoom(
    val id: String,
    val roomType: String,
    val price: Double,
    val images: List<String>
)

data class HotelReservation(
    val id: String,
    val hotelId: String,
    val roomId: String,
    val startDate: String,
    val endDate: String,
    val guestName: String,
    val guestEmail: String,
    val hotel: Hotel? = null,
    val room: HotelRoom? = null
)
