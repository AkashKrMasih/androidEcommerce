package com.androidcommerce

import android.app.Application
import androidx.room.Room
import com.androidcommerce.data.CartManager
import com.androidcommerce.data.CatalogSeeder
import com.androidcommerce.data.CommerceRepository
import com.androidcommerce.data.SessionManager
import com.androidcommerce.data.local.CommerceDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CommerceApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var repository: CommerceRepository
        private set

    lateinit var cartManager: CartManager
        private set

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(
            this,
            CommerceDatabase::class.java,
            "commerce.db",
        ).build()

        val sessionManager = SessionManager(this)
        repository = CommerceRepository(
            userDao = database.userDao(),
            productDao = database.productDao(),
            orderDao = database.orderDao(),
            sessionManager = sessionManager,
        )
        cartManager = CartManager()

        applicationScope.launch {
            CatalogSeeder(database.productDao()).seedIfEmpty()
        }
    }
}
