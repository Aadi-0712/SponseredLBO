package com.lbo.app.data.model

data class CommunityPost(
    val postId: String = "",
    val title: String = "",
    val description: String = "",
    val authorName: String = "Admin",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "postId" to postId,
        "title" to title,
        "description" to description,
        "authorName" to authorName,
        "createdAt" to createdAt
    )
}
