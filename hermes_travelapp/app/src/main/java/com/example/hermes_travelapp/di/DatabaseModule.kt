package com.example.hermes_travelapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hermes_travelapp.data.database.AppDatabase
import com.example.hermes_travelapp.data.database.AppTypeConverters
import com.example.hermes_travelapp.data.database.dao.AccessLogDao
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.data.database.dao.TripDayDao
import com.example.hermes_travelapp.data.database.dao.ItineraryItemDao
import com.example.hermes_travelapp.data.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppTypeConverters(): AppTypeConverters {
        return AppTypeConverters()
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        typeConverters: AppTypeConverters
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hermes_database"
        )
            .addTypeConverter(typeConverters)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(
                        "INSERT INTO users (id, name, email, login, username, birthdate, address, country, phone, acceptEmails, profileInitials, activeTripCount, countriesVisited) " +
                                "VALUES ('default_user', 'Default User', 'default@example.com', 'default@example.com', 'default_user', 0, 'Unknown Address', 'Unknown Country', '000000000', 0, 'DU', 0, 0)"
                    )
                }
            })
            .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideTripDao(database: AppDatabase): TripDao {
        return database.tripDao()
    }

    @Provides
    @Singleton
    fun provideTripDayDao(database: AppDatabase): TripDayDao {
        return database.tripDayDao()
    }

    @Provides
    @Singleton
    fun provideItineraryItemDao(database: AppDatabase): ItineraryItemDao {
        return database.itineraryItemDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideAccessLogDao(database: AppDatabase): AccessLogDao {
        return database.accessLogDao()
    }
}
