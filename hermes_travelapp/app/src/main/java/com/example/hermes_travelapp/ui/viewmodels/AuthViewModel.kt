package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.repository.AuthRepository
import com.example.hermes_travelapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: Int, val errorCode: String? = null) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var usernameError by mutableStateOf<Int?>(null)
    var emailError by mutableStateOf<Int?>(null)
    var passwordError by mutableStateOf<Int?>(null)
    var confirmPasswordError by mutableStateOf<Int?>(null)

    fun registerWithFirebase(
        username: String,
        birthDate: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        Log.d("AuthVM", "Iniciando registro para: $username ($email)")
        usernameError = if (username.isBlank()) R.string.error_username_required else null
        emailError = when {
            email.isBlank() -> R.string.error_email_required
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.error_email_invalid
            else -> null
        }
        passwordError = when {
            password.isBlank() -> R.string.error_password_required
            password.length < 6 -> R.string.error_password_length
            else -> null
        }
        confirmPasswordError = when {
            confirmPassword.isBlank() -> R.string.error_confirm_password_required
            password != confirmPassword -> R.string.error_password_mismatch
            else -> null
        }

        if (usernameError != null || emailError != null || 
            passwordError != null || confirmPasswordError != null) {
            
            val errorCode = when {
                usernameError != null -> "ERROR_EMPTY_USERNAME"
                emailError == R.string.error_email_required -> "ERROR_EMPTY_EMAIL"
                emailError == R.string.error_email_invalid -> "ERROR_INVALID_EMAIL"
                passwordError == R.string.error_password_required -> "ERROR_EMPTY_PASSWORD"
                passwordError == R.string.error_password_length -> "ERROR_WEAK_PASSWORD"
                confirmPasswordError == R.string.error_confirm_password_required -> "ERROR_EMPTY_CONFIRM_PASSWORD"
                confirmPasswordError == R.string.error_password_mismatch -> "ERROR_PASSWORD_MISMATCH"
                else -> null
            }

            _uiState.value = AuthUiState.Error(
                usernameError ?: emailError ?: passwordError ?: confirmPasswordError ?: R.string.error_auth_unknown,
                errorCode
            )
            Log.w("AuthVM", "Registro cancelado: Errores de validación local ($errorCode)")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            if (userRepository.isUsernameTaken(username)) {
                Log.w("AuthVM", "Registro fallido: El nombre de usuario '$username' ya existe.")
                _uiState.value = AuthUiState.Error(R.string.error_username_taken, "ERROR_USERNAME_TAKEN")
                return@launch
            }

            authRepository.register(email, password, username, birthDate)
                .onSuccess { 
                    Log.i("AuthVM", "Registro exitoso para: $email")
                    _uiState.value = AuthUiState.Success 
                }
                .onFailure { error ->
                    val errorCode = (error as? FirebaseAuthException)?.errorCode ?: "ERROR_UNKNOWN"
                    Log.e("AuthVM", "Error en registro Firebase: $errorCode", error)
                    _uiState.value = AuthUiState.Error(R.string.error_register_failed, errorCode) 
                }
        }
    }

    fun signIn(email: String, password: String) {
        Log.d("AuthVM", "Intentando login para: $email")
        if (email.isBlank() || password.isBlank()) {
            Log.w("AuthVM", "Login cancelado: Campos vacíos")
            _uiState.value = AuthUiState.Error(R.string.error_auth_empty_fields, "ERROR_EMPTY_FIELDS")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signIn(email, password)
            result.fold(
                onSuccess = {
                    Log.i("AuthVM", "Login exitoso para: $email")
                    _uiState.value = AuthUiState.Success
                },
                onFailure = { error ->
                    val errorCode = (error as? FirebaseAuthException)?.errorCode ?: "ERROR_INVALID_CREDENTIALS"
                    Log.e("AuthVM", "Error en login Firebase: $errorCode", error)
                    _uiState.value = AuthUiState.Error(R.string.error_auth_invalid_credentials, errorCode)
                }
            )
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error(R.string.error_email_required, "ERROR_EMPTY_EMAIL")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error(R.string.error_email_invalid, "ERROR_INVALID_EMAIL")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _uiState.value = AuthUiState.Success
                }
                .onFailure { error ->
                    val errorCode = (error as? FirebaseAuthException)?.errorCode ?: "ERROR_UNKNOWN"
                    _uiState.value = AuthUiState.Error(R.string.error_forgot_password_failed, errorCode)
                }
        }
    }

    fun signOut() {
        Log.i("AuthVM", "Usuario solicitó cerrar sesión.")
        authRepository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
    
    fun resetState() {
        _uiState.value = AuthUiState.Idle
        usernameError = null
        emailError = null
        passwordError = null
        confirmPasswordError = null
    }

    fun register(
        username: String,
        birthDate: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        registerWithFirebase(username, birthDate, email, password, confirmPassword)
    }
}
