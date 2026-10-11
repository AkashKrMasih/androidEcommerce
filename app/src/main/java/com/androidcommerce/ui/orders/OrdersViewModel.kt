package com.androidcommerce.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidcommerce.data.CommerceRepository
import com.androidcommerce.domain.model.Order
import com.androidcommerce.domain.model.OrderLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OrderDetailsState(
    val orderId: Long,
    val lines: List<OrderLine>,
)

class OrdersViewModel(
    private val repository: CommerceRepository,
) : ViewModel() {
    val orders: StateFlow<List<Order>> = repository.sessionUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.observeOrders(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _expandedOrder = MutableStateFlow<OrderDetailsState?>(null)
    val expandedOrder: StateFlow<OrderDetailsState?> = _expandedOrder.asStateFlow()

    fun toggleOrderDetails(orderId: Long) {
        viewModelScope.launch {
            if (_expandedOrder.value?.orderId == orderId) {
                _expandedOrder.value = null
            } else {
                val lines = repository.loadOrderDetails(orderId)
                _expandedOrder.value = OrderDetailsState(orderId, lines)
            }
        }
    }
}
