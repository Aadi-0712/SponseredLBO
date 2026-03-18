package com.lbo.app.domain.repository

import com.lbo.app.data.model.User
import com.lbo.app.utils.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun isLoggedIn(): Boolean
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun register(email: String, password: String, name: String, role: String): Resource<FirebaseUser>
    suspend fun googleSignIn(idToken: String): Resource<FirebaseUser>
    suspend fun forgotPassword(email: String): Resource<Boolean>
    suspend fun logout()
    suspend fun getCurrentUserData(): Resource<User>
}
