package com.lbo.app.domain.repository

import com.lbo.app.data.model.CommunityPost
import com.lbo.app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    suspend fun createPost(post: CommunityPost): Resource<Boolean>
    suspend fun getPosts(): Resource<List<CommunityPost>>
    suspend fun deletePost(postId: String): Resource<Boolean>
    fun observePosts(): Flow<Resource<List<CommunityPost>>>
}
