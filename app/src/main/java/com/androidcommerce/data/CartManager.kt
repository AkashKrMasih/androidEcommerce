package com.androidcommerce.data

import com.androidcommerce.domain.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartManager {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun add(productId: Long, name: String, unitPriceCents: Long, quantity: Int = 1) {
        if (quantity <= 0) return
        _items.update { current ->
            val existing = current.find { it.productId == productId }
            if (existing == null) {
                current + CartItem(productId, name, unitPriceCents, quantity)
            } else {
                current.map {
                    if (it.productId == productId) {
                        it.copy(quantity = it.quantity + quantity)
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun setQuantity(productId: Long, quantity: Int) {
        _items.update { current ->
            if (quantity <= 0) {
                current.filter { it.productId != productId }
            } else {
                current.map {
                    if (it.productId == productId) it.copy(quantity = quantity) else it
                }
            }
        }
    }

    fun remove(productId: Long) {
        _items.update { it.filter { item -> item.productId != productId } }
    }

    fun clear() {
        _items.value = emptyList()
    }

    fun totalCents(): Long = _items.value.sumOf { it.lineTotalCents }
}
