package com.lbo.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.lbo.app.data.model.Provider
import com.lbo.app.data.model.ProviderSearchIndex
import com.lbo.app.domain.repository.ProviderRepository
import com.lbo.app.utils.Constants
import com.lbo.app.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProviderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProviderRepository {

    private val providersCollection = firestore.collection(Constants.COLLECTION_PROVIDERS)

    override suspend fun createProvider(provider: Provider): Resource<Boolean> {
        return try {
            val docRef = if (provider.providerId.isEmpty()) {
                providersCollection.document()
            } else {
                providersCollection.document(provider.providerId)
            }
            val providerWithId = provider.copy(providerId = docRef.id)
            docRef.set(providerWithId.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create provider")
        }
    }

    override suspend fun updateProvider(provider: Provider): Resource<Boolean> {
        return try {
            providersCollection.document(provider.providerId)
                .set(provider.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update provider")
        }
    }

    override suspend fun getProvider(providerId: String): Resource<Provider> {
        return try {
            val doc = providersCollection.document(providerId).get().await()
            val provider = doc.toObject(Provider::class.java)
                ?: return Resource.Error("Provider not found")
            Resource.Success(provider)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get provider")
        }
    }

    override suspend fun getProviderByUserId(userId: String): Resource<Provider?> {
        return try {
            val snapshot = providersCollection
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()
            val provider = snapshot.toObjects(Provider::class.java).firstOrNull()
            Resource.Success(provider)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get provider")
        }
    }

    override suspend fun getAllProviders(): Resource<List<Provider>> {
        return try {
            val snapshot = providersCollection.get().await()
            val providers = snapshot.toObjects(Provider::class.java)
            Resource.Success(providers)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get providers")
        }
    }

    override suspend fun getApprovedProviders(): Resource<List<Provider>> {
        return try {
            val snapshot = providersCollection
                .whereEqualTo("isApproved", true)
                .get()
                .await()
            val providers = snapshot.toObjects(Provider::class.java)
            Resource.Success(providers)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get approved providers")
        }
    }

    override suspend fun getPendingProviders(): Resource<List<Provider>> {
        return try {
            val snapshot = providersCollection
                .whereEqualTo("isApproved", false)
                .get()
                .await()
            val providers = snapshot.toObjects(Provider::class.java)
            Resource.Success(providers)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get pending providers")
        }
    }

    override suspend fun getProvidersByCategory(category: String): Resource<List<Provider>> {
        return try {
            val snapshot = providersCollection
                .whereEqualTo("category", category)
                .whereEqualTo("isApproved", true)
                .get()
                .await()
            val providers = snapshot.toObjects(Provider::class.java)
            Resource.Success(providers)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get providers by category")
        }
    }

    override suspend fun searchProviders(query: String): Resource<List<Provider>> {
        return try {
            val snapshot = providersCollection
                .whereEqualTo("isApproved", true)
                .get()
                .await()
            val providers = snapshot.toObjects(Provider::class.java)
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.location.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
                }
            Resource.Success(providers)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search failed")
        }
    }

    override suspend fun approveProvider(providerId: String): Resource<Boolean> {
        return try {
            providersCollection.document(providerId)
                .update("isApproved", true).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to approve provider")
        }
    }

    override suspend fun rejectProvider(providerId: String): Resource<Boolean> {
        return try {
            providersCollection.document(providerId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to reject provider")
        }
    }

    override suspend fun getTopRatedProviders(limit: Int): Resource<List<Provider>> {
        return try {
            val snapshot = providersCollection
                .whereEqualTo("isApproved", true)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            val providers = snapshot.toObjects(Provider::class.java)
            Resource.Success(providers)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get top rated providers")
        }
    }

    override suspend fun updateRating(
        providerId: String,
        rating: Double,
        totalReviews: Int
    ): Resource<Boolean> {
        return try {
            providersCollection.document(providerId)
                .update(
                    mapOf(
                        "rating" to rating,
                        "totalReviews" to totalReviews
                    )
                ).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update rating")
        }
    }

    override suspend fun getProvidersByCluster(cluster: String): Resource<List<Provider>> {
        return try {
            val snapshot = providersCollection
                .whereEqualTo("cluster", cluster)
                .whereEqualTo("isApproved", true)
                .get()
                .await()
            Resource.Success(snapshot.toObjects(Provider::class.java))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get providers for cluster")
        }
    }

    override fun observeProviders(): Flow<Resource<List<Provider>>> = callbackFlow {
        var registration: ListenerRegistration? = null
        try {
            registration = providersCollection
                .whereEqualTo("isApproved", true)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Resource.Error(error.message ?: "Error observing providers"))
                        return@addSnapshotListener
                    }
                    val providers = snapshot?.toObjects(Provider::class.java) ?: emptyList()
                    trySend(Resource.Success(providers))
                }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Error observing providers"))
        }
        awaitClose { registration?.remove() }
    }

    override suspend fun getProvidersPaged(
        limit: Int,
        lastDocument: DocumentSnapshot?
    ): Resource<Pair<List<ProviderSearchIndex>, DocumentSnapshot?>> {
        return try {
            var query = firestore.collection(Constants.COLLECTION_PROVIDER_SEARCH_INDEX)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(limit.toLong())

            if (lastDocument != null) {
                query = query.startAfter(lastDocument)
            }

            val snapshot = query.get().await()
            val providers = snapshot.toObjects(ProviderSearchIndex::class.java)
            val nextDoc = if (snapshot.size() > 0) snapshot.documents[snapshot.size() - 1] else null

            Resource.Success(Pair(providers, nextDoc))
        } catch (e: Exception) {
            // Fallback to providers collection if index doesn't exist yet for seamless testing
            try {
                var backupQuery = providersCollection
                    .whereEqualTo("isApproved", true)
                    .orderBy("rating", Query.Direction.DESCENDING)
                    .limit(limit.toLong())
                if (lastDocument != null) {
                    backupQuery = backupQuery.startAfter(lastDocument)
                }
                val snapshot = backupQuery.get().await()
                val providers = snapshot.toObjects(Provider::class.java).map {
                    ProviderSearchIndex(it.providerId, it.name, it.category, it.location, it.rating, it.experience)
                }
                val nextDoc = if (snapshot.size() > 0) snapshot.documents[snapshot.size() - 1] else null
                Resource.Success(Pair(providers, nextDoc))
            } catch (ex: Exception) {
                Resource.Error(ex.message ?: "Failed to get paged providers")
            }
        }
    }

    override suspend fun searchProvidersPaged(
        query: String,
        limit: Int,
        lastDocument: DocumentSnapshot?
    ): Resource<Pair<List<ProviderSearchIndex>, DocumentSnapshot?>> {
        return try {
            // Assuming basic client-side filtering on a larger paged request, 
            // since Firebase doesn't fully support native full-text search.
            // In a real prod environment this would query Algolia or Typesense.
            var firestoreQuery = firestore.collection(Constants.COLLECTION_PROVIDER_SEARCH_INDEX)
                .orderBy("name") // Requires index
                .limit(limit.toLong())

            if (lastDocument != null) {
                firestoreQuery = firestoreQuery.startAfter(lastDocument)
            }

            val snapshot = firestoreQuery.get().await()
            val providers = snapshot.toObjects(ProviderSearchIndex::class.java)
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.city.contains(query, ignoreCase = true)
                }
            
            val nextDoc = if (snapshot.size() > 0) snapshot.documents[snapshot.size() - 1] else null
            Resource.Success(Pair(providers, nextDoc))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search pagination failed")
        }
    }
}
