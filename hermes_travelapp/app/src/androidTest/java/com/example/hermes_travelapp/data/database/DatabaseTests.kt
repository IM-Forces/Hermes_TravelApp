package com.example.hermes_travelapp.data.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hermes_travelapp.data.database.dao.AccessLogDao
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.data.database.dao.UserDao
import com.example.hermes_travelapp.data.database.entities.AccessLogEntity
import com.example.hermes_travelapp.data.database.entities.TripEntity
import com.example.hermes_travelapp.data.database.entities.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTests {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var tripDao: TripDao
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
}
