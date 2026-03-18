package com.lbo.app.data.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = ROLE_CUSTOMER,
    val location: String = "",
    val profileImage: String = "",
    val isVerified: Boolean = false,
    val fcmToken: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROLE_ADMIN = "admin"
        const val ROLE_PROVIDER = "provider"
        const val ROLE_CUSTOMER = "customer"
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "name" to name,
        "email" to email,
        "role" to role,
        "location" to location,
        "profileImage" to profileImage,
        "isVerified" to isVerified,
        "fcmToken" to fcmToken,
        "createdAt" to createdAt
    )
}
