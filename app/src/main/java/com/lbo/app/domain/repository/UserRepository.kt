package com.lbo.app.domain.repository

import com.lbo.app.data.model.User
import com.lbo.app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUser(user: User): Resource<Boolean>
    suspend fun getUser(userId: String): Resource<User>
    suspend fun updateUser(user: User): Resource<Boolean>
    suspend fun getAllUsers(): Resource<List<User>>
    suspend fun getUsersByRole(role: String): Resource<List<User>>
    suspend fun updateFcmToken(userId: String, token: String): Resource<Boolean>
    fun observeUser(userId: String): Flow<Resource<User>>
}
