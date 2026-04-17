package com.example.hermes_travelapp.data.database.mapper

import com.example.hermes_travelapp.data.database.entities.ItineraryItemEntity
import com.example.hermes_travelapp.domain.model.ItineraryItem

fun ItineraryItemEntity.toDomain(): ItineraryItem {
    return ItineraryItem(
        id = id,
        tripId = tripId,
        dayId = dayId,
        title = title,
        description = description,
        date = date,
        time = time,
        location = location,
        cost = cost
    )
}

fun ItineraryItem.toEntity(): ItineraryItemEntity {
    return ItineraryItemEntity(
        id = id,
        tripId = tripId,
        dayId = dayId,
        title = title,
        description = description,
        date = date,
        time = time,
        location = location,
        cost = cost
    )
}
