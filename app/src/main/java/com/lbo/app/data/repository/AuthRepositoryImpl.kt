package com.lbo.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.lbo.app.data.model.User
import com.lbo.app.domain.repository.AuthRepository
import com.lbo.app.utils.Constants
import com.lbo.app.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override fun isLoggedIn(): Boolean = auth.currentUser != null

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { Resource.Success(it) }
                ?: Resource.Error("Login failed")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        role: String
    ): Resource<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.Error("Registration failed")

            val user = User(
                userId = firebaseUser.uid,
                name = name,
                email = email,
                role = role
            )
            // DATA INTEGRITY BREAKPOINT: If the `set` operation below fails (e.g., due to network issues
            // or security rules), the user is created in Firebase Auth but will not have a
            // corresponding user document in Firestore. This will cause errors later in the app.
            // Consider using a Firebase Cloud Function triggered on user creation to reliably create the user document.
            try {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .set(user.toMap())
                    .await()
                Resource.Success(firebaseUser)
            } catch (e: Exception) {
                // Rollback: delete the auth user if choosing not to keep it in a broken state
                firebaseUser.delete().await()
                Resource.Error("Profile creation failed: ${e.message}. Deleted account for retry.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun googleSignIn(idToken: String): Resource<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: return Resource.Error("Google sign-in failed")

            // Check if user exists in Firestore
            val doc = firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!doc.exists()) {
                val user = User(
                    userId = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    role = User.ROLE_CUSTOMER,
                    profileImage = firebaseUser.photoUrl?.toString() ?: ""
                )
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .set(user.toMap())
                    .await()
            }

            Resource.Success(firebaseUser)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google sign-in failed")
        }
    }

    override suspend fun forgotPassword(email: String): Resource<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send reset email")
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUserData(): Resource<User> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not logged in")
            val doc = firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .await()
            val user = doc.toObject(User::class.java)
                ?: return Resource.Error("User data not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user data")
        }
    }
}
