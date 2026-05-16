package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.data.PreferencesManager
import com.example.hermes_travelapp.data.database.entities.UserEntity
import com.example.hermes_travelapp.domain.repository.AuthRepository
import com.example.hermes_travelapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _username = MutableStateFlow(preferencesManager.username)
    val username: StateFlow<String> = _username.asStateFlow()

    private val _birthDate = MutableStateFlow(preferencesManager.dateOfBirth)
    val birthDate: StateFlow<String> = _birthDate.asStateFlow()

    private val _email = MutableStateFlow(preferencesManager.email)
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var observationJob: kotlinx.coroutines.Job? = null

    init {
        observeUserData()
    }

    private fun observeUserData() {
        val uid = authRepository.getCurrentUserId()
        Log.d("AccountViewModel", "Observing user data for UID: $uid")
        
        if (uid == null) return
        
        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            userRepository.getUserFlow(uid).collect { user ->
                Log.d("AccountViewModel", "Received user from DB: ${user?.username}")
                user?.let {
                    _username.value = it.username
                    _email.value = it.email
                    
                    val bDate = if (it.birthdate != 0L) {
                        try {
                            LocalDate.ofEpochDay(it.birthdate).format(formatter)
                        } catch (_: Exception) { "" }
                    } else ""
                    _birthDate.value = bDate

                    // Sync with preferences
                    preferencesManager.username = it.username
                    preferencesManager.email = it.email
                    preferencesManager.dateOfBirth = bDate
                }
            }
        }
    }

    fun loadUserData() {
        Log.d("AccountViewModel", "loadUserData called")
        observeUserData()
    }

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updateBirthDate(newBirthDate: String) {
        _birthDate.value = newBirthDate
        validateDate(newBirthDate)
    }

    private fun validateDate(date: String): Boolean {
        if (date.isBlank()) {
            _dateError.value = null
            return true
        }
        return try {
            LocalDate.parse(date, formatter)
            _dateError.value = null
            true
        } catch (e: DateTimeParseException) {
            _dateError.value = "Invalid format. Use DD/MM/YYYY"
            false
        }
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun saveAccount(): Boolean {
        if (_dateError.value != null) return false
        
        val uid = authRepository.getCurrentUserId() ?: return false
        
        Log.d("AccountViewModel", "Saving account info to DB and Prefs")
        
        val birthDateLong = try {
            if (_birthDate.value.isNotBlank()) {
                LocalDate.parse(_birthDate.value, formatter).toEpochDay()
            } else 0L
        } catch (e: Exception) { 0L }

        viewModelScope.launch {
            val currentUser = userRepository.getUserById(uid)
            val updatedUser = currentUser?.copy(
                username = _username.value,
                email = _email.value,
                birthdate = birthDateLong
            ) ?: UserEntity(
                id = uid,
                name = _username.value,
                email = _email.value,
                login = _email.value,
                username = _username.value,
                birthdate = birthDateLong,
                address = "",
                country = "",
                phone = "",
                acceptEmails = false,
                profileInitials = if (_username.value.isNotEmpty()) _username.value.take(2).uppercase() else "U",
                activeTripCount = 0,
                countriesVisited = 0
            )
            
            userRepository.updateUser(updatedUser)
        }

        preferencesManager.username = _username.value
        preferencesManager.dateOfBirth = _birthDate.value
        preferencesManager.email = _email.value
        
        return true
    }
}
