package com.androidcommerce.data

import com.androidcommerce.data.local.dao.OrderDao
import com.androidcommerce.data.local.dao.ProductDao
import com.androidcommerce.data.local.dao.UserDao
import com.androidcommerce.data.local.entity.OrderEntity
import com.androidcommerce.data.local.entity.OrderItemEntity
import com.androidcommerce.data.local.entity.ProductEntity
import com.androidcommerce.data.local.entity.UserEntity
import com.androidcommerce.domain.model.CartItem
import com.androidcommerce.domain.model.Order
import com.androidcommerce.domain.model.OrderLine
import com.androidcommerce.domain.model.Product
import com.androidcommerce.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class AuthResult {
    data class Success(val userId: Long) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class CommerceRepository(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val orderDao: OrderDao,
    private val sessionManager: SessionManager,
) {
    val sessionUserId: Flow<Long?> = sessionManager.currentUserId

    fun observeCurrentUser(userId: Long): Flow<User?> =
        userDao.observeById(userId).map { entity -> entity?.toDomain() }

    fun observeProducts(): Flow<List<Product>> =
        productDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeProduct(productId: Long): Flow<Product?> =
        productDao.observeById(productId).map { it?.toDomain() }

    fun observeOrders(userId: Long): Flow<List<Order>> =
        orderDao.observeOrdersForUser(userId).map { orders ->
            orders.map { order ->
                Order(
                    id = order.id,
                    totalCents = order.totalCents,
                    status = order.status,
                    createdAtMillis = order.createdAtMillis,
                    items = emptyList(),
                )
            }
        }

    suspend fun loadOrderDetails(orderId: Long): List<OrderLine> =
        orderDao.getItemsForOrder(orderId).map {
            OrderLine(
                productName = it.productName,
                quantity = it.quantity,
                unitPriceCents = it.unitPriceCents,
            )
        }

    suspend fun signUp(email: String, password: String, displayName: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        if (normalizedEmail.isBlank() || password.length < 6) {
            return AuthResult.Error("Use a valid email and password (min 6 characters).")
        }
        if (displayName.isBlank()) {
            return AuthResult.Error("Display name is required.")
        }
        if (userDao.findByEmail(normalizedEmail) != null) {
            return AuthResult.Error("An account with this email already exists.")
        }
        val userId = userDao.insert(
            UserEntity(
                email = normalizedEmail,
                passwordHash = PasswordHasher.hash(password),
                displayName = displayName.trim(),
            ),
        )
        sessionManager.setUserId(userId)
        return AuthResult.Success(userId)
    }

    suspend fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val user = userDao.findByEmail(normalizedEmail)
            ?: return AuthResult.Error("Invalid email or password.")
        if (!PasswordHasher.verify(password, user.passwordHash)) {
            return AuthResult.Error("Invalid email or password.")
        }
        sessionManager.setUserId(user.id)
        return AuthResult.Success(user.id)
    }

    suspend fun logout() {
        sessionManager.setUserId(null)
    }

    suspend fun updateProfile(
        userId: Long,
        newEmail: String,
        newPassword: String?,
    ): AuthResult {
        val user = userDao.getById(userId) ?: return AuthResult.Error("User not found.")
        val normalizedEmail = newEmail.trim().lowercase()
        if (normalizedEmail.isBlank()) {
            return AuthResult.Error("Email cannot be empty.")
        }
        if (newPassword != null && newPassword.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters.")
        }
        val emailTaken = userDao.findByEmail(normalizedEmail)
        if (emailTaken != null && emailTaken.id != userId) {
            return AuthResult.Error("Email is already in use.")
        }
        val updated = user.copy(
            email = normalizedEmail,
            passwordHash = newPassword?.let { PasswordHasher.hash(it) } ?: user.passwordHash,
        )
        userDao.update(updated)
        return AuthResult.Success(userId)
    }

    suspend fun placeOrder(userId: Long, cartItems: List<CartItem>): AuthResult {
        if (cartItems.isEmpty()) {
            return AuthResult.Error("Your cart is empty.")
        }
        val total = cartItems.sumOf { it.lineTotalCents }
        val order = OrderEntity(
            userId = userId,
            totalCents = total,
            status = "PLACED",
            createdAtMillis = System.currentTimeMillis(),
        )
        val lines = cartItems.map { item ->
            OrderItemEntity(
                orderId = 0,
                productId = item.productId,
                productName = item.name,
                quantity = item.quantity,
                unitPriceCents = item.unitPriceCents,
            )
        }
        orderDao.placeOrder(order, lines)
        return AuthResult.Success(userId)
    }

    private fun UserEntity.toDomain() = User(id = id, email = email, displayName = displayName)

    private fun ProductEntity.toDomain() = Product(
        id = id,
        name = name,
        description = description,
        priceCents = priceCents,
        category = category,
        imageEmoji = imageEmoji,
    )
}
