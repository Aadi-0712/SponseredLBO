package com.lbo.app.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lbo.app.data.model.Booking
import com.lbo.app.presentation.theme.*

@Composable
fun BookingCard(
    booking: Booking,
    showActionButtons: Boolean = false,
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        booking.providerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        booking.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (booking.status) {
                        Booking.STATUS_REQUESTED -> WarningLight
                        Booking.STATUS_ACCEPTED -> PrimaryBlue.copy(alpha = 0.1f)
                        Booking.STATUS_COMPLETED -> SuccessLight
                        else -> ErrorLight
                    }
                ) {
                    Text(
                        booking.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (booking.status) {
                            Booking.STATUS_REQUESTED -> Warning
                            Booking.STATUS_ACCEPTED -> PrimaryBlue
                            Booking.STATUS_COMPLETED -> Success
                            else -> ErrorRed
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${booking.date} at ${booking.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (showActionButtons && booking.status == Booking.STATUS_REQUESTED) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                    ) {
                        Icon(Icons.Default.Close, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reject")
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Success)
                    ) {
                        Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Accept")
                    }
                }
            } else if (showActionButtons && booking.status == Booking.STATUS_ACCEPTED) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Mark as Completed")
                }
            }
        }
    }
}
