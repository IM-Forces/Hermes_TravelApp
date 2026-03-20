package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.AccountViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AccountScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AccountViewModel = viewModel()
) {
    val username by viewModel.username.collectAsState()
    val birthDate by viewModel.birthDate.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val dateError by viewModel.dateError.collectAsState()

    AccountScreenContent(
        username = username,
        birthDate = birthDate,
        email = email,
        password = password,
        dateError = dateError,
        onNavigateBack = onNavigateBack,
        onUpdateUsername = { viewModel.updateUsername(it) },
        onUpdateBirthDate = { viewModel.updateBirthDate(it) },
        onUpdateEmail = { viewModel.updateEmail(it) },
        onUpdatePassword = { viewModel.updatePassword(it) },
        onSaveAccount = { viewModel.saveAccount() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenContent(
    username: String,
    birthDate: String,
    email: String,
    password: String,
    dateError: String?,
    onNavigateBack: () -> Unit,
    onUpdateUsername: (String) -> Unit,
    onUpdateBirthDate: (String) -> Unit,
    onUpdateEmail: (String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onSaveAccount: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val initials = if (username.length >= 2) username.take(2).uppercase() else username.uppercase()

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        onUpdateBirthDate(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_account)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = username,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUpdateUsername,
                    label = { Text(stringResource(R.string.account_username)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { },
                        label = { Text(stringResource(R.string.account_birthdate)) },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        isError = dateError != null,
                        supportingText = dateError?.let { { Text(it) } }
                    )
                    // Cover the field to capture clicks without making it enabled=false (which changes style)
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = onUpdateEmail,
                    label = { Text(stringResource(R.string.account_email)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = onUpdatePassword,
                    label = { Text(stringResource(R.string.account_password)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Button(
                    onClick = onSaveAccount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 32.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.account_save),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Account Light")
@Composable
fun AccountScreenPreviewLight() {
    Hermes_travelappTheme(darkTheme = false) {
        AccountScreenContent(
            username = "Vítor Da Silva",
            birthDate = "01/01/1990",
            email = "vitor.dasilva@example.com",
            password = "password123",
            dateError = null,
            onNavigateBack = {},
            onUpdateUsername = {},
            onUpdateBirthDate = {},
            onUpdateEmail = {},
            onUpdatePassword = {},
            onSaveAccount = {}
        )
    }
}

@Preview(showBackground = true, name = "Account Dark")
@Composable
fun AccountScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        AccountScreenContent(
            username = "Vítor Da Silva",
            birthDate = "01/01/1990",
            email = "vitor.dasilva@example.com",
            password = "password123",
            dateError = null,
            onNavigateBack = {},
            onUpdateUsername = {},
            onUpdateBirthDate = {},
            onUpdateEmail = {},
            onUpdatePassword = {},
            onSaveAccount = {}
        )
    }
}
