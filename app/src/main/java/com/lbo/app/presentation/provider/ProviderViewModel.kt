package com.lbo.app.presentation.provider

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lbo.app.data.model.Booking
import com.lbo.app.data.model.Provider
import com.lbo.app.data.model.User
import com.lbo.app.domain.repository.*
import com.lbo.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProviderProfileState(
    val provider: User? = null,
    val providerData: Provider? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

data class ProviderBookingsState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val providerRepository: ProviderRepository,
    private val bookingRepository: BookingRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProviderProfileState())
    val profileState = _profileState.asStateFlow()

    private val _bookingsState = MutableStateFlow(ProviderBookingsState())
    val bookingsState = _bookingsState.asStateFlow()

    init {
        loadProfile()
        refreshBookings()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            when (val result = authRepository.getCurrentUserData()) {
                is Resource.Success -> {
                    val user = result.data
                    // Also load the Provider record
                    val providerResult = providerRepository.getProviderByUserId(user.userId)
                    val provData = (providerResult as? Resource.Success)?.data
                    _profileState.value = ProviderProfileState(provider = user, providerData = provData)
                }
                is Resource.Error -> {
                    _profileState.value = _profileState.value.copy(isLoading = false, error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refreshBookings() {
        viewModelScope.launch {
            _bookingsState.value = _bookingsState.value.copy(isLoading = true)
            val userId = authRepository.currentUser?.uid
            if (userId == null) {
                _bookingsState.value = ProviderBookingsState(error = "User not logged in")
                return@launch
            }
            when (val result = bookingRepository.getBookingsByProvider(userId)) {
                is Resource.Success -> {
                    _bookingsState.value = ProviderBookingsState(bookings = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _bookingsState.value = ProviderBookingsState(error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(bookingId, status)
            refreshBookings()
        }
    }

    fun saveProfile(
        name: String,
        category: String,
        location: String,
        description: String,
        experience: String,
        profileImageUri: Uri?,
        documentUris: List<Uri>
    ) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            
            val userId = authRepository.currentUser?.uid ?: return@launch
            
            var imageUrl = _profileState.value.provider?.profileImage ?: ""
            if (profileImageUri != null) {
                val uploadResult = storageRepository.uploadProfileImage(userId, profileImageUri)
                if (uploadResult is Resource.Success) {
                    imageUrl = uploadResult.data ?: ""
                }
            }

            // Document upload logic could be more complex (list of URLs)
            // For now, assume repository handles singular or basic doc updates
            
            val updatedUser = _profileState.value.provider?.copy(
                name = name,
                role = User.ROLE_PROVIDER,
                location = location,
                profileImage = imageUrl,
                isVerified = false // Reset verification on edit if needed
            ) ?: User(
                userId = userId,
                name = name,
                role = User.ROLE_PROVIDER,
                location = location,
                profileImage = imageUrl
            )

            val provider = com.lbo.app.data.model.Provider(
                providerId = userId,
                userId = userId,
                name = name,
                category = category,
                location = location,
                description = description,
                experience = experience.toIntOrNull() ?: 0,
                profileImage = imageUrl
            )

            val userResult = userRepository.updateUser(updatedUser)
            val providerResult = providerRepository.createProvider(provider)

            if (userResult is Resource.Success && providerResult is Resource.Success) {
                _profileState.value = ProviderProfileState(provider = updatedUser, providerData = provider, isSuccess = true)
            } else {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = "Update failed"
                )
            }
        }
    }
}
