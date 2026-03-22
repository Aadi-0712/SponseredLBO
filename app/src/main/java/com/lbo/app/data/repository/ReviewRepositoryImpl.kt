package com.lbo.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lbo.app.data.model.Review
import com.lbo.app.domain.repository.ReviewRepository
import com.lbo.app.utils.Constants
import com.lbo.app.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    private val reviewsCollection = firestore.collection(Constants.COLLECTION_REVIEWS)

    override suspend fun addReview(review: Review): Resource<Boolean> {
        return try {
            // Immutable Rule: 1 review per booking. Protects against client-side spam/bypasses.
            val existingReviewSnapshot = reviewsCollection
                .whereEqualTo("bookingId", review.bookingId)
                .limit(1)
                .get()
                .await()
                
            if (!existingReviewSnapshot.isEmpty) {
                return Resource.Error("You have already submitted a public review for this booking. Reviews are immutable.")
            }

            val docRef = reviewsCollection.document()
            val reviewWithId = review.copy(reviewId = docRef.id)
            docRef.set(reviewWithId.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add review")
        }
    }

    override suspend fun getReviewsByProvider(providerId: String): Resource<List<Review>> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("providerId", providerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val reviews = snapshot.toObjects(Review::class.java)
            Resource.Success(reviews)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get reviews")
        }
    }

    override suspend fun getReviewByBooking(bookingId: String): Resource<Review?> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("bookingId", bookingId)
                .limit(1)
                .get()
                .await()
            val review = snapshot.toObjects(Review::class.java).firstOrNull()
            Resource.Success(review)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get review")
        }
    }
}
