package com.example.hermes_travelapp.data.remote.mapper

import com.example.hermes_travelapp.data.remote.dto.HotelDto
import com.example.hermes_travelapp.data.remote.dto.ReservationDto
import com.example.hermes_travelapp.data.remote.dto.RoomDto
import com.example.hermes_travelapp.domain.model.Hotel
import com.example.hermes_travelapp.domain.model.HotelReservation
import com.example.hermes_travelapp.domain.model.HotelRoom

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = id ?: "",
        name = name ?: "Hotel sin nombre",
        address = address ?: "Dirección no disponible",
        rating = rating ?: 0,
        imageUrl = imageUrl ?: "",
        rooms = rooms?.map { it.toDomain() } ?: emptyList()
    )
}

fun RoomDto.toDomain(): HotelRoom {
    return HotelRoom(
        id = id ?: "",
        roomType = roomType ?: "Estándar",
        price = price ?: 0.0,
        images = images ?: emptyList()
    )
}

fun ReservationDto.toDomain(): HotelReservation {
    return HotelReservation(
        id = id,
        hotelId = hotelId,
        roomId = roomId,
        startDate = startDate,
        endDate = endDate,
        guestName = guestName,
        guestEmail = guestEmail,
        hotel = hotel?.toDomain(),
        room = room?.toDomain()
    )
}
