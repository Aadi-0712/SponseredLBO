package com.lbo.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.lbo.app.data.model.Booking
import com.lbo.app.domain.repository.BookingRepository
import com.lbo.app.utils.Constants
import com.lbo.app.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookingRepository {

    private val bookingsCollection = firestore.collection(Constants.COLLECTION_BOOKINGS)

    override suspend fun createBooking(booking: Booking): Resource<Boolean> {
        return try {
            val docRef = bookingsCollection.document()
            val bookingWithId = booking.copy(bookingId = docRef.id)
            docRef.set(bookingWithId.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create booking")
        }
    }

    override suspend fun updateBookingStatus(
        bookingId: String,
        status: String
    ): Resource<Boolean> {
        return try {
            bookingsCollection.document(bookingId)
                .update("status", status).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update booking status")
        }
    }

    override suspend fun getBooking(bookingId: String): Resource<Booking> {
        return try {
            val doc = bookingsCollection.document(bookingId).get().await()
            val booking = doc.toObject(Booking::class.java)
                ?: return Resource.Error("Booking not found")
            Resource.Success(booking)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get booking")
        }
    }

    override suspend fun getBookingsByCustomer(customerId: String): Resource<List<Booking>> {
        return try {
            val snapshot = bookingsCollection
                .whereEqualTo("customerId", customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val bookings = snapshot.toObjects(Booking::class.java)
            Resource.Success(bookings)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get bookings")
        }
    }

    override suspend fun getBookingsByProvider(providerId: String): Resource<List<Booking>> {
        return try {
            val snapshot = bookingsCollection
                .whereEqualTo("providerId", providerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val bookings = snapshot.toObjects(Booking::class.java)
            Resource.Success(bookings)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get bookings")
        }
    }

    override suspend fun getAllBookings(): Resource<List<Booking>> {
        return try {
            val snapshot = bookingsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val bookings = snapshot.toObjects(Booking::class.java)
            Resource.Success(bookings)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get all bookings")
        }
    }

    override fun observeBookingsByCustomer(customerId: String): Flow<Resource<List<Booking>>> =
        callbackFlow {
            var registration: ListenerRegistration? = null
            try {
                registration = bookingsCollection
                    .whereEqualTo("customerId", customerId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(Resource.Error(error.message ?: "Error observing bookings"))
                            return@addSnapshotListener
                        }
                        val bookings = snapshot?.toObjects(Booking::class.java) ?: emptyList()
                        trySend(Resource.Success(bookings))
                    }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message ?: "Error observing bookings"))
            }
            awaitClose { registration?.remove() }
        }

    override fun observeBookingsByProvider(providerId: String): Flow<Resource<List<Booking>>> =
        callbackFlow {
            var registration: ListenerRegistration? = null
            try {
                registration = bookingsCollection
                    .whereEqualTo("providerId", providerId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(Resource.Error(error.message ?: "Error observing bookings"))
                            return@addSnapshotListener
                        }
                        val bookings = snapshot?.toObjects(Booking::class.java) ?: emptyList()
                        trySend(Resource.Success(bookings))
                    }
            } catch (e: Exception) {
                trySend(Resource.Error(e.message ?: "Error observing bookings"))
            }
            awaitClose { registration?.remove() }
        }
}
