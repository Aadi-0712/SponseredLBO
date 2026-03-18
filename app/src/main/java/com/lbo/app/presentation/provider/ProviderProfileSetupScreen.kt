package com.lbo.app.presentation.provider

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lbo.app.presentation.components.LBOButton
import com.lbo.app.presentation.components.LBOTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderProfileSetupScreen(
    profileState: ProviderProfileState,
    onSaveProfile: (String, String, String, String, String, Uri?, List<Uri>) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(profileState.user?.name ?: "") }
    var category by remember { mutableStateOf("") } // Ideally a dropdown
    var location by remember { mutableStateOf(profileState.user?.location ?: "") }
    var description by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var documentUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Provider Profile") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LBOTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            LBOTextField(
                value = category,
                onValueChange = { category = it },
                label = "Category (e.g. Plumbing, Cleaning)",
                leadingIcon = Icons.Default.Category
            )

            Spacer(modifier = Modifier.height(16.dp))

            LBOTextField(
                value = location,
                onValueChange = { location = it },
                label = "Location",
                leadingIcon = Icons.Default.LocationOn
            )

            Spacer(modifier = Modifier.height(16.dp))

            LBOTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description of Services",
                leadingIcon = Icons.Default.Description
            )

            Spacer(modifier = Modifier.height(16.dp))

            LBOTextField(
                value = experience,
                onValueChange = { experience = it },
                label = "Years of Experience",
                leadingIcon = Icons.Default.Work
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (profileImageUri != null) "Change Profile Image" else "Upload Profile Image")
            }

            Spacer(modifier = Modifier.height(32.dp))

            LBOButton(
                text = "Save Profile",
                onClick = {
                    onSaveProfile(name, category, location, description, experience, profileImageUri, documentUris)
                },
                isLoading = profileState.isLoading,
                enabled = name.isNotBlank() && category.isNotBlank() && location.isNotBlank()
            )
            
            if (profileState.isSuccess) {
                LaunchedEffect(Unit) {
                    onBack()
                }
            }
        }
    }
}
