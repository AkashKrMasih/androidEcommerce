package com.androidcommerce.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.androidcommerce.CommerceApplication
import com.androidcommerce.ui.AppViewModelFactory
import com.androidcommerce.ui.auth.AuthViewModel
import com.androidcommerce.ui.auth.LoginScreen
import com.androidcommerce.ui.auth.SignUpScreen
import com.androidcommerce.ui.checkout.CheckoutScreen
import com.androidcommerce.ui.checkout.CheckoutViewModel
import com.androidcommerce.ui.orders.OrdersScreen
import com.androidcommerce.ui.orders.OrdersViewModel
import com.androidcommerce.ui.product.ProductDetailScreen
import com.androidcommerce.ui.product.ProductDetailViewModel
import com.androidcommerce.ui.product.ProductDetailViewModelFactory
import com.androidcommerce.ui.products.ProductsScreen
import com.androidcommerce.ui.products.ProductsViewModel
import com.androidcommerce.ui.profile.ProfileScreen
import com.androidcommerce.ui.profile.ProfileViewModel

private enum class MainTab(val route: String, val label: String) {
    Shop("main/shop", "Shop"),
    Orders("main/orders", "Orders"),
    Profile("main/profile", "Profile"),
}

@Composable
fun CommerceNavHost(application: CommerceApplication) {
    val factory = AppViewModelFactory.from(application)
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val authState by authViewModel.uiState.collectAsState()
    val loggedInUserId = authState.loggedInUserId

    val startDestination = if (loggedInUserId == null) Routes.LOGIN else Routes.MAIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) },
            )
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
            )
        }
        composable(Routes.MAIN) {
            val userId = loggedInUserId
            if (userId == null) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.MAIN) { inclusive = true }
                }
                return@composable
            }
            MainScaffold(
                factory = factory,
                onProductClick = { id ->
                    navController.navigate(Routes.productDetail(id))
                },
                onCheckout = { navController.navigate(Routes.CHECKOUT) },
            )
        }
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.LongType }),
        ) { entry ->
            val productId = entry.arguments?.getLong("productId") ?: return@composable
            val detailFactory = ProductDetailViewModelFactory(
                application.repository,
                application.cartManager,
                productId,
            )
            val detailViewModel: ProductDetailViewModel = viewModel(factory = detailFactory)
            ProductDetailScreen(
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() },
                onGoToCheckout = { navController.navigate(Routes.CHECKOUT) },
            )
        }
        composable(Routes.CHECKOUT) {
            val userId = loggedInUserId ?: return@composable
            val checkoutViewModel: CheckoutViewModel = viewModel(factory = factory)
            CheckoutScreen(
                viewModel = checkoutViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.popBackStack(Routes.MAIN, false)
                },
            )
        }
    }

    androidx.compose.runtime.LaunchedEffect(loggedInUserId) {
        if (loggedInUserId != null) {
            val route = navController.currentDestination?.route
            if (route == Routes.LOGIN || route == Routes.SIGN_UP || route == null) {
                navController.navigate(Routes.MAIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        } else {
            val route = navController.currentDestination?.route
            if (route != Routes.LOGIN && route != Routes.SIGN_UP) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(
    factory: AppViewModelFactory,
    onProductClick: (Long) -> Unit,
    onCheckout: () -> Unit,
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: MainTab.Shop.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                when (tab) {
                                    MainTab.Shop -> Icons.Default.ShoppingBag
                                    MainTab.Orders -> Icons.Default.Receipt
                                    MainTab.Profile -> Icons.Default.Person
                                },
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainTab.Shop.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(MainTab.Shop.route) {
                val productsViewModel: ProductsViewModel = viewModel(factory = factory)
                ProductsScreen(
                    viewModel = productsViewModel,
                    onProductClick = onProductClick,
                    onCheckoutClick = onCheckout,
                )
            }
            composable(MainTab.Orders.route) {
                val ordersViewModel: OrdersViewModel = viewModel(factory = factory)
                OrdersScreen(viewModel = ordersViewModel)
            }
            composable(MainTab.Profile.route) {
                val profileViewModel: ProfileViewModel = viewModel(factory = factory)
                ProfileScreen(viewModel = profileViewModel)
            }
        }
    }
}
