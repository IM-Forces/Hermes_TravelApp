package com.example.hermes_travelapp.data.remote.api

import com.example.hermes_travelapp.data.remote.dto.AvailabilityResponseDto
import com.example.hermes_travelapp.data.remote.dto.HotelDto
import com.example.hermes_travelapp.data.remote.dto.HotelListDto
import com.example.hermes_travelapp.data.remote.dto.ReservationDto
import com.example.hermes_travelapp.data.remote.dto.ReservationListDto
import com.example.hermes_travelapp.data.remote.dto.ReserveRequestDto
import com.example.hermes_travelapp.data.remote.dto.ReserveResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApiService {

    /**
     * Lists all hotels for the given group.
     */
    @GET("hotels/{group_id}/hotels")
    suspend fun getHotels(
        @Path("group_id") groupId: String = "G03"
    ): HotelListDto

    /**
     * Reserves a room.
     */
    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String = "G03",
        @Body request: ReserveRequestDto
    ): ReserveResponseDto

    /**
     * Checks hotel availability.
     * groupId, startDate and endDate are mandatory.
     */
    @GET("hotels/{group_id}/availability")
    suspend fun checkAvailability(
        @Path("group_id") groupId: String,
        @Query("city") city: String? = null,
        @Query("hotel_id") hotelId: String? = null,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): AvailabilityResponseDto

    /**
     * Lists all reservations for the given group.
     */
    @GET("hotels/{group_id}/reservations")
    suspend fun getGroupReservations(
        @Path("group_id") groupId: String = "G03",
        @Query("guest_email") guestEmail: String? = null
    ): ReservationListDto

    /**
     * Gets a single reservation by its ID.
     */
    @GET("reservations/{res_id}")
    suspend fun getReservationById(
        @Path("res_id") reservationId: String
    ): ReservationDto

    /**
     * Cancels/deletes a reservation by its ID.
     */
    @DELETE("reservations/{res_id}")
    suspend fun deleteReservation(
        @Path("res_id") reservationId: String
    ): ReservationDto
}
