package com.androidcommerce.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androidcommerce.data.CartManager
import com.androidcommerce.data.CommerceRepository

class ProductDetailViewModelFactory(
    private val repository: CommerceRepository,
    private val cartManager: CartManager,
    private val productId: Long,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(repository, cartManager, productId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
