package com.lbo.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lbo.app.data.model.User
import com.lbo.app.domain.repository.AuthRepository
import com.lbo.app.domain.repository.UserRepository
import com.lbo.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isPasswordResetSent: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        if (authRepository.isLoggedIn()) {
            viewModelScope.launch {
                _authState.value = _authState.value.copy(isLoading = true)
                when (val result = authRepository.getCurrentUserData()) {
                    is Resource.Success -> {
                        _authState.value = AuthState(
                            isLoggedIn = true,
                            user = result.data
                        )
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(isLoggedIn = false)
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    when (val userData = authRepository.getCurrentUserData()) {
                        is Resource.Success -> {
                            _authState.value = AuthState(
                                isLoggedIn = true,
                                user = userData.data
                            )
                        }
                        is Resource.Error -> {
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                error = userData.message
                            )
                        }
                        is Resource.Loading -> {}
                    }
                }
                is Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun register(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.register(email, password, name, role)) {
                is Resource.Success -> {
                    when (val userData = authRepository.getCurrentUserData()) {
                        is Resource.Success -> {
                            _authState.value = AuthState(
                                isLoggedIn = true,
                                user = userData.data
                            )
                        }
                        is Resource.Error -> {
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                error = userData.message
                            )
                        }
                        is Resource.Loading -> {}
                    }
                }
                is Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun googleSignIn(idToken: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.googleSignIn(idToken)) {
                is Resource.Success -> {
                    when (val userData = authRepository.getCurrentUserData()) {
                        is Resource.Success -> {
                            _authState.value = AuthState(
                                isLoggedIn = true,
                                user = userData.data
                            )
                        }
                        is Resource.Error -> {
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                error = userData.message
                            )
                        }
                        is Resource.Loading -> {}
                    }
                }
                is Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.forgotPassword(email)) {
                is Resource.Success -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isPasswordResetSent = true
                    )
                }
                is Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState()
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}
