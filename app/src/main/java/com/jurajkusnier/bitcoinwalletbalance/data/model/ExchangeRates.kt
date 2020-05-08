package com.jurajkusnier.bitcoinwalletbalance.data.model

import java.util.*

typealias CurrencyCode = String

data class ExchangeRates(val lastUpdate: Date, val values: Map<CurrencyCode, ExchangeRate>) {
    companion object {
        val empty = ExchangeRates(Date(0), mapOf())
    }
}

fun Map<CurrencyCode, TickerDto>.toExchangeRates(date: Date): ExchangeRates {
    val values = mutableMapOf<CurrencyCode, ExchangeRate>()
    forEach { item ->
        values[item.key] = item.value.toExchangeRate()
    }
    return ExchangeRates(date, values)
}