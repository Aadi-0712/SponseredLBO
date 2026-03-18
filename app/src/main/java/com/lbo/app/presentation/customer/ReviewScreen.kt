package com.lbo.app.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lbo.app.presentation.components.LBOButton
import com.lbo.app.presentation.components.LBOTextField
import com.lbo.app.presentation.components.RatingBar
import com.lbo.app.presentation.theme.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    bookingId: String,
    providerId: String,
    providerName: String,
    onSubmitReview: (String, String, Float, String) -> Unit,
    onBack: () -> Unit
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rate & Review") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSubmitted) {
                Spacer(modifier = Modifier.height(64.dp))
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Thank You!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Your review has been submitted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                LBOButton(
                    text = "Go Back",
                    onClick = onBack,
                    modifier = Modifier.width(200.dp)
                )
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "How was your experience with",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            providerName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "Tap to rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RatingBar(
                            rating = rating,
                            onRatingChange = { rating = it },
                            starSize = 40
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        LBOTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = "Write your review (optional)",
                            singleLine = false,
                            maxLines = 5
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                LBOButton(
                    text = "Submit Review",
                    onClick = {
                        onSubmitReview(bookingId, providerId, rating, comment)
                        isSubmitted = true
                    },
                    enabled = rating > 0,
                    icon = Icons.Filled.Send
                )
            }
        }
    }
}
