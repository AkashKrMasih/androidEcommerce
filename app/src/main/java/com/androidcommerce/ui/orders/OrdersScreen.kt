package com.androidcommerce.ui.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidcommerce.domain.model.Order
import com.androidcommerce.util.MoneyFormatter
import java.text.DateFormat
import java.util.Date

@Composable
fun OrdersScreen(viewModel: OrdersViewModel) {
    val orders by viewModel.orders.collectAsState()
    val expanded by viewModel.expandedOrder.collectAsState()
    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

    if (orders.isEmpty()) {
        Text(
            "No orders yet.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(orders, key = { it.id }) { order ->
            OrderCard(
                order = order,
                dateLabel = dateFormat.format(Date(order.createdAtMillis)),
                isExpanded = expanded?.orderId == order.id,
                expandedLines = if (expanded?.orderId == order.id) {
                    expanded?.lines ?: emptyList()
                } else {
                    emptyList()
                },
                onToggle = { viewModel.toggleOrderDetails(order.id) },
            )
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    dateLabel: String,
    isExpanded: Boolean,
    expandedLines: List<com.androidcommerce.domain.model.OrderLine>,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable(onClick = onToggle),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.id}", style = MaterialTheme.typography.titleMedium)
            Text(dateLabel, style = MaterialTheme.typography.bodySmall)
            Text("Status: ${order.status}")
            Text("Total: ${MoneyFormatter.format(order.totalCents)}")
            if (isExpanded) {
                Text(
                    "Items",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
                expandedLines.forEach { line ->
                    Text(
                        "• ${line.productName} × ${line.quantity} — ${MoneyFormatter.format(line.unitPriceCents * line.quantity)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                Text(
                    "Tap for details",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
