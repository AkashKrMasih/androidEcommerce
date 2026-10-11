package com.androidcommerce.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androidcommerce.CommerceApplication
import com.androidcommerce.data.CartManager
import com.androidcommerce.data.CommerceRepository
import com.androidcommerce.ui.auth.AuthViewModel
import com.androidcommerce.ui.checkout.CheckoutViewModel
import com.androidcommerce.ui.orders.OrdersViewModel
import com.androidcommerce.ui.product.ProductDetailViewModel
import com.androidcommerce.ui.products.ProductsViewModel
import com.androidcommerce.ui.profile.ProfileViewModel

class AppViewModelFactory(
    private val repository: CommerceRepository,
    private val cartManager: CartManager,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(repository) as T
            modelClass.isAssignableFrom(ProductsViewModel::class.java) ->
                ProductsViewModel(repository, cartManager) as T
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) ->
                ProductDetailViewModel(repository, cartManager) as T
            modelClass.isAssignableFrom(CheckoutViewModel::class.java) ->
                CheckoutViewModel(repository, cartManager) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(repository) as T
            modelClass.isAssignableFrom(OrdersViewModel::class.java) ->
                OrdersViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }

    companion object {
        fun from(application: CommerceApplication): AppViewModelFactory =
            AppViewModelFactory(application.repository, application.cartManager)
    }
}
