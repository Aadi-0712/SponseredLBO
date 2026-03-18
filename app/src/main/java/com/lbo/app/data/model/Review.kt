package com.lbo.app.data.model

data class Review(
    val reviewId: String = "",
    val bookingId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val providerId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "reviewId" to reviewId,
        "bookingId" to bookingId,
        "customerId" to customerId,
        "customerName" to customerName,
        "providerId" to providerId,
        "rating" to rating,
        "comment" to comment,
        "createdAt" to createdAt
    )
}
