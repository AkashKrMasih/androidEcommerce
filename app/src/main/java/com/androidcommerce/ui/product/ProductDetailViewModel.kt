package com.androidcommerce.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidcommerce.data.CartManager
import com.androidcommerce.data.CommerceRepository
import com.androidcommerce.domain.model.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProductDetailViewModel(
    repository: CommerceRepository,
    private val cartManager: CartManager,
    productId: Long,
) : ViewModel() {
    val product: StateFlow<Product?> = repository.observeProduct(productId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun addToCart(quantity: Int) {
        val p = product.value ?: return
        cartManager.add(p.id, p.name, p.priceCents, quantity)
    }
}
