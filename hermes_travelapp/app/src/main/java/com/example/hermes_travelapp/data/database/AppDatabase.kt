package com.example.hermes_travelapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hermes_database"
                )
                .addTypeConverter(AppTypeConverters())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
