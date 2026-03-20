package com.example.hermes_travelapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.ItineraryItem
import com.example.hermes_travelapp.ui.theme.*
import com.example.hermes_travelapp.ui.viewmodels.ActivityViewModel
import com.example.hermes_travelapp.ui.viewmodels.TripDayViewModel
import com.example.hermes_travelapp.ui.viewmodels.TripViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class TripDayInfo(
    val id: String,
    val dayNumber: Int,
    val date: String,
    val fullDate: LocalDate,
    val dayOfWeek: String,
    val subtitle: String,
    val activitiesCount: Int,
    val budget: String
)

@Composable
fun DayItineraryScreen(
    tripId: String = "grecia_trip",
    dayId: String = "day1",
    tripViewModel: TripViewModel = viewModel(),
    activityViewModel: ActivityViewModel = viewModel(),
    tripDayViewModel: TripDayViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNavigateToEditActivity: (activityId: String) -> Unit = {}
) {
    val allTrips by tripViewModel.trips.collectAsState()
    val trip = allTrips.find { it.id == tripId }
    
    val domainDays by tripDayViewModel.tripDays.collectAsState()
    val activities by activityViewModel.activities.collectAsState()
    val dayCounts by activityViewModel.dayCounts.collectAsState()

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
    val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    
    val uiDays = remember(domainDays) {
        domainDays.map { domainDay ->
            TripDayInfo(
                id = domainDay.id,
                dayNumber = domainDay.dayNumber,
                date = domainDay.date.format(dateFormatter),
                fullDate = domainDay.date,
                dayOfWeek = domainDay.date.format(dayOfWeekFormatter).replaceFirstChar { it.uppercase() },
                subtitle = domainDay.subtitle,
                activitiesCount = 0,
                budget = "€0" 
            )
        }
    }

    LaunchedEffect(tripId) {
        tripDayViewModel.loadDaysForTrip(tripId)
    }

    if (uiDays.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val initialPageIndex = remember(uiDays, dayId) {
        val index = uiDays.indexOfFirst { it.id == dayId }
        if (index >= 0) index else 0
    }
    
    val pagerState = rememberPagerState(initialPage = initialPageIndex) { uiDays.size }
    
    LaunchedEffect(tripId, uiDays) {
        if (uiDays.isNotEmpty()) {
            activityViewModel.loadAllDayCounts(tripId, uiDays.map { it.id })
        }
    }

    LaunchedEffect(pagerState.currentPage, uiDays) {
        if (uiDays.isNotEmpty()) {
            val currentDayId = uiDays[pagerState.currentPage].id
            activityViewModel.loadActivitiesForDay(tripId, currentDayId)
        }
    }

    val currentDayBudget = remember(activities) {
        val total = activities.sumOf { it.cost ?: 0.0 }
        "€${total.toInt()}"
    }

    DayItineraryContent(
        tripTitle = trip?.title ?: stringResource(R.string.itinerary_title),
        uiDays = uiDays,
        dayCounts = dayCounts,
        pagerState = pagerState,
        activities = activities,
        currentDayBudget = currentDayBudget,
        onBack = onBack,
        onAddActivity = { },
        onEditActivity = onNavigateToEditActivity,
        onDeleteActivity = { }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayItineraryContent(
    tripTitle: String,
    uiDays: List<TripDayInfo>,
    dayCounts: Map<String, Int>,
    pagerState: PagerState,
    activities: List<ItineraryItem>,
    currentDayBudget: String,
    onBack: () -> Unit,
    onAddActivity: () -> Unit,
    onEditActivity: (String) -> Unit,
    onDeleteActivity: (ItineraryItem) -> Unit
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tripTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddActivity,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(32.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DayCarousel(
                days = uiDays,
                dayCounts = dayCounts,
                selectedPageIndex = pagerState.currentPage,
                onDayClick = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val day = uiDays[pageIndex]
                
                DayContent(
                    day = day.copy(budget = if(pageIndex == pagerState.currentPage) currentDayBudget else day.budget),
                    activities = if(pageIndex == pagerState.currentPage) activities else emptyList(),
                    onEdit = onEditActivity,
                    onDelete = onDeleteActivity,
                    onAddFirst = onAddActivity
                )
            }
        }
    }
}

@Composable
fun DayCarousel(
    days: List<TripDayInfo>,
    dayCounts: Map<String, Int>,
    selectedPageIndex: Int,
    onDayClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(selectedPageIndex) {
        if (days.isNotEmpty()) {
            listState.animateScrollToItem(selectedPageIndex)
        }
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
    ) {
        items(days.size) { index ->
            val day = days[index]
            val isSelected = index == selectedPageIndex
            val count = dayCounts[day.id] ?: 0
            
            DayChip(
                day = day,
                count = count,
                isSelected = isSelected,
                onClick = { onDayClick(index) }
            )
        }
    }
}

@Composable
fun DayChip(
    day: TripDayInfo,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(85.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.itinerary_day, day.dayNumber),
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = day.date,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.itinerary_activities, count),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun DayContent(
    day: TripDayInfo,
    activities: List<ItineraryItem>,
    onEdit: (String) -> Unit,
    onDelete: (ItineraryItem) -> Unit,
    onAddFirst: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = stringResource(R.string.itinerary_day, day.dayNumber) + " • ${day.dayOfWeek}, ${day.date}",
                    style = MaterialTheme.typography.labelLarge,
                    color = DoradoAtenea,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = day.subtitle.ifBlank { stringResource(R.string.itinerary_no_activities) },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoLabel(Icons.Default.Schedule, "09:00 - 22:00")
                    InfoLabel(Icons.AutoMirrored.Filled.List, stringResource(R.string.itinerary_activities, activities.size))
                    InfoLabel(Icons.Default.Payments, day.budget)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }

        if (activities.isEmpty()) {
            item {
                EmptyActivitiesState(onAddFirst)
            }
        } else {
            items(activities) { activity ->
                ActivityTimelineItem(
                    activity = activity,
                    isLast = activity == activities.last(),
                    onEdit = { onEdit(activity.id) },
                    onDelete = { onDelete(activity) }
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun InfoLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun ActivityTimelineItem(
    activity: ItineraryItem,
    isLast: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(DoradoAtenea, CircleShape)
                    .border(3.dp, DoradoAtenea.copy(alpha = 0.2f), CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(130.dp)
                        .background(DoradoAtenea.copy(alpha = 0.3f))
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f).padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = activity.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                if (activity.description.isNotBlank()) {
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = TerracotaSuave)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(activity.location ?: stringResource(R.string.itinerary_no_location), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }
                    if (activity.cost != null) {
                        Text(
                            text = "€${activity.cost.toInt()}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = DoradoAtenea
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyActivitiesState(onAddFirst: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.itinerary_no_activities),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddFirst,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.itinerary_add_first_activity))
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DayItineraryPreview() {
    val sampleDays = listOf(
        TripDayInfo("1", 1, "15 Jul", LocalDate.now(), "Lun", "Llegada a Atenas", 2, "€50"),
        TripDayInfo("2", 2, "16 Jul", LocalDate.now().plusDays(1), "Mar", "Acrópolis", 4, "€120")
    )
    
    val sampleActivities = listOf(
        ItineraryItem(
            id = "1",
            tripId = "t1",
            dayId = "1",
            title = "Visita al Partenón",
            description = "Tour guiado por la Acrópolis de Atenas.",
            date = LocalDate.now(),
            time = LocalTime.of(9, 0),
            location = "Atenas, Grecia",
            cost = 20.0
        ),
        ItineraryItem(
            id = "2",
            tripId = "t1",
            dayId = "1",
            title = "Comida en Plaka",
            description = "Degustación de comida típica griega.",
            date = LocalDate.now(),
            time = LocalTime.of(13, 30),
            location = "Barrio de Plaka",
            cost = 30.0
        )
    )
    
    val pagerState = rememberPagerState(initialPage = 0) { sampleDays.size }

    Hermes_travelappTheme {
        DayItineraryContent(
            tripTitle = "Viaje a Grecia",
            uiDays = sampleDays,
            dayCounts = mapOf("1" to 2, "2" to 4),
            pagerState = pagerState,
            activities = sampleActivities,
            currentDayBudget = "€50",
            onBack = {},
            onAddActivity = {},
            onEditActivity = {},
            onDeleteActivity = {}
        )
    }
}
