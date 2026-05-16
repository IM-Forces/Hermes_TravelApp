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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
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
    val allRecommendations = remember(items) { items ?: loadRecommendationsFromAssets(context) }
    
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredRecommendations = remember(searchQuery, allRecommendations) {
        if (searchQuery.isBlank()) {
            allRecommendations
        } else {
            allRecommendations.filter { item ->
                item.lugar.contains(searchQuery, ignoreCase = true) ||
                item.pais.contains(searchQuery, ignoreCase = true) ||
                item.ciudadRegion.contains(searchQuery, ignoreCase = true) ||
                item.tipo.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(username = username)
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.explore_search_placeholder)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                }

                item {
                    HotelSearchPromotionCard(onClick = onSearchHotelsClick)
                }

                if (allRecommendations.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxHeight(0.7f), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(stringResource(R.string.home_loading))
                            }
                        }
                    }
                } else if (filteredRecommendations.isEmpty() && searchQuery.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.explore_no_results, searchQuery),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    items(filteredRecommendations) { item ->
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

@Composable
fun HomeTopBar(username: String) {
    val displayFirstName = username.split(" ").firstOrNull() ?: "User"
    val initials = if (username.length >= 2) username.take(2).uppercase() else if (username.isNotEmpty()) username.uppercase() else "U"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.secondary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.home_welcome, displayFirstName),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
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
