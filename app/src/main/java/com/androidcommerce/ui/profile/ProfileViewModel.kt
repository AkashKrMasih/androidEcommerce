package com.androidcommerce.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidcommerce.data.AuthResult
import com.androidcommerce.data.CommerceRepository
import com.androidcommerce.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isSaving: Boolean = false,
    val message: String? = null,
    val error: String? = null,
)

class ProfileViewModel(
    private val repository: CommerceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val user: StateFlow<User?> = repository.sessionUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.observeCurrentUser(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun save(userId: Long, email: String, newPassword: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, message = null)
            when (val result = repository.updateProfile(userId, email, newPassword)) {
                is AuthResult.Success -> {
                    _uiState.value = ProfileUiState(message = "Profile updated.")
                }
                is AuthResult.Error -> {
                    _uiState.value = ProfileUiState(error = result.message)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}
