package com.androidcommerce.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androidcommerce.data.local.dao.OrderDao
import com.androidcommerce.data.local.dao.ProductDao
import com.androidcommerce.data.local.dao.UserDao
import com.androidcommerce.data.local.entity.OrderEntity
import com.androidcommerce.data.local.entity.OrderItemEntity
import com.androidcommerce.data.local.entity.ProductEntity
import com.androidcommerce.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class CommerceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
}
