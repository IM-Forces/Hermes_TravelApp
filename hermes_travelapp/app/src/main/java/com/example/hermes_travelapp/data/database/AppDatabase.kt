package com.example.hermes_travelapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.data.database.entities.ItineraryItemEntity
import com.example.hermes_travelapp.data.database.entities.TripDayEntity
import com.example.hermes_travelapp.data.database.entities.TripEntity
import com.example.hermes_travelapp.data.database.entities.UserEntity

@Database(
    entities = [
        TripEntity::class,
        TripDayEntity::class,
        UserEntity::class,
        ItineraryItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
