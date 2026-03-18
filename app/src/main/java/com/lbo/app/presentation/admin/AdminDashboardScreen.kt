package com.lbo.app.presentation.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lbo.app.data.model.Booking
import com.lbo.app.data.model.Provider
import com.lbo.app.presentation.components.*
import com.lbo.app.presentation.customer.BookingCard
import com.lbo.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    state: AdminDashboardState,
    onRefresh: () -> Unit,
    onApproveProvider: (String) -> Unit,
    onRejectProvider: (String) -> Unit,
    onManageCategories: () -> Unit,
    onCreatePost: () -> Unit,
    onViewAllBookings: () -> Unit
) {
    if (state.isLoading) {
        LoadingIndicator()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stats Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Users",
                        value = "${state.stats.totalUsers}",
                        icon = Icons.Filled.People,
                        color = DeepBlue500,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Providers",
                        value = "${state.stats.totalProviders}",
                        icon = Icons.Filled.Engineering,
                        color = Orange500,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Pending",
                        value = "${state.stats.pendingApprovals}",
                        icon = Icons.Filled.HourglassTop,
                        color = Warning,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Bookings",
                        value = "${state.stats.totalBookings}",
                        icon = Icons.Filled.CalendarMonth,
                        color = Success,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onManageCategories,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Category, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Categories", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = onCreatePost,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Campaign, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Post Alert", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = onViewAllBookings,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.List, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Bookings", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Pending Approvals
            item {
                SectionHeader(title = "Pending Approvals")
            }
            if (state.pendingProviders.isEmpty()) {
                item {
                    EmptyState(
                        message = "No pending approvals",
                        icon = Icons.Outlined.CheckCircle
                    )
                }
            } else {
                items(state.pendingProviders) { provider ->
                    PendingProviderCard(
                        provider = provider,
                        onApprove = { onApproveProvider(provider.providerId) },
                        onReject = { onRejectProvider(provider.providerId) }
                    )
                }
            }

            // Recent Bookings
            item {
                SectionHeader(
                    title = "Recent Bookings",
                    actionText = "View All",
                    onActionClick = onViewAllBookings
                )
            }
            items(state.allBookings.take(5)) { booking ->
                BookingCard(booking = booking)
            }
        }
    }
}

@Composable
private fun PendingProviderCard(
    provider: Provider,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(DeepBlue100, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, tint = DeepBlue600)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        provider.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${provider.category} • ${provider.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${provider.experience} years experience",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (provider.documentsUrl.isNotEmpty()) {
                        Text(
                            "${provider.documentsUrl.size} documents uploaded",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Close, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reject")
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Success),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Check, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Approve")
                }
            }
        }
    }
}
