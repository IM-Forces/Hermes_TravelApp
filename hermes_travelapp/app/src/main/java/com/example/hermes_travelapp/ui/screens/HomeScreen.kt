package com.example.hermes_travelapp.ui.screens

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.model.RecommendationItem
import com.example.hermes_travelapp.ui.theme.*
import com.example.hermes_travelapp.ui.viewmodels.AccountViewModel
import org.json.JSONArray
import java.io.InputStream

fun loadRecommendationsFromAssets(context: Context): List<RecommendationItem> {
    val items = mutableListOf<RecommendationItem>()
    try {
        val inputStream: InputStream = context.assets.open("exemplos.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            
            val precio = if (obj.has("precio_estimado_usd")) {
                obj.getInt("precio_estimado_usd")
            } else if (obj.has("precio_estimated_usd")) {
                obj.getInt("precio_estimated_usd")
            } else {
                0
            }

            items.add(
                RecommendationItem(
                    lugar = obj.optString("lugar", "Lugar desconocido"),
                    tipo = obj.optString("tipo", "General"),
                    pais = obj.optString("pais", ""),
                    ciudadRegion = obj.optString("ciudad_region", ""),
                    precio = precio,
                    descripcion = obj.optString("descripcion", "")
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return items
}

@Composable
fun HomeScreen(
    items: List<RecommendationItem>? = null,
    favorites: List<RecommendationItem> = emptyList(),
    onToggleFavorite: (RecommendationItem) -> Unit = {},
    accountViewModel: AccountViewModel = viewModel(),
    onSearchHotelsClick: () -> Unit = {}
) {
    val username by accountViewModel.username.collectAsState()

    HomeScreenContent(
        username = username,
        items = items,
        favorites = favorites,
        onToggleFavorite = onToggleFavorite,
        onSearchHotelsClick = onSearchHotelsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    username: String,
    items: List<RecommendationItem>? = null,
    favorites: List<RecommendationItem> = emptyList(),
    onToggleFavorite: (RecommendationItem) -> Unit = {},
    onSearchHotelsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val recommendations = remember(items) { items ?: loadRecommendationsFromAssets(context) }

    Scaffold(
        topBar = { HomeTopBar(username = username) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                HotelSearchPromotionCard(onClick = onSearchHotelsClick)
            }

            if (recommendations.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxHeight(0.7f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.home_loading))
                        }
                    }
                }
            } else {
                items(recommendations) { item ->
                    val isFavorite = favorites.any { it.lugar == item.lugar }
                    RecommendationCard(
                        item = item,
                        isFavorite = isFavorite,
                        onToggleFavorite = { onToggleFavorite(item) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun HotelSearchPromotionCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.hotel_search_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Encuentra el alojamiento perfecto para tu viaje",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun RecommendationCard(
    item: RecommendationItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

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
                        text = item.tipo,
                        style = MaterialTheme.typography.labelSmall,
                        color = DoradoAtenea,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = item.lugar,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${item.ciudadRegion}, ${item.pais}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (item.precio > 0) "$${item.precio}" else stringResource(R.string.home_free),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(username: String) {
    val displayFirstName = username.split(" ").firstOrNull() ?: "User"
    val initials = if (username.length >= 2) username.take(2).uppercase() else username.take(1).uppercase()

    MediumTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.home_welcome, displayFirstName),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.size(28.dp))
            }
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.secondary)
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    val sampleItems = listOf(
        RecommendationItem(
            lugar = "Partenón",
            tipo = "Monumento",
            pais = "Grecia",
            ciudadRegion = "Atenas",
            precio = 20,
            descripcion = "Templo dedicado a la diosa Atenea."
        ),
        RecommendationItem(
            lugar = "Plaka",
            tipo = "Barrio histórico",
            pais = "Grecia",
            ciudadRegion = "Atenas",
            precio = 0,
            descripcion = "El barrio más antiguo de Atenas."
        )
    )

    Hermes_travelappTheme {
        HomeScreenContent(username = "Vítor Da Silva", items = sampleItems)
    }
}
