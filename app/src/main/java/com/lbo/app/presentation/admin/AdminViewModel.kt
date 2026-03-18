package com.lbo.app.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lbo.app.data.model.*
import com.lbo.app.domain.repository.*
import com.lbo.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminDashboardState(
    val isLoading: Boolean = true,
    val stats: DashboardStats = DashboardStats(),
    val pendingProviders: List<Provider> = emptyList(),
    val allBookings: List<Booking> = emptyList(),
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val providerRepository: ProviderRepository,
    private val bookingRepository: BookingRepository,
    private val categoryRepository: CategoryRepository,
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow(AdminDashboardState())
    val dashboardState: StateFlow<AdminDashboardState> = _dashboardState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(isLoading = true)
            try {
                val usersResult = userRepository.getAllUsers()
                val providersResult = providerRepository.getAllProviders()
                val pendingResult = providerRepository.getPendingProviders()
                val bookingsResult = bookingRepository.getAllBookings()
                val categoriesResult = categoryRepository.getCategories()

                val totalUsers = (usersResult as? Resource.Success)?.data?.size ?: 0
                val allProviders = (providersResult as? Resource.Success)?.data ?: emptyList()
                val pendingProviders = (pendingResult as? Resource.Success)?.data ?: emptyList()
                val allBookings = (bookingsResult as? Resource.Success)?.data ?: emptyList()
                val categories = (categoriesResult as? Resource.Success)?.data ?: emptyList()

                _dashboardState.value = AdminDashboardState(
                    isLoading = false,
                    stats = DashboardStats(
                        totalUsers = totalUsers,
                        totalProviders = allProviders.size,
                        pendingApprovals = pendingProviders.size,
                        totalBookings = allBookings.size
                    ),
                    pendingProviders = pendingProviders,
                    allBookings = allBookings,
                    categories = categories
                )
            } catch (e: Exception) {
                _dashboardState.value = AdminDashboardState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun approveProvider(providerId: String) {
        viewModelScope.launch {
            providerRepository.approveProvider(providerId)
            loadDashboard()
        }
    }

    fun rejectProvider(providerId: String) {
        viewModelScope.launch {
            providerRepository.rejectProvider(providerId)
            loadDashboard()
        }
    }

    fun addCategory(name: String, icon: String) {
        viewModelScope.launch {
            categoryRepository.addCategory(Category(name = name, icon = icon))
            loadDashboard()
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(categoryId)
            loadDashboard()
        }
    }

    fun createCommunityPost(title: String, description: String) {
        viewModelScope.launch {
            communityRepository.createPost(
                CommunityPost(title = title, description = description)
            )
        }
    }
}
