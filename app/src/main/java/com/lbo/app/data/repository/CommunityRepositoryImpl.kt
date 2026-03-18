package com.lbo.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.lbo.app.data.model.CommunityPost
import com.lbo.app.domain.repository.CommunityRepository
import com.lbo.app.utils.Constants
import com.lbo.app.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommunityRepository {

    private val postsCollection = firestore.collection(Constants.COLLECTION_COMMUNITY_POSTS)

    override suspend fun createPost(post: CommunityPost): Resource<Boolean> {
        return try {
            val docRef = postsCollection.document()
            val postWithId = post.copy(postId = docRef.id)
            docRef.set(postWithId.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create post")
        }
    }

    override suspend fun getPosts(): Resource<List<CommunityPost>> {
        return try {
            val snapshot = postsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val posts = snapshot.toObjects(CommunityPost::class.java)
            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get posts")
        }
    }

    override suspend fun deletePost(postId: String): Resource<Boolean> {
        return try {
            postsCollection.document(postId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete post")
        }
    }

    override fun observePosts(): Flow<Resource<List<CommunityPost>>> = callbackFlow {
        var registration: ListenerRegistration? = null
        try {
            registration = postsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Resource.Error(error.message ?: "Error observing posts"))
                        return@addSnapshotListener
                    }
                    val posts = snapshot?.toObjects(CommunityPost::class.java) ?: emptyList()
                    trySend(Resource.Success(posts))
                }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Error observing posts"))
        }
        awaitClose { registration?.remove() }
    }
}
