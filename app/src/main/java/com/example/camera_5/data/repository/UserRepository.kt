package com.example.camera_5.data.repository

import android.util.Log
import com.example.camera_5.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class UserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signup(name: String, email: String, password: String, image: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User creation failed")

            val userModel = UserModel(user.uid, name, email, image)
            firestore.collection("users").document(user.uid).set(userModel).await()
            user
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun resetPassword(email: String, callback: (String?) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback("Password reset email sent.")
                } else {
                    callback("Error: ${task.exception?.message}")
                }
            }
    }

    suspend fun getUserProfile(uid: String): UserModel? {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            var name = snapshot.data?.get("name")
            var email  = snapshot.data?.get("email")
            var uid = snapshot.data?.get("uid")
            var image = snapshot.data?.get("image")
            var user = UserModel(uid.toString(),name.toString(),email.toString(), image.toString())
            user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(uid: String, user: UserModel) {
        firestore.collection("users").document(uid).set(user).await()
    }
}
