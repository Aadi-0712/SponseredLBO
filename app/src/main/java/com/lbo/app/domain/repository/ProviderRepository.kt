package com.lbo.app.domain.repository

import com.lbo.app.data.model.Provider
import com.lbo.app.data.model.ProviderSearchIndex
import com.lbo.app.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface ProviderRepository {
    suspend fun createProvider(provider: Provider): Resource<Boolean>
    suspend fun updateProvider(provider: Provider): Resource<Boolean>
    suspend fun getProvider(providerId: String): Resource<Provider>
    suspend fun getProviderByUserId(userId: String): Resource<Provider?>
    suspend fun getAllProviders(): Resource<List<Provider>>
    suspend fun getApprovedProviders(): Resource<List<Provider>>
    suspend fun getPendingProviders(): Resource<List<Provider>>
    suspend fun getProvidersByCategory(category: String): Resource<List<Provider>>
    suspend fun searchProviders(query: String): Resource<List<Provider>>
    suspend fun approveProvider(providerId: String): Resource<Boolean>
    suspend fun rejectProvider(providerId: String): Resource<Boolean>
    suspend fun getTopRatedProviders(limit: Int = 5): Resource<List<Provider>>
    suspend fun getProvidersByCluster(cluster: String): Resource<List<Provider>>
    suspend fun updateRating(providerId: String, rating: Double, totalReviews: Int): Resource<Boolean>
    fun observeProviders(): Flow<Resource<List<Provider>>>

    // Paged Queries (Infinite Scroll) via CQRS Read Collections
    suspend fun getProvidersPaged(
        limit: Int = 20, 
        lastDocument: DocumentSnapshot? = null
    ): Resource<Pair<List<ProviderSearchIndex>, DocumentSnapshot?>>
    
    suspend fun searchProvidersPaged(
        query: String, 
        limit: Int = 20, 
        lastDocument: DocumentSnapshot? = null
    ): Resource<Pair<List<ProviderSearchIndex>, DocumentSnapshot?>>
}
