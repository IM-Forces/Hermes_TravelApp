package com.example.hermes_travelapp.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TripWithDays(
    @Embedded
    val trip: TripEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "trip_id"
    )
    val days: List<TripDayEntity>
)
