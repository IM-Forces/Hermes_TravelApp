package com.example.hermes_travelapp.di

import com.example.hermes_travelapp.data.repository.ActivityRepositoryImpl
import com.example.hermes_travelapp.data.repository.AuthRepositoryImpl
import com.example.hermes_travelapp.data.repository.TripDayRepositoryImpl
import com.example.hermes_travelapp.data.repository.TripRepositoryImpl
import com.example.hermes_travelapp.domain.repository.ActivityRepository
import com.example.hermes_travelapp.domain.repository.AuthRepository
import com.example.hermes_travelapp.domain.repository.TripDayRepository
import com.example.hermes_travelapp.domain.repository.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(
        tripRepositoryImpl: TripRepositoryImpl
    ): TripRepository

    @Binds
    @Singleton
    abstract fun bindTripDayRepository(
        tripDayRepositoryImpl: TripDayRepositoryImpl
    ): TripDayRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        activityRepositoryImpl: ActivityRepositoryImpl
    ): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
