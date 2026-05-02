package com.example.hermes_travelapp.data.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hermes_travelapp.data.database.dao.AccessLogDao
import com.example.hermes_travelapp.data.database.dao.ItineraryItemDao
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.data.database.dao.TripDayDao
import com.example.hermes_travelapp.data.database.dao.UserDao
import com.example.hermes_travelapp.data.database.entities.AccessLogEntity
import com.example.hermes_travelapp.data.database.entities.ItineraryItemEntity
import com.example.hermes_travelapp.data.database.entities.TripDayEntity
import com.example.hermes_travelapp.data.database.entities.TripEntity
import com.example.hermes_travelapp.data.database.entities.UserEntity
import com.example.hermes_travelapp.domain.ValidationUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class DatabaseTests {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var tripDao: TripDao
    private lateinit var tripDayDao: TripDayDao
    private lateinit var itineraryItemDao: ItineraryItemDao
    private lateinit var accessLogDao: AccessLogDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        try {
            // Instanciamos el conversor ya que es @ProvidedTypeConverter
            val converters = AppTypeConverters()
            
            db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .addTypeConverter(converters)
                .allowMainThreadQueries()
                .build()
                
            userDao = db.userDao()
            tripDao = db.tripDao()
            tripDayDao = db.tripDayDao()
            itineraryItemDao = db.itineraryItemDao()
            accessLogDao = db.accessLogDao()
            Log.d("DatabaseTests", "DB inicializada con éxito")
        } catch (e: Exception) {
            Log.e("DatabaseTests", "Error inicializando DB", e)
            throw e
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        if (::db.isInitialized) {
            db.close()
        }
    }

    @Test
    fun testUserPersistence() = runBlocking {
        val user = UserEntity(
            id = "user_123",
            name = "Test User",
            email = "test@hermes.com",
            login = "test@hermes.com",
            username = "tester",
            birthdate = 0L,
            address = "Test St",
            country = "Spain",
            phone = "123",
            acceptEmails = true,
            profileInitials = "TU",
            activeTripCount = 0,
            countriesVisited = 0
        )
        
        userDao.insertUser(user)
        val result = userDao.getUserById("user_123")
        
        assertNotNull("El usuario no debería ser nulo", result)
        assertEquals("tester", result?.username)
    }

    @Test
    fun testTripConstraintAndFiltering() = runBlocking {
        // Necesitamos insertar el usuario primero por la Foreign Key
        val user = UserEntity(
            id = "owner_id",
            name = "Owner",
            email = "owner@test.com",
            login = "owner@test.com",
            username = "owner_username",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = false,
            profileInitials = "O",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)

        val trip = TripEntity(
            id = "trip_001",
            title = "Vacaciones",
            startDate = "2024-01-01",
            endDate = "2024-01-10",
            description = "Desc",
            emoji = "🏖️",
            budget = 1000,
            spent = 0,
            progress = 0f,
            daysRemaining = 10,
            userId = "owner_id"
        )
        tripDao.insertTrip(trip)

        val trips = tripDao.getTripsByUser("owner_id").first()
        assertEquals(1, trips.size)
        assertEquals("Vacaciones", trips[0].title)
    }

    @Test
    fun testAccessLogPersistence() = runBlocking {
        val log = AccessLogEntity(
            userId = "user_123",
            type = "IN"
        )
        
        accessLogDao.insertLog(log)
        val logs = accessLogDao.getLogsByUser("user_123").first()
        
        assertTrue("Debería haber al menos un log", logs.isNotEmpty())
        assertEquals("IN", logs[0].type)
    }

    @Test
    fun testUsernameUniqueness() = runBlocking {
        val user = UserEntity(
            id = "u1",
            name = "U1",
            email = "u1@test.com",
            login = "u1@test.com",
            username = "unique_user",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = false,
            profileInitials = "U",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)
        
        val isTaken = userDao.isUsernameTaken("unique_user")
        assertTrue("El username debería estar ocupado", isTaken)
    }

    @Test
    fun testTripUpdate() = runBlocking {
        val user = UserEntity(
            id = "user_update",
            name = "Update User",
            email = "update@test.com",
            login = "update@test.com",
            username = "update_user",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = false,
            profileInitials = "U",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)

        val trip = TripEntity(
            id = "trip_update_id",
            title = "Original Title",
            startDate = "2024-01-01",
            endDate = "2024-01-10",
            description = "Original Description",
            emoji = "🏖️",
            budget = 1000,
            spent = 0,
            progress = 0f,
            daysRemaining = 10,
            userId = "user_update"
        )
        tripDao.insertTrip(trip)

        val updatedTrip = trip.copy(title = "Updated Title", budget = 2000)
        tripDao.updateTrip(updatedTrip)

        val result = tripDao.getTripById("trip_update_id")
        assertNotNull(result)
        assertEquals("Updated Title", result?.title)
        assertEquals(2000, result?.budget)
    }

    @Test
    fun testTripDelete() = runBlocking {
        val user = UserEntity(
            id = "user_delete",
            name = "Delete User",
            email = "delete@test.com",
            login = "delete@test.com",
            username = "delete_user",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = false,
            profileInitials = "U",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)

        val trip = TripEntity(
            id = "trip_delete_id",
            title = "Delete Me",
            startDate = "2024-01-01",
            endDate = "2024-01-05",
            description = "",
            emoji = "🗑️",
            budget = 100,
            spent = 0,
            progress = 0f,
            daysRemaining = 4,
            userId = "user_delete"
        )
        tripDao.insertTrip(trip)

        // Verificar que existe
        assertNotNull(tripDao.getTripById("trip_delete_id"))

        // Eliminar
        tripDao.deleteTrip(trip)

        // Verificar que ya no existe
        val result = tripDao.getTripById("trip_delete_id")
        assertTrue("El viaje debería haber sido eliminado", result == null)
    }

    @Test
    fun testTripDeleteById() = runBlocking {
        val user = UserEntity(
            id = "user_delete_id",
            name = "Delete By Id User",
            email = "delete_id@test.com",
            login = "delete_id@test.com",
            username = "delete_id_user",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = false,
            profileInitials = "U",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)

        val trip = TripEntity(
            id = "trip_to_delete_id",
            title = "Delete By ID",
            startDate = "2024-01-01",
            endDate = "2024-01-05",
            description = "",
            emoji = "🆔",
            budget = 100,
            spent = 0,
            progress = 0f,
            daysRemaining = 4,
            userId = "user_delete_id"
        )
        tripDao.insertTrip(trip)

        tripDao.deleteTripById("trip_to_delete_id")

        val result = tripDao.getTripById("trip_to_delete_id")
        assertTrue("El viaje debería haber sido eliminado por ID", result == null)
    }

    @Test
    fun testTripInvalidDates() = runBlocking {
        val user = UserEntity(
            id = "user_dates",
            name = "Dates User",
            email = "dates@test.com",
            login = "dates@test.com",
            username = "dates_user",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = false,
            profileInitials = "U",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)

        // Creamos un viaje con fecha de inicio posterior a la de fin
        val invalidTrip = TripEntity(
            id = "trip_invalid_dates",
            title = "Invalid Dates Trip",
            startDate = "2024-12-31",
            endDate = "2024-01-01", 
            description = "This trip has invalid date range",
            emoji = "⚠️",
            budget = 100,
            spent = 0,
            progress = 0f,
            daysRemaining = -364, // Valor inconsistente
            userId = "user_dates"
        )

        tripDao.insertTrip(invalidTrip)
        val result = tripDao.getTripById("trip_invalid_dates")

        assertNotNull("El viaje debería haberse insertado", result)
        // Verificamos que los datos se guardaron, exponiendo que actualmente no hay restricción
        assertEquals("2024-12-31", result?.startDate)
        assertEquals("2024-01-01", result?.endDate)
    }

    // --- Helper setup for Itinerary Tests ---
    private suspend fun setupFullHierarchy(
        userId: String = "u1",
        tripId: String = "t1",
        dayId: String = "d1"
    ) {
        val user = UserEntity(
            id = userId,
            name = "Test User",
            email = "$userId@test.com",
            login = "$userId@test.com",
            username = "user_$userId",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = false,
            profileInitials = "T",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)

        val trip = TripEntity(
            id = tripId,
            title = "Test Trip",
            startDate = "2024-01-01",
            endDate = "2024-01-10",
            description = "",
            emoji = "✈️",
            budget = 1000,
            spent = 0,
            progress = 0f,
            daysRemaining = 10,
            userId = userId
        )
        tripDao.insertTrip(trip)

        val tripDay = TripDayEntity(
            id = dayId,
            tripId = tripId,
            dayNumber = 1,
            date = LocalDate.of(2024, 1, 1)
        )
        tripDayDao.insertTripDay(tripDay)
    }

    @Test
    fun testInsertAndQueryActivity() = runBlocking {
        setupFullHierarchy(dayId = "day_1")
        
        val activity = ItineraryItemEntity(
            id = "act_1",
            tripId = "t1",
            dayId = "day_1",
            title = "Visit Museum",
            description = "History museum",
            date = LocalDate.of(2024, 1, 1),
            time = LocalTime.of(10, 0),
            location = "City Center",
            cost = 15.0
        )
        
        itineraryItemDao.insertActivity(activity)
        
        val activities = itineraryItemDao.getActivitiesForDay("day_1").first()
        assertEquals(1, activities.size)
        assertEquals("Visit Museum", activities[0].title)
    }

    @Test
    fun testUpdateActivity() = runBlocking {
        setupFullHierarchy(dayId = "day_update")
        
        val activity = ItineraryItemEntity(
            id = "act_update",
            tripId = "t1",
            dayId = "day_update",
            title = "Old Title",
            description = "",
            date = LocalDate.of(2024, 1, 1),
            time = LocalTime.of(10, 0),
            location = null,
            cost = null
        )
        itineraryItemDao.insertActivity(activity)
        
        val updatedActivity = activity.copy(
            title = "New Title",
            time = LocalTime.of(11, 30)
        )
        itineraryItemDao.updateActivity(updatedActivity)
        
        val result = itineraryItemDao.getActivitiesForDay("day_update").first()
        assertEquals(1, result.size)
        assertEquals("New Title", result[0].title)
        assertEquals(LocalTime.of(11, 30), result[0].time)
    }

    @Test
    fun testDeleteActivity() = runBlocking {
        setupFullHierarchy(dayId = "day_delete")
        
        val activity = ItineraryItemEntity(
            id = "act_delete",
            tripId = "t1",
            dayId = "day_delete",
            title = "Delete Me",
            description = "",
            date = LocalDate.of(2024, 1, 1),
            time = LocalTime.of(10, 0),
            location = null,
            cost = null
        )
        itineraryItemDao.insertActivity(activity)
        
        // Check exists
        assertTrue(itineraryItemDao.getActivitiesForDay("day_delete").first().isNotEmpty())
        
        itineraryItemDao.deleteActivity(activity)
        
        // Check deleted
        assertTrue(itineraryItemDao.getActivitiesForDay("day_delete").first().isEmpty())
    }

    @Test
    fun testDeleteActivityById() = runBlocking {
        setupFullHierarchy(dayId = "day_delete_id")
        
        val activity = ItineraryItemEntity(
            id = "act_delete_id",
            tripId = "t1",
            dayId = "day_delete_id",
            title = "Delete By Id",
            description = "",
            date = LocalDate.of(2024, 1, 1),
            time = LocalTime.of(10, 0),
            location = null,
            cost = null
        )
        itineraryItemDao.insertActivity(activity)
        
        itineraryItemDao.deleteActivityById("act_delete_id")
        
        assertTrue(itineraryItemDao.getActivitiesForDay("day_delete_id").first().isEmpty())
    }

    @Test
    fun testActivitiesOrderedByTime() = runBlocking {
        setupFullHierarchy(dayId = "day_ordered")
        
        val act1 = ItineraryItemEntity(
            id = "a1", tripId = "t1", dayId = "day_ordered",
            title = "Late Activity", description = "",
            date = LocalDate.of(2024, 1, 1), time = LocalTime.of(20, 0),
            location = null, cost = null
        )
        val act2 = ItineraryItemEntity(
            id = "a2", tripId = "t1", dayId = "day_ordered",
            title = "Early Activity", description = "",
            date = LocalDate.of(2024, 1, 1), time = LocalTime.of(8, 0),
            location = null, cost = null
        )
        val act3 = ItineraryItemEntity(
            id = "a3", tripId = "t1", dayId = "day_ordered",
            title = "Mid Activity", description = "",
            date = LocalDate.of(2024, 1, 1), time = LocalTime.of(12, 0),
            location = null, cost = null
        )
        
        // Insert in random order
        itineraryItemDao.insertActivity(act1)
        itineraryItemDao.insertActivity(act2)
        itineraryItemDao.insertActivity(act3)
        
        val result = itineraryItemDao.getActivitiesForDay("day_ordered").first()
        assertEquals(3, result.size)
        assertEquals("Early Activity", result[0].title)
        assertEquals("Mid Activity", result[1].title)
        assertEquals("Late Activity", result[2].title)
    }

    @Test
    fun testActivitiesFilteredByDayId() = runBlocking {
        setupFullHierarchy(dayId = "day_1")
        
        // Add another day to the same trip
        val day2 = TripDayEntity(
            id = "day_2",
            tripId = "t1",
            dayNumber = 2,
            date = LocalDate.of(2024, 1, 2)
        )
        tripDayDao.insertTripDay(day2)
        
        val actDay1 = ItineraryItemEntity(
            id = "act_d1", tripId = "t1", dayId = "day_1",
            title = "Day 1 Activity", description = "",
            date = LocalDate.of(2024, 1, 1), time = LocalTime.of(10, 0),
            location = null, cost = null
        )
        val actDay2 = ItineraryItemEntity(
            id = "act_d2", tripId = "t1", dayId = "day_2",
            title = "Day 2 Activity", description = "",
            date = LocalDate.of(2024, 1, 2), time = LocalTime.of(10, 0),
            location = null, cost = null
        )
        
        itineraryItemDao.insertActivity(actDay1)
        itineraryItemDao.insertActivity(actDay2)
        
        val resultDay1 = itineraryItemDao.getActivitiesForDay("day_1").first()
        assertEquals(1, resultDay1.size)
        assertEquals("Day 1 Activity", resultDay1[0].title)
        
        val resultDay2 = itineraryItemDao.getActivitiesForDay("day_2").first()
        assertEquals(1, resultDay2.size)
        assertEquals("Day 2 Activity", resultDay2[0].title)
    }

    @Test
    fun testDuplicateTripNamePrevention() = runBlocking {
        val userId = "user_dup_test"
        val tripTitle = "Japan 2024"
        
        // Insert user
        val user = UserEntity(
            id = userId,
            name = "Dup User",
            email = "dup@test.com",
            login = "dup@test.com",
            username = "dup_user",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = false,
            profileInitials = "D",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)

        // Insert first trip
        val trip = TripEntity(
            id = "trip_dup_1",
            title = tripTitle,
            startDate = "2024-01-01",
            endDate = "2024-01-10",
            description = "",
            emoji = "🇯🇵",
            budget = 1000,
            spent = 0,
            progress = 0f,
            daysRemaining = 10,
            userId = userId
        )
        tripDao.insertTrip(trip)

        // Verify existsByTitle returns true for the same user and title
        val exists = tripDao.existsByTitle(userId, tripTitle)
        assertTrue("El DAO debería detectar que el nombre del viaje ya existe para este usuario", exists)
    }

    @Test
    fun testValidationUtils() {
        // Fechas válidas (futuras y en orden correcto)
        val validStart = "01/01/2030"
        val validEnd = "10/01/2030"
        
        val validResult = ValidationUtils.validateTripDates(validStart, validEnd)
        assertNull("Debería retornar null para fechas válidas", validResult)

        // Fechas inválidas (inicio después de fin)
        val invalidStart = "15/01/2030"
        val invalidEnd = "10/01/2030"
        
        val invalidResult = ValidationUtils.validateTripDates(invalidStart, invalidEnd)
        assertEquals(
            "La fecha de inicio debe ser anterior a la de fin",
            invalidResult
        )
        
        // Fechas en el pasado (también debería fallar según la lógica de ValidationUtils)
        val pastStart = "01/01/2020"
        val pastEnd = "05/01/2020"
        val pastResult = ValidationUtils.validateTripDates(pastStart, pastEnd)
        assertEquals("La fecha de inicio no puede estar en el pasado", pastResult)
    }
}
