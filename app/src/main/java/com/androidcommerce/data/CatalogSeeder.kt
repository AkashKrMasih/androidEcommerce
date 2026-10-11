package com.androidcommerce.data

import com.androidcommerce.data.local.dao.ProductDao
import com.androidcommerce.data.local.entity.ProductEntity

class CatalogSeeder(private val productDao: ProductDao) {
    suspend fun seedIfEmpty() {
        if (productDao.count() > 0) return
        productDao.insertAll(
            listOf(
                ProductEntity(
                    name = "Wireless Headphones",
                    description = "Noise-cancelling over-ear headphones with 30h battery life.",
                    priceCents = 12999,
                    category = "Electronics",
                    imageEmoji = "🎧",
                ),
                ProductEntity(
                    name = "Smart Watch",
                    description = "Fitness tracking, heart rate monitor, and GPS.",
                    priceCents = 19999,
                    category = "Electronics",
                    imageEmoji = "⌚",
                ),
                ProductEntity(
                    name = "Running Shoes",
                    description = "Lightweight mesh upper with responsive cushioning.",
                    priceCents = 8999,
                    category = "Fashion",
                    imageEmoji = "👟",
                ),
                ProductEntity(
                    name = "Ceramic Mug",
                    description = "350ml handmade mug, dishwasher safe.",
                    priceCents = 1599,
                    category = "Home",
                    imageEmoji = "☕",
                ),
                ProductEntity(
                    name = "Backpack",
                    description = "Water-resistant 20L day pack with laptop sleeve.",
                    priceCents = 5499,
                    category = "Accessories",
                    imageEmoji = "🎒",
                ),
                ProductEntity(
                    name = "Desk Lamp",
                    description = "Adjustable LED lamp with warm and cool modes.",
                    priceCents = 3999,
                    category = "Home",
                    imageEmoji = "💡",
                ),
            ),
        )
    }
}
