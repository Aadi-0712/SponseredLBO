package com.lbo.app.data.model

data class Booking(
    val bookingId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val category: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = STATUS_REQUESTED,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_REQUESTED = "Requested"
        const val STATUS_ACCEPTED = "Accepted"
        const val STATUS_REJECTED = "Rejected"
        const val STATUS_COMPLETED = "Completed"
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "bookingId" to bookingId,
        "customerId" to customerId,
        "customerName" to customerName,
        "providerId" to providerId,
        "providerName" to providerName,
        "category" to category,
        "date" to date,
        "time" to time,
        "status" to status,
        "createdAt" to createdAt
    )
}
