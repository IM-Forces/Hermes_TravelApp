package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.hermes_travelapp.domain.repository.ActivityRepository
import com.example.hermes_travelapp.domain.model.ItineraryItem
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: ActivityRepository
    private lateinit var viewModel: ActivityViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        repository = mockk(relaxed = true)
        viewModel = ActivityViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `loadActivitiesForDay updates activities state`() {
        // Arrange
        val dayId = "day1"
        val mockActivities = listOf(
            ItineraryItem("1", "trip1", dayId, "Museum", "Desc", LocalDate.now(), LocalTime.now())
        )
        every { repository.getActivitiesForDay(dayId) } returns flowOf(mockActivities)

        // Act
        viewModel.loadActivitiesForDay(dayId)

        // Assert
        assertEquals(mockActivities, viewModel.activities.value)
        assertEquals(1, viewModel.dayCounts.value[dayId])
    }

    @Test
    fun `addActivity calls repository when valid`() {
        // Arrange
        val activity = ItineraryItem(
            id = "1",
            tripId = "trip1",
            dayId = "day1",
            title = "Valid Title",
            description = "Valid Description",
            date = LocalDate.now(),
            time = LocalTime.now()
        )

        // Act
        viewModel.addActivity(activity)

        // Assert
        coVerify { repository.addActivity(activity) }
    }

    @Test
    fun `deleteActivity calls repository`() {
        // Arrange
        val activityId = "act_123"

        // Act
        viewModel.deleteActivity(activityId)

        // Assert
        coVerify { repository.deleteActivity(activityId) }
    }

    @Test
    fun `validation rejects activity with empty title`() {
        // Arrange
        val invalidActivity = ItineraryItem(
            id = "1",
            tripId = "trip1",
            dayId = "day1",
            title = "", // Titulo vacío invalida la actividad
            description = "Desc",
            date = LocalDate.now(),
            time = LocalTime.now()
        )

        // Act
        viewModel.addActivity(invalidActivity)

        // Assert
        coVerify(exactly = 0) { repository.addActivity(any()) }
    }
}
