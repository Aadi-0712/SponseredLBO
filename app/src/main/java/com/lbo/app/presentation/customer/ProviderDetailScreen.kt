package com.lbo.app.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lbo.app.data.model.Review
import com.lbo.app.data.model.User
import com.lbo.app.presentation.components.LBOButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailScreen(
    provider: User?,
    reviews: List<Review>,
    onBookNow: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(provider?.name ?: "Provider Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (provider == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(provider.name, style = MaterialTheme.typography.headlineMedium)
                Text(provider.location, style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("About", style = MaterialTheme.typography.titleMedium)
                Text("Experienced professional providing high-quality services in your local area.", style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Reviews (${reviews.size})", style = MaterialTheme.typography.titleMedium)
                reviews.forEach { review ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Rating: ${review.rating}/5", style = MaterialTheme.typography.labelSmall)
                            Text(review.comment, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                LBOButton(
                    text = "Book Now",
                    onClick = onBookNow
                )
            }
        }
    }
}
