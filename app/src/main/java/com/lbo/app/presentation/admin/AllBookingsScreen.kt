package com.lbo.app.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lbo.app.data.model.Booking
import com.lbo.app.presentation.components.*
import com.lbo.app.presentation.customer.BookingCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBookingsScreen(
    bookings: List<Booking>,
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Requested", "Accepted", "Completed", "Rejected")

    val filteredBookings = if (selectedFilter == "All") bookings
    else bookings.filter { it.status == selectedFilter }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Bookings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                filters.forEachIndexed { index, filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        text = { Text(filter) }
                    )
                }
            }

            if (filteredBookings.isEmpty()) {
                EmptyState(message = "No $selectedFilter bookings")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredBookings) { booking ->
                        BookingCard(booking = booking)
                    }
                }
            }
        }
    }
}
