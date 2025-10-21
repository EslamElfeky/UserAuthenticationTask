package com.example.userauthentication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.room.util.copy
import com.example.userauthentication.data.biometric.BiometricAuthManager
import com.example.userauthentication.data.local.SecureStorageManager
import com.example.userauthentication.data.local.SessionManager
import com.example.userauthentication.utils.ValidationUtils
import kotlinx.coroutines.launch
import com.example.userauthentication.data.model.LoginState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val storageManager = remember { SecureStorageManager(context) }
    val sessionManager = remember { SessionManager(context) }

    var state by remember { mutableStateOf(LoginState()) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showBiometric by remember { mutableStateOf(false) }
    var savedEmail by remember { mutableStateOf("") }

    // Check if user exists and biometric is available
    LaunchedEffect(Unit) {
        val user = storageManager.getUser()
        val activity = context as? FragmentActivity

        if (user != null && activity != null) {
            val biometricManager = BiometricAuthManager(activity)
            showBiometric = biometricManager.canAuthenticate()
            savedEmail = user.email
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Login to your account",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                state = state.copy(email = it, emailError = null)
            },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            isError = state.emailError != null,
            supportingText = { state.emailError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                state = state.copy(password = it, passwordError = null)
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        null
                    )
                }
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            isError = state.passwordError != null,
            supportingText = { state.passwordError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                val emailError = ValidationUtils.validateEmail(state.email)
                val passwordError = ValidationUtils.validatePassword(state.password)

                if (emailError != null || passwordError != null) {
                    state = state.copy(
                        emailError = emailError,
                        passwordError = passwordError
                    )
                    return@Button
                }

                scope.launch {
                    state = state.copy(isLoading = true)
                    val user = storageManager.getUser()

                    if (user != null && user.email == state.email &&
                        user.password == state.password) {
                        sessionManager.saveSession(state.email)
                        onLoginSuccess(state.email)
                    } else {
                        state = state.copy(
                            isLoading = false,
                            errorMessage = "Invalid email or password"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        if (showBiometric) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    val activity = context as? FragmentActivity
                    if (activity != null) {
                        val biometricManager = BiometricAuthManager(activity)
                        if (biometricManager.canAuthenticate()) {
                            biometricManager.authenticate(
                                onSuccess = {
                                    scope.launch {
                                        // Use the saved email from storage
                                        val user = storageManager.getUser()
                                        if (user != null) {
                                            sessionManager.saveSession(user.email, biometricEnabled = true)
                                            onLoginSuccess(user.email)
                                        }
                                    }
                                },
                                onError = { error ->
                                    state = state.copy(errorMessage = error)
                                }
                            )
                        } else {
                            state = state.copy(
                                errorMessage = "Biometric authentication is not available on this device"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Fingerprint, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Login with Biometric")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Register")
        }
    }
}