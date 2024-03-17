package com.example.employe_manage.Modals

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

// User.kt
data class User(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val company: String = "",
    val profession: String = "",
    val phoneNumber: String = "",
    val collage: String = "",
    val school: String = "",
    val city: String = "",
    val county: String = "",
    val image: String = "",
    val connections: MutableList<String> = mutableListOf()  // List to store user IDs of connections
)


object AuthManager {
    val auth = Firebase.auth

    fun createUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun updateUserProfile(name: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { e -> onFailure(e) }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }
}
