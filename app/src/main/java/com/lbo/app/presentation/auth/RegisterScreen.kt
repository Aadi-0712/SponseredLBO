package com.lbo.app.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lbo.app.data.model.User
import com.lbo.app.presentation.components.LBOButton
import com.lbo.app.presentation.components.LBOOutlinedButton
import com.lbo.app.presentation.components.LBOTextField
import com.lbo.app.presentation.theme.*

@Composable
fun RegisterScreen(
    authState: AuthState,
    onRegister: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(User.ROLE_CUSTOMER) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background gradient header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(GradientBlueStart, GradientBlueEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Join our community today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Content card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 140.dp, bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LBOTextField(
                    value = name,
                    onValueChange = { name = it; onClearError() },
                    label = "Full Name",
                    leadingIcon = Icons.Filled.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                LBOTextField(
                    value = email,
                    onValueChange = { email = it; onClearError() },
                    label = "Email",
                    leadingIcon = Icons.Filled.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                LBOTextField(
                    value = password,
                    onValueChange = { password = it; onClearError() },
                    label = "Password",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "I am a:",
                    modifier = Modifier.align(Alignment.Start),
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = role == User.ROLE_CUSTOMER,
                        onClick = { role = User.ROLE_CUSTOMER },
                        label = { Text("Customer") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = role == User.ROLE_PROVIDER,
                        onClick = { role = User.ROLE_PROVIDER },
                        label = { Text("Service Provider") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Error message
                AnimatedVisibility(visible = authState.error != null) {
                    authState.error?.let {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorLight
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = it,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                LBOButton(
                    text = "Register",
                    onClick = { onRegister(name, email, password, role) },
                    isLoading = authState.isLoading,
                    enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        "Already have an account? Login",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
