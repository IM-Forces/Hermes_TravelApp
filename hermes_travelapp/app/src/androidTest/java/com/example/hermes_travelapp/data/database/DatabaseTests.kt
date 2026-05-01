package com.example.hermes_travelapp.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.data.database.dao.UserDao
import com.example.hermes_travelapp.data.database.entities.TripEntity
import com.example.hermes_travelapp.data.database.entities.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTests {
    private lateinit var userDao: UserDao
    private lateinit var tripDao: TripDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        userDao = db.userDao()
        tripDao = db.tripDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val user = UserEntity(
            id = "user_1",
            name = "Test User",
            email = "test@example.com",
            login = "test@example.com",
            username = "testuser",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = true,
            profileInitials = "TU",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)
        val byName = userDao.getUserById("user_1")
        assertEquals(byName?.username, "testuser")
    }

    @Test
    @Throws(Exception::class)
    fun insertTripAndCheckIfExists() = runBlocking {
        val user = UserEntity(
            id = "user_1",
            name = "Test User",
            email = "test@example.com",
            login = "test@example.com",
            username = "testuser",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = true,
            profileInitials = "TU",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)

        val trip = TripEntity(
            id = "trip_1",
            title = "Paris Trip",
            startDate = "2023-10-01",
            endDate = "2023-10-10",
            description = "Fun trip",
            emoji = "🗼",
            budget = 1000,
            spent = 0,
            progress = 0f,
            daysRemaining = 10,
            userId = "user_1"
        )
        tripDao.insertTrip(trip)

        val exists = tripDao.existsByTitle("user_1", "Paris Trip")
        assertTrue(exists)
        
        val trips = tripDao.getTripsByUser("user_1").first()
        assertEquals(1, trips.size)
        assertEquals("Paris Trip", trips[0].title)
    }

    @Test
    @Throws(Exception::class)
    fun checkUsernameTaken() = runBlocking {
        val user = UserEntity(
            id = "user_1",
            name = "Test User",
            email = "test@example.com",
            login = "test@example.com",
            username = "testuser",
            birthdate = 0L,
            address = "",
            country = "",
            phone = "",
            acceptEmails = true,
            profileInitials = "TU",
            activeTripCount = 0,
            countriesVisited = 0
        )
        userDao.insertUser(user)
        
        val isTaken = userDao.isUsernameTaken("testuser")
        assertTrue(isTaken)
    }
}
