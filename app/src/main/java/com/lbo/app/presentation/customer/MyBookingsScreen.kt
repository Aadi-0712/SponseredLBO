package com.lbo.app.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lbo.app.data.model.Booking
import com.lbo.app.utils.Resource
import com.lbo.app.presentation.components.LBOButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    bookingsState: Resource<List<Booking>>,
    onRefresh: () -> Unit,
    onReviewClick: (Booking) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when (bookingsState) {
            is Resource.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(bookingsState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is Resource.Success -> {
                val bookings = bookingsState.data
                if (bookings.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("No bookings yet")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bookings) { booking ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(booking.providerName, style = MaterialTheme.typography.titleMedium)
                                        Text(booking.status.uppercase(), style = MaterialTheme.typography.labelSmall)
                                    }
                                    Text("${booking.date} at ${booking.time}", style = MaterialTheme.typography.bodySmall)
                                    
                                    if (booking.status == Booking.STATUS_COMPLETED) {
                                        Spacer(Modifier.height(8.dp))
                                        LBOButton(
                                            text = "Leave Review",
                                            onClick = { onReviewClick(booking) },
                                            modifier = Modifier.height(40.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
