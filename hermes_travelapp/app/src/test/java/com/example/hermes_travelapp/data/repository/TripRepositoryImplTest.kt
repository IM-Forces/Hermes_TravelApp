package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.AuthRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class TripRepositoryImplTest {

    private lateinit var repository: TripRepositoryImpl
    private val tripDao: TripDao = mockk()
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        repository = TripRepositoryImpl(tripDao, authRepository)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test(expected = IllegalStateException::class)
    fun `addTrip throws exception if title already exists`() = runBlocking {
        // Arrange
        val trip = Trip(
            title = "Viaje Repetido",
            startDate = "2024-01-01",
            endDate = "2024-01-10",
            description = "Test Description"
        )
        coEvery { authRepository.getCurrentUserId() } returns "user123"
        coEvery { tripDao.existsByTitle("user123", "Viaje Repetido") } returns true

        // Act
        repository.addTrip(trip)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addTrip throws exception if dates are inconsistent`() = runBlocking {
        // Arrange
        val trip = Trip(
            title = "Viaje Mal",
            startDate = "2024-05-10",
            endDate = "2024-05-01", // Fecha fin antes que inicio
            description = "Test Description"
        )
        coEvery { authRepository.getCurrentUserId() } returns "user123"
        coEvery { tripDao.existsByTitle("user123", "Viaje Mal") } returns false

        // Act
        repository.addTrip(trip)
    }
}
