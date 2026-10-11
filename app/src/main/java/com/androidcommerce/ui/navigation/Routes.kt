package com.androidcommerce.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val MAIN = "main"
    const val PRODUCT_DETAIL = "product/{productId}"
    const val CHECKOUT = "checkout"

    fun productDetail(productId: Long) = "product/$productId"
}
