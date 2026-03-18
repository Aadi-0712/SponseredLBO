package com.lbo.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lbo.app.data.model.User
import com.lbo.app.domain.repository.UserRepository
import com.lbo.app.utils.Constants
import com.lbo.app.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection(Constants.COLLECTION_USERS)

    override suspend fun createUser(user: User): Resource<Boolean> {
        return try {
            usersCollection.document(user.userId).set(user.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create user")
        }
    }

    override suspend fun getUser(userId: String): Resource<User> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val user = doc.toObject(User::class.java)
                ?: return Resource.Error("User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user")
        }
    }

    override suspend fun updateUser(user: User): Resource<Boolean> {
        return try {
            usersCollection.document(user.userId).set(user.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update user")
        }
    }

    override suspend fun getAllUsers(): Resource<List<User>> {
        return try {
            val snapshot = usersCollection.get().await()
            val users = snapshot.toObjects(User::class.java)
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get users")
        }
    }

    override suspend fun getUsersByRole(role: String): Resource<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("role", role)
                .get()
                .await()
            val users = snapshot.toObjects(User::class.java)
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get users by role")
        }
    }

    override suspend fun updateFcmToken(userId: String, token: String): Resource<Boolean> {
        return try {
            usersCollection.document(userId).update("fcmToken", token).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update FCM token")
        }
    }

    override fun observeUser(userId: String): Flow<Resource<User>> = callbackFlow {
        var registration: ListenerRegistration? = null
        try {
            registration = usersCollection.document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Resource.Error(error.message ?: "Error observing user"))
                        return@addSnapshotListener
                    }
                    val user = snapshot?.toObject(User::class.java)
                    if (user != null) {
                        trySend(Resource.Success(user))
                    }
                }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Error observing user"))
        }
        awaitClose { registration?.remove() }
    }
}
