package com.androidcommerce.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidcommerce.data.CartManager
import com.androidcommerce.data.CommerceRepository
import com.androidcommerce.domain.model.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProductsViewModel(
    repository: CommerceRepository,
    private val cartManager: CartManager,
) : ViewModel() {
    val products: StateFlow<List<Product>> = repository.observeProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cartItemCount: StateFlow<Int> = cartManager.items
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun addToCart(product: Product) {
        cartManager.add(product.id, product.name, product.priceCents)
    }
}
