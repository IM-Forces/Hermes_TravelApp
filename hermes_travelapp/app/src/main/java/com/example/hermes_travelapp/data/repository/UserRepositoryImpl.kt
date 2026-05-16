package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.database.dao.UserDao
import com.example.hermes_travelapp.data.database.entities.UserEntity
import com.example.hermes_travelapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    private val TAG = "UserRepository"

    override suspend fun createUser(user: UserEntity): Result<Unit> {
        Log.d(TAG, "Creating user: ${user.username}")
        return try {
            if (userDao.isUsernameTaken(user.username)) {
                Log.w(TAG, "Username already in use: ${user.username}")
                Result.failure(Exception("Username already in use"))
            } else {
                userDao.insertUser(user)
                Log.i(TAG, "User created successfully: ${user.id}")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserById(id: String): UserEntity? {
        Log.d(TAG, "Getting user by ID: $id")
        return userDao.getUserById(id)
    }

    override fun getUserFlow(id: String): Flow<UserEntity?> {
        Log.d(TAG, "Watching user by ID: $id")
        return userDao.getUserFlowById(id)
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        Log.d(TAG, "Getting user by email: $email")
        return userDao.getUserByEmail(email)
    }

    override suspend fun updateUser(user: UserEntity): Result<Unit> {
        Log.d(TAG, "Updating user: ${user.id}")
        return try {
            userDao.updateUser(user)
            Log.i(TAG, "User updated successfully: ${user.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        Log.d(TAG, "Checking if username is taken: $username")
        val taken = userDao.isUsernameTaken(username)
        Log.d(TAG, "Username $username taken: $taken")
        return taken
    }
}
