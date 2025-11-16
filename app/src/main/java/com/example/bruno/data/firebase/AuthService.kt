package com.example.bruno.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthService {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser get() = auth.currentUser


    suspend fun createUser(name: String, email: String, pass: String): Result<Unit> {
        return try {

            auth.createUserWithEmailAndPassword(email, pass).await()

            val uid = auth.currentUser!!.uid


            val userData = mapOf(
                "name" to name,
                "email" to email
            )

            db.collection("users").document(uid).set(userData).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun checkUserExists(email: String): Result<Boolean> {
        return try {
            val methods = auth.fetchSignInMethodsForEmail(email).await()
            Result.success(methods.signInMethods?.isNotEmpty() ?: false)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()
}
