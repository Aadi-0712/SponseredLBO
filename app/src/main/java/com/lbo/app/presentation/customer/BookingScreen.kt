package com.lbo.app.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lbo.app.data.model.Provider
import com.lbo.app.presentation.components.LBOButton
import com.lbo.app.presentation.components.LBOTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    provider: Provider?,
    bookingFormState: BookingFormState,
    onCreateBooking: (String, String, String, String, String) -> Unit,
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    if (bookingFormState.isSuccess) {
        LaunchedEffect(Unit) {
            onBookingSuccess()
        }
    }

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Service") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Booking with ${provider?.name ?: "Provider"}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))

            LBOTextField(
                value = date,
                onValueChange = { date = it },
                label = "Select Date (e.g. 2024-05-20)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LBOTextField(
                value = time,
                onValueChange = { time = it },
                label = "Select Time (e.g. 10:00 AM)"
            )

            Spacer(modifier = Modifier.weight(1f))

            LBOButton(
                text = "Confirm Booking",
                onClick = {
                    provider?.let {
                        onCreateBooking(it.providerId, it.name, it.category, date, time)
                    }
                },
                isLoading = bookingFormState.isLoading,
                enabled = date.isNotBlank() && time.isNotBlank() && provider != null
            )
            
            bookingFormState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
