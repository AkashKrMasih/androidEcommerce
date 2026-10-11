package com.androidcommerce.domain.model

data class Order(
    val id: Long,
    val totalCents: Long,
    val status: String,
    val createdAtMillis: Long,
    val items: List<OrderLine>,
)

data class OrderLine(
    val productName: String,
    val quantity: Int,
    val unitPriceCents: Long,
)
