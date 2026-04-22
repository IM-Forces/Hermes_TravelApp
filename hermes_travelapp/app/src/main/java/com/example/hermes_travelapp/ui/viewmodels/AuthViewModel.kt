package com.example.hermes_travelapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.domain.repository.AuthRepository
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
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signIn(email, password)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Success
                },
                onFailure = { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "Authentication failed")
                }
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
    
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
