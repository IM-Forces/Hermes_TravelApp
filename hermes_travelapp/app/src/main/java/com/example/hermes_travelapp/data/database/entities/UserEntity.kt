package com.example.hermes_travelapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val profileInitials: String,
    val activeTripCount: Int,
    val countriesVisited: Int
)
