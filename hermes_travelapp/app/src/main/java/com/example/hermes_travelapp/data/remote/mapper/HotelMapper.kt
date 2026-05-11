package com.example.hermes_travelapp.data.remote.mapper

import com.example.hermes_travelapp.data.remote.dto.HotelDto
import com.example.hermes_travelapp.data.remote.dto.ReservationDto
import com.example.hermes_travelapp.data.remote.dto.RoomDto
import com.example.hermes_travelapp.domain.model.Hotel
import com.example.hermes_travelapp.domain.model.HotelReservation
import com.example.hermes_travelapp.domain.model.HotelRoom

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = id,
        name = name,
        address = address,
        rating = rating,
        imageUrl = imageUrl,
        rooms = rooms.map { it.toDomain() }
    )
}

fun RoomDto.toDomain(): HotelRoom {
    return HotelRoom(
        id = id,
        roomType = roomType,
        price = price,
        images = images
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
