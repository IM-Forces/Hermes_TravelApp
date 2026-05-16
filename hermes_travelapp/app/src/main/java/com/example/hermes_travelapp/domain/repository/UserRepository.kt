package com.example.hermes_travelapp.domain.repository

import com.example.hermes_travelapp.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUser(user: UserEntity): Result<Unit>
    suspend fun getUserById(id: String): UserEntity?
    fun getUserFlow(id: String): Flow<UserEntity?>
    suspend fun getUserByEmail(email: String): UserEntity?
    suspend fun updateUser(user: UserEntity): Result<Unit>
    suspend fun isUsernameTaken(username: String): Boolean
}
