package com.lbo.app.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lbo.app.data.model.*
import com.lbo.app.domain.repository.*
import com.lbo.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val categories: List<Category> = emptyList(),
    val topRatedProviders: List<User> = emptyList(),
    val communityPosts: List<CommunityPost> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SearchState(
    val providers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = ""
)

data class BookingFormState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val categoryRepository: CategoryRepository,
    private val bookingRepository: BookingRepository,
    private val communityRepository: CommunityRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    private val _selectedProvider = MutableStateFlow<User?>(null)
    val selectedProvider = _selectedProvider.asStateFlow()

    private val _providerReviews = MutableStateFlow<List<Review>>(emptyList())
    val providerReviews = _providerReviews.asStateFlow()

    private val _myBookingsState = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading())
    val myBookingsState = _myBookingsState.asStateFlow()

    private val _bookingFormState = MutableStateFlow(BookingFormState())
    val bookingFormState = _bookingFormState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true)
            
            // In a real app, use combine or zip to load concurrently
            val cats = categoryRepository.getAllCategories()
            val topProviders = providerRepository.getTopRatedProviders()
            val posts = communityRepository.getPosts()

            _homeState.value = HomeState(
                categories = (cats as? Resource.Success)?.data ?: emptyList(),
                topRatedProviders = (topProviders as? Resource.Success)?.data ?: emptyList(),
                communityPosts = (posts as? Resource.Success)?.data ?: emptyList(),
                isLoading = false
            )
        }
    }

    fun searchProviders(query: String) {
        _searchState.value = _searchState.value.copy(query = query, isLoading = true)
        viewModelScope.launch {
            val result = providerRepository.searchProviders(query)
            _searchState.value = _searchState.value.copy(
                providers = (result as? Resource.Success)?.data ?: emptyList(),
                isLoading = false
            )
        }
    }

    fun loadProviderDetails(providerId: String) {
        viewModelScope.launch {
            _selectedProvider.value = (providerRepository.getProvider(providerId) as? Resource.Success)?.data
            _providerReviews.value = (reviewRepository.getReviewsForProvider(providerId) as? Resource.Success)?.data ?: emptyList()
        }
    }

    fun createBooking(providerId: String, providerName: String, category: String, date: String, time: String) {
        viewModelScope.launch {
            _bookingFormState.value = BookingFormState(isLoading = true)
            val booking = Booking(
                providerId = providerId,
                providerName = providerName,
                category = category,
                date = date,
                time = time,
                status = Booking.STATUS_REQUESTED
            )
            val result = bookingRepository.createBooking(booking)
            if (result is Resource.Success) {
                _bookingFormState.value = BookingFormState(isSuccess = true)
            } else {
                _bookingFormState.value = BookingFormState(error = (result as? Resource.Error)?.message ?: "Error")
            }
        }
    }

    fun resetBookingForm() {
        _bookingFormState.value = BookingFormState()
    }

    fun loadMyBookings() {
        viewModelScope.launch {
            _myBookingsState.value = bookingRepository.getMyBookings()
        }
    }

    fun submitReview(bookingId: String, providerId: String, rating: Float, comment: String) {
        viewModelScope.launch {
            val review = Review(
                bookingId = bookingId,
                providerId = providerId,
                rating = rating,
                comment = comment
            )
            reviewRepository.submitReview(review)
            // Refresh details if needed
        }
    }
}
