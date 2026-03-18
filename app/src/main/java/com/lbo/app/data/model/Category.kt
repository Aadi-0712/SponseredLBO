package com.lbo.app.data.model

data class Category(
    val categoryId: String = "",
    val name: String = "",
    val icon: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "categoryId" to categoryId,
        "name" to name,
        "icon" to icon,
        "createdAt" to createdAt
    )
}
