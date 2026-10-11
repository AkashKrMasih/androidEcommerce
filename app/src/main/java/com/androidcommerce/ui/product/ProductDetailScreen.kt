package com.androidcommerce.ui.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidcommerce.util.MoneyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onBack: () -> Unit,
    onGoToCheckout: () -> Unit,
) {
    val product by viewModel.product.collectAsState()
    var quantity by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Product") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        val p = product
        if (p == null) {
            Text(
                "Loading…",
                modifier = Modifier.padding(padding).padding(24.dp),
            )
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            Text(p.imageEmoji, fontSize = 72.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(16.dp))
            Text(p.name, style = MaterialTheme.typography.headlineSmall)
            Text(p.category, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Text(MoneyFormatter.format(p.priceCents), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Text(p.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Qty: $quantity", modifier = Modifier.weight(1f))
                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                    Text("−", style = MaterialTheme.typography.headlineSmall)
                }
                IconButton(onClick = { quantity++ }) {
                    Text("+", style = MaterialTheme.typography.headlineSmall)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.addToCart(quantity)
                    onGoToCheckout()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Add to cart & checkout")
            }
        }
    }
}
