package com.lbo.app.presentation.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lbo.app.data.model.User
import com.lbo.app.presentation.components.CategoryItem // Assumption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeState: HomeState,
    onSearch: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onProviderClick: (String) -> Unit,
    onViewAllTopRated: () -> Unit,
    onViewAllProviders: () -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LBO Marketplace") },
                actions = {
                    IconButton(onClick = onSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        if (homeState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Categories
                item {
                    Text("Categories", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(homeState.categories) { category ->
                            Card(
                                onClick = { onCategoryClick(category.name) },
                                modifier = Modifier.size(100.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(category.name, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                // Top Rated
                item {
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Top Rated", style = MaterialTheme.typography.titleLarge)
                        TextButton(onClick = onViewAllTopRated) { Text("View All") }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                items(homeState.topRatedProviders) { provider ->
                    Card(
                        onClick = { onProviderClick(provider.providerId) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(provider.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.weight(1f))
                            Text(provider.location, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
