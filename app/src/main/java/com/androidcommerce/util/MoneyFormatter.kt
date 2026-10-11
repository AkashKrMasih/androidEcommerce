package com.androidcommerce.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object MoneyFormatter {
    private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance("USD")
    }

    fun format(cents: Long): String = formatter.format(cents / 100.0)
}
