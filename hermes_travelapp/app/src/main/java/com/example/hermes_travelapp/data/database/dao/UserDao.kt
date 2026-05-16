package com.example.hermes_travelapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hermes_travelapp.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the users table.
 */
@Dao
interface UserDao {

    /**
     * Inserts a user into the database. If the user already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Retrieves a user by their unique identifier.
     */
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    /**
     * Observes a user by their unique identifier.
     */
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserFlowById(id: String): Flow<UserEntity?>

    /**
     * Retrieves a user by their contact email.
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * Updates an existing user's information.
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Deletes a user from the database by their ID.
     */
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: String)

    /**
     * Checks if a username is already taken by another user.
     * Returns true if the username exists, false otherwise.
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username")
    suspend fun isUsernameTaken(username: String): Boolean

    /**
     * Observes all users in the database.
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
}
