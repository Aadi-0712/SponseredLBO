package com.lbo.app.domain.repository

import com.lbo.app.data.model.Review
import com.lbo.app.utils.Resource

interface ReviewRepository {
    suspend fun addReview(review: Review): Resource<Boolean>
    suspend fun getReviewsByProvider(providerId: String): Resource<List<Review>>
    suspend fun getReviewByBooking(bookingId: String): Resource<Review?>
}
