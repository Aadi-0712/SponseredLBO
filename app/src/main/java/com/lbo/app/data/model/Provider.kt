package com.lbo.app.data.model

data class Provider(
    val providerId: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "",
    val location: String = "",
    val description: String = "",
    val experience: Int = 0,
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    val isApproved: Boolean = false,
    val profileImage: String = "",
    val documentsUrl: List<String> = emptyList(),
    val cluster: String = "",
    val tags: List<String> = emptyList(),
    val lastUpdatedAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "providerId" to providerId,
        "userId" to userId,
        "name" to name,
        "category" to category,
        "location" to location,
        "description" to description,
        "experience" to experience,
        "rating" to rating,
        "totalReviews" to totalReviews,
        "isApproved" to isApproved,
        "profileImage" to profileImage,
        "documentsUrl" to documentsUrl,
        "cluster" to cluster,
        "tags" to tags,
        "lastUpdatedAt" to lastUpdatedAt,
        "createdAt" to createdAt
    )
}
