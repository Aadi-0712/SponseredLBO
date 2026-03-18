package com.lbo.app.presentation.admin

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
import com.lbo.app.presentation.theme.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onCreatePost: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPosted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Community Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isPosted) {
                Spacer(modifier = Modifier.height(64.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.CheckCircle, null,
                        tint = Success,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Post Published!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Your community alert has been posted.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))
                    LBOButton(
                        text = "Go Back",
                        onClick = onBack,
                        modifier = Modifier.width(200.dp)
                    )
                }
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "New Alert / Announcement",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(16.dp))

                        LBOTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = "Title",
                            leadingIcon = Icons.Filled.Title
                        )
                        Spacer(Modifier.height(12.dp))
                        LBOTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Description",
                            leadingIcon = Icons.Filled.Description,
                            singleLine = false,
                            maxLines = 6
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                LBOButton(
                    text = "Publish Post",
                    onClick = {
                        onCreatePost(title, description)
                        isPosted = true
                    },
                    enabled = title.isNotBlank() && description.isNotBlank(),
                    icon = Icons.Filled.Send
                )
            }
        }
    }
}
