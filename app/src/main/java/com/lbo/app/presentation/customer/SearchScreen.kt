package com.lbo.app.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lbo.app.presentation.components.LBOTextField
import com.lbo.app.presentation.components.ProviderCard // Simplified card assumption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchState: SearchState,
    onQueryChange: (String) -> Unit,
    onProviderClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    LBOTextField(
                        value = searchState.query,
                        onValueChange = onQueryChange,
                        label = "Search providers...",
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (searchState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(searchState.providers) { provider ->
                    // Assuming a ProviderCard exists or using a generic one
                    Card(
                        onClick = { onProviderClick(provider.userId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(provider.name, style = MaterialTheme.typography.titleMedium)
                            Text(provider.location, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
