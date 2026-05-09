package com.example.hermes_travelapp.data.repository

import com.example.hermes_travelapp.data.remote.api.HotelApiService
import com.example.hermes_travelapp.data.remote.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HotelRepositoryImplTest {

    private lateinit var repository: HotelRepositoryImpl
    private val apiService: HotelApiService = mockk()

    @Before
    fun setUp() {
        repository = HotelRepositoryImpl(apiService)
    }

    @Test
    fun `getHotels returns success when API call is successful`() = runBlocking {
        val mockHotelsDto = listOf(
            HotelDto("H1", "Hotel Test", "Address", 5, "url", emptyList())
        )
        coEvery { apiService.getHotels("G03") } returns mockHotelsDto

        val result = repository.getHotels("G03")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Hotel Test", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `checkAvailability returns hotels when successful`() = runBlocking {
        val mockHotelsDto = listOf(
            HotelDto("H1", "Hotel Available", "Address", 4, "url", emptyList())
        )
        coEvery { 
            apiService.checkAvailability("G03", any(), any(), "2026-05-20", "2026-05-22") 
        } returns mockHotelsDto

        val result = repository.checkAvailability("G03", "BCN", "H1", "2026-05-20", "2026-05-22")

        assertTrue(result.isSuccess)
        assertEquals("Hotel Available", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `reserveRoom returns success and maps correctly`() = runBlocking {
        val reservationDto = ReservationDto(
            id = "R1", hotelId = "H1", roomId = "RM1",
            startDate = "2026-05-20", endDate = "2026-05-22",
            guestName = "Ivan Gil", guestEmail = "ivan@example.com"
        )
        val mockResponse = ReserveResponseDto(
            message = "Reservation confirmed",
            nights = 2,
            reservation = reservationDto
        )
        
        coEvery { apiService.reserveRoom(any(), any()) } returns mockResponse

        val result = repository.reserveRoom(
            groupId = "G03", hotelId = "H1", roomId = "RM1",
            startDate = "2026-05-20", endDate = "2026-05-22",
            guestName = "Ivan Gil", guestEmail = "ivan@example.com"
        )

        assertTrue(result.isSuccess)
        assertEquals("R1", result.getOrNull()?.id)
        assertEquals("Ivan Gil", result.getOrNull()?.guestName)
    }

    @Test
    fun `getGroupReservations returns reservations when successful`() = runBlocking {
        val reservations = listOf(
            ReservationDto("R1", "H1", "RM1", "2026-05-20", "2026-05-22", "Marco", "marco@test.com")
        )
        coEvery { apiService.getGroupReservations("G03", any()) } returns ReservationListDto(reservations)

        val result = repository.getGroupReservations("G03", "marco@test.com")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Marco", result.getOrNull()?.first()?.guestName)
    }

    @Test
    fun `deleteReservation returns success when item is removed`() = runBlocking {
        val reservationDto = ReservationDto("R1", "H1", "RM1", "2026-05-20", "2026-05-22", "Ivan", "ivan@test.com")
        coEvery { apiService.deleteReservation("R1") } returns reservationDto

        val result = repository.deleteReservation("R1")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `repository returns failure when API throws exception`() = runBlocking {
        coEvery { apiService.getHotels(any()) } throws Exception("API Error")

        val result = repository.getHotels("G03")

        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }
}
