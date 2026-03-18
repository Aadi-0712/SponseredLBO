package com.lbo.app.domain.repository

import com.lbo.app.data.model.Booking
import com.lbo.app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Resource<Boolean>
    suspend fun updateBookingStatus(bookingId: String, status: String): Resource<Boolean>
    suspend fun getBooking(bookingId: String): Resource<Booking>
    suspend fun getBookingsByCustomer(customerId: String): Resource<List<Booking>>
    suspend fun getBookingsByProvider(providerId: String): Resource<List<Booking>>
    suspend fun getAllBookings(): Resource<List<Booking>>
    fun observeBookingsByCustomer(customerId: String): Flow<Resource<List<Booking>>>
    fun observeBookingsByProvider(providerId: String): Flow<Resource<List<Booking>>>
}
