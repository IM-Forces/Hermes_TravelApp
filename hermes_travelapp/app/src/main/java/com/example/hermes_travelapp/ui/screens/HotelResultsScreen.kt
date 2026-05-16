package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.model.Hotel
import com.example.hermes_travelapp.ui.viewmodels.HotelSearchViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import com.example.hermes_travelapp.ui.theme.*
import com.example.hermes_travelapp.domain.model.HotelRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelResultsScreen(
    city: String,
    startDate: String,
    endDate: String,
    viewModel: HotelSearchViewModel,
    onBack: () -> Unit,
    onHotelClick: (Hotel) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    HotelResultsContent(
        city = city,
        searchResults = searchResults,
        isLoading = isLoading,
        error = error,
        onBack = onBack,
        onHotelClick = onHotelClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelResultsContent(
    city: String,
    searchResults: List<Hotel>,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onHotelClick: (Hotel) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.hotel_results_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else if (searchResults.isEmpty()) {
                Text(
                    text = stringResource(R.string.hotel_no_results),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(searchResults) { hotel ->
                        HotelCard(hotel = hotel, onClick = { onHotelClick(hotel) })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HotelResultsScreenPreview() {
    val mockHotels = listOf(
        Hotel(
            id = "1",
            name = "Grand Hotel Barcelona",
            address = "Via Laietana, 30, 08003 Barcelona",
            rating = 4,
            imageUrl = "https://example.com/hotel1.jpg",
            rooms = listOf(HotelRoom(id = "r1", roomType = "Double", price = 150.0, images = emptyList<String>()))
        ),
        Hotel(
            id = "2",
            name = "Sea View Resort",
            address = "Passeig Marítim, 10, 08005 Barcelona",
            rating = 5,
            imageUrl = "https://example.com/hotel2.jpg",
            rooms = listOf(HotelRoom(id = "r2", roomType = "Suite", price = 250.0, images = emptyList<String>()))
        )
    )

    Hermes_travelappTheme {
        HotelResultsContent(
            city = "Barcelona",
            searchResults = mockHotels,
            isLoading = false,
            error = null,
            onBack = {},
            onHotelClick = {}
        )
    }
}



@Composable
fun HotelCard(hotel: Hotel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = hotel.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOTEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = DoradoAtenea,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = hotel.rating.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = hotel.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                val minPrice = hotel.rooms.minOfOrNull { it.price } ?: 0.0
                Text(
                    text = stringResource(R.string.hotel_price_from, minPrice),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
