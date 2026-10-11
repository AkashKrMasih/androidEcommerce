package com.androidcommerce.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidcommerce.data.AuthResult
import com.androidcommerce.data.CartManager
import com.androidcommerce.data.CommerceRepository
import com.androidcommerce.domain.model.CartItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val isPlacingOrder: Boolean = false,
    val error: String? = null,
    val orderPlaced: Boolean = false,
)

class CheckoutViewModel(
    private val repository: CommerceRepository,
    private val cartManager: CartManager,
) : ViewModel() {
    val items: StateFlow<List<CartItem>> = cartManager.items
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalCents: StateFlow<Long> = cartManager.items
        .map { cartManager.totalCents() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState

    fun setQuantity(productId: Long, quantity: Int) {
        cartManager.setQuantity(productId, quantity)
    }

    fun remove(productId: Long) {
        cartManager.remove(productId)
    }

    fun placeOrder(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPlacingOrder = true, error = null)
            when (val result = repository.placeOrder(userId, cartManager.items.value)) {
                is AuthResult.Success -> {
                    cartManager.clear()
                    _uiState.value = CheckoutUiState(orderPlaced = true)
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isPlacingOrder = false,
                        error = result.message,
                    )
                }
            }
        }
    }

    fun clearOrderPlaced() {
        _uiState.value = CheckoutUiState()
    }
}
