package com.example.hermes_travelapp.di

import android.content.Context
import androidx.room.Room
import com.example.hermes_travelapp.data.database.AppDatabase
import com.example.hermes_travelapp.data.database.AppTypeConverters
import com.example.hermes_travelapp.data.database.dao.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppTypeConverters(): AppTypeConverters {
        return AppTypeConverters()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        typeConverters: AppTypeConverters
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hermes_database"
        )
        .addTypeConverter(typeConverters)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideTripDao(database: AppDatabase): TripDao {
        return database.tripDao()
    }
}
