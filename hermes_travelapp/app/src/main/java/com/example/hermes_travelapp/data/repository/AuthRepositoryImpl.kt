package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val TAG = "AuthRepositoryImpl"

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            Log.d(TAG, "Attempting to sign in with email: $email")
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Log.i(TAG, "Successfully signed in user: ${firebaseAuth.currentUser?.uid}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed for email $email: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun signOut() {
        val userId = firebaseAuth.currentUser?.uid
        Log.d(TAG, "Signing out user: $userId")
        firebaseAuth.signOut()
        Log.i(TAG, "Successfully signed out")
    }

    override fun isLoggedIn(): Boolean {
        val loggedIn = firebaseAuth.currentUser != null
        Log.v(TAG, "Checking login status: $loggedIn")
        return loggedIn
    }

    override fun getCurrentUserId(): String? {
        val uid = firebaseAuth.currentUser?.uid
        Log.v(TAG, "Current user ID: $uid")
        return uid
    }
}
