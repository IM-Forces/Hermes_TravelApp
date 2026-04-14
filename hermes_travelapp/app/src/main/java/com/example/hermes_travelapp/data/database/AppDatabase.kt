package com.example.hermes_travelapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hermes_travelapp.domain.model.Trip

@Database(entities = [Trip::class], version = 1, exportSchema = false)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    // DAOs will be added here
}
