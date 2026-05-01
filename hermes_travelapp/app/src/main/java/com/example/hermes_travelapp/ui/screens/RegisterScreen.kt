package com.example.hermes_travelapp.ui.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.AuthUiState
import com.example.hermes_travelapp.ui.viewmodels.AuthViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    Log.d("AuthUI", "RegisterScreen: Composición de la pantalla.")
    var username by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            snackbarHostState.showSnackbar(
                context.getString(R.string.register_verification_sent)
            )
            viewModel.resetState()
            onRegisterClick()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        birthDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        viewModel.resetState()
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                        .padding(top = 48.dp, bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.logofinal),
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.auth_create_account),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; viewModel.resetState() },
                        label = { Text(stringResource(R.string.auth_username)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = uiState !is AuthUiState.Loading,
                        isError = viewModel.usernameError != null
                    )

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)) {
                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { },
                            label = { Text(stringResource(R.string.auth_dob)) },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            readOnly = true,
                            enabled = uiState !is AuthUiState.Loading,
                            isError = false
                        )
                        if (uiState !is AuthUiState.Loading) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showDatePicker = true }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; viewModel.resetState() },
                        label = { Text(stringResource(R.string.auth_email)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = uiState !is AuthUiState.Loading,
                        isError = viewModel.emailError != null
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; viewModel.resetState() },
                        label = { Text(stringResource(R.string.auth_password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = uiState !is AuthUiState.Loading,
                        isError = viewModel.passwordError != null
                    )

                    OutlinedTextField(
                        value = repeatPassword,
                        onValueChange = { repeatPassword = it; viewModel.resetState() },
                        label = { Text(stringResource(R.string.auth_repeat_password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val icon = if (repeatPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { repeatPasswordVisible = !repeatPasswordVisible }) {
                                Icon(icon, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = uiState !is AuthUiState.Loading,
                        isError = viewModel.confirmPasswordError != null
                    )

                    if (uiState is AuthUiState.Error) {
                        val errorCode = (uiState as AuthUiState.Error).errorCode
                        val errorMessage = when (errorCode) {
                            "ERROR_INVALID_EMAIL",
                            "ERROR_WRONG_PASSWORD",
                            "ERROR_INVALID_CREDENTIAL" -> stringResource(R.string.error_auth_invalid_credentials)

                            "ERROR_USER_NOT_FOUND",
                            "ERROR_USER_DISABLED" -> stringResource(R.string.error_auth_user_not_found)

                            "ERROR_NETWORK_REQUEST_FAILED" -> stringResource(R.string.error_auth_network_error)

                            "ERROR_TOO_MANY_REQUESTS" -> stringResource(R.string.error_auth_too_many_requests)

                            "ERROR_EMPTY_FIELDS" -> stringResource(R.string.error_auth_empty_fields)

                            "ERROR_EMPTY_USERNAME" -> stringResource(R.string.error_username_required)
                            "ERROR_WEAK_PASSWORD" -> stringResource(R.string.error_password_length)
                            "ERROR_EMPTY_PASSWORD" -> stringResource(R.string.error_password_required)
                            "ERROR_EMPTY_CONFIRM_PASSWORD" -> stringResource(R.string.error_confirm_password_required)
                            "ERROR_PASSWORD_MISMATCH" -> stringResource(R.string.error_password_mismatch)
                            "ERROR_USERNAME_TAKEN" -> stringResource(R.string.error_username_taken)
                            "ERROR_EMAIL_ALREADY_IN_USE" -> stringResource(R.string.error_email_already_in_use)

                            else -> stringResource(R.string.error_auth_unknown)
                        }

                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState is AuthUiState.Loading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Button(
                            onClick = {
                                Log.d("AuthUI", "RegisterScreen: Usuario pulsó el botón de registro.")
                                viewModel.registerWithFirebase(
                                    username = username,
                                    birthDate = birthDate,
                                    email = email,
                                    password = password,
                                    confirmPassword = repeatPassword
                                )
                            },
                            enabled = uiState !is AuthUiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.register_title),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onNavigateToLogin, enabled = uiState !is AuthUiState.Loading) {
                        Text(
                            text = stringResource(R.string.auth_has_account),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Register Mode Light")
@Composable
fun RegisterScreenPreviewLight() {
    Hermes_travelappTheme(darkTheme = false) {
        RegisterScreen()
    }
}

@Preview(showBackground = true, name = "Register Mode Dark")
@Composable
fun RegisterScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        RegisterScreen()
    }
}
