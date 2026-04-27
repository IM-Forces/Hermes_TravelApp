package com.example.hermes_travelapp.domain.repository

/**
 * Interface defining authentication operations for the application.
 */
interface AuthRepository {
    /**
     * Signs in a user with the provided email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] indicating success or failure.
     */
    suspend fun signIn(email: String, password: String): Result<Unit>

    /**
     * Signs out the currently authenticated user.
     */
    fun signOut()

    /**
     * Checks if a user is currently logged in.
     *
     * @return True if a user is authenticated, false otherwise.
     */
    fun isLoggedIn(): Boolean

    /**
     * Retrieves the unique identifier of the currently authenticated user.
     *
     * @return The user ID as a [String], or null if no user is authenticated.
     */
    fun getCurrentUserId(): String?

    /**
     * Registers a new user with the provided details.
     */
    suspend fun register(
        email: String,
        password: String,
        username: String,
        birthDate: String
    ): Result<Unit>

    /**
     * Sends a verification email to the currently authenticated user.
     */
    suspend fun sendEmailVerification(): Result<Unit>

    /**
     * Checks if the current user's email is verified.
     */
    suspend fun isEmailVerified(): Boolean

    /**
     * Sends a password reset email to the provided email address.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}
