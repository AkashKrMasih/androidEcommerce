package com.androidcommerce.domain.model

data class CartItem(
    val productId: Long,
    val name: String,
    val unitPriceCents: Long,
    val quantity: Int,
) {
    val lineTotalCents: Long = unitPriceCents * quantity
}
