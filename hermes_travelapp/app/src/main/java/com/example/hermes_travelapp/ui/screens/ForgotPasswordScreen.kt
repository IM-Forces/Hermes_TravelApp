package com.example.hermes_travelapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.AuthUiState
import com.example.hermes_travelapp.ui.viewmodels.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            snackbarHostState.showSnackbar(
                context.getString(R.string.forgot_password_success)
            )
            viewModel.resetState()
            onNavigateToLogin()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.forgot_password_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.forgot_password_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    viewModel.resetState()
                },
                label = { Text(stringResource(R.string.forgot_password_email_label)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AuthUiState.Loading,
                isError = uiState is AuthUiState.Error
            )

            if (uiState is AuthUiState.Error) {
                val errorCode = (uiState as AuthUiState.Error).errorCode
                val errorMessage = when (errorCode) {
                    "ERROR_INVALID_EMAIL" -> stringResource(R.string.error_email_invalid)
                    "ERROR_USER_NOT_FOUND" -> stringResource(R.string.error_auth_user_not_found)
                    "ERROR_EMPTY_EMAIL" -> stringResource(R.string.error_email_required)
                    "ERROR_NETWORK_REQUEST_FAILED" -> stringResource(R.string.error_auth_network_error)
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

            Button(
                onClick = { viewModel.forgotPassword(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = uiState !is AuthUiState.Loading
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.forgot_password_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    viewModel.resetState()
                    onNavigateToLogin()
                }
            ) {
                Text(
                    text = stringResource(R.string.forgot_password_back_to_login),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ForgotPasswordScreenPreviewLight() {
    Hermes_travelappTheme {
        ForgotPasswordScreen()
    }
}

@Preview(showBackground = true, name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ForgotPasswordScreenPreviewDark() {
    Hermes_travelappTheme {
        ForgotPasswordScreen()
    }
}
