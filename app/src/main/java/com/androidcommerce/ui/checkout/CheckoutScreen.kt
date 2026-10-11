package com.androidcommerce.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidcommerce.domain.model.CartItem
import com.androidcommerce.util.MoneyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    userId: Long,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit,
) {
    val items by viewModel.items.collectAsState()
    val total by viewModel.totalCents.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.orderPlaced) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Order placed!", style = MaterialTheme.typography.headlineMedium)
            Text("Thank you for your purchase.", modifier = Modifier.padding(top = 8.dp))
            Button(
                onClick = {
                    viewModel.clearOrderPlaced()
                    onOrderPlaced()
                },
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Text("Continue shopping")
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (items.isEmpty()) {
                Text(
                    "Your cart is empty.",
                    modifier = Modifier.padding(24.dp),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(items, key = { it.productId }) { item ->
                        CartLineItem(
                            item = item,
                            onIncrease = {
                                viewModel.setQuantity(item.productId, item.quantity + 1)
                            },
                            onDecrease = {
                                viewModel.setQuantity(item.productId, item.quantity - 1)
                            },
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Total", style = MaterialTheme.typography.titleMedium)
                    Text(MoneyFormatter.format(total), style = MaterialTheme.typography.titleMedium)
                }
                uiState.error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
                Button(
                    onClick = { viewModel.placeOrder(userId) },
                    enabled = !uiState.isPlacingOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(if (uiState.isPlacingOrder) "Placing order…" else "Place order")
                }
            }
        }
    }
}

@Composable
private fun CartLineItem(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleSmall)
                Text(MoneyFormatter.format(item.unitPriceCents))
            }
            IconButton(onClick = onDecrease) { Text("−") }
            Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 4.dp))
            IconButton(onClick = onIncrease) { Text("+") }
            Text(
                MoneyFormatter.format(item.lineTotalCents),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
