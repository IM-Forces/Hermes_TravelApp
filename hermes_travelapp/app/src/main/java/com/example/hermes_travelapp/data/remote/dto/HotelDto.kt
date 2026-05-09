package com.example.hermes_travelapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Envoltorio para la respuesta de la lista de reservas.
 */
data class ReservationListDto(
    @SerializedName("reservations")
    val reservations: List<ReservationDto>
)

/**
 * DTO que representa un Hotel.
 */
data class HotelDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("rooms")
    val rooms: List<RoomDto>? = null
)

/**
 * DTO que representa una Habitación.
 */
data class RoomDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("room_type")
    val roomType: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("images")
    val images: List<String>
)

/**
 * DTO de una Reserva, incluyendo hotel y habitación anidados.
 */
data class ReservationDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("hotel_id")
    val hotelId: String,
    @SerializedName("room_id")
    val roomId: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("guest_name")
    val guestName: String,
    @SerializedName("guest_email")
    val guestEmail: String,
    @SerializedName("hotel")
    val hotel: HotelDto,
    @SerializedName("room")
    val room: RoomDto
)

/**
 * DTO para la petición de reserva.
 */
data class ReserveRequestDto(
    @SerializedName("hotel_id")
    val hotelId: String,
    @SerializedName("room_id")
    val roomId: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("guest_name")
    val guestName: String,
    @SerializedName("guest_email")
    val guestEmail: String
)

data class CancelRequestDto(
    @SerializedName("reservation_id")
    val reservationId: String
)
