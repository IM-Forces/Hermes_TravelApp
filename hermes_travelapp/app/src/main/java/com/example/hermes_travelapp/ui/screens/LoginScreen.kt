package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.AuthUiState
import com.example.hermes_travelapp.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logofinal),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(250.dp).padding(bottom = 32.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 32.dp),
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; authViewModel.resetState()},
                label = { Text(stringResource(R.string.auth_email), style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
                enabled = uiState !is AuthUiState.Loading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; authViewModel.resetState()},
                label = { Text(stringResource(R.string.auth_password), style = MaterialTheme.typography.bodyMedium) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
                enabled = uiState !is AuthUiState.Loading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
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
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { authViewModel.signIn(email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.login_title), color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onNavigateToRegister,
                enabled = uiState !is AuthUiState.Loading
            ) {
                Text(
                    stringResource(R.string.auth_no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            TextButton(onClick = onNavigateToForgotPassword) {
                Text(
                    text = stringResource(R.string.forgot_password_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
