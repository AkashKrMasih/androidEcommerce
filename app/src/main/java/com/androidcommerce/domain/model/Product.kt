package com.androidcommerce.domain.model

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val priceCents: Long,
    val category: String,
    val imageEmoji: String,
)
