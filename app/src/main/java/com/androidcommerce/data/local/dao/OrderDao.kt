package com.androidcommerce.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.androidcommerce.data.local.entity.OrderEntity
import com.androidcommerce.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Transaction
    suspend fun placeOrder(order: OrderEntity, items: List<OrderItemEntity>): Long {
        val orderId = insertOrder(order)
        insertItems(items.map { it.copy(orderId = orderId) })
        return orderId
    }

    @Query(
        """
        SELECT * FROM orders
        WHERE userId = :userId
        ORDER BY createdAtMillis DESC
        """,
    )
    fun observeOrdersForUser(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: Long): List<OrderItemEntity>
}
