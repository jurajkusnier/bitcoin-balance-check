package com.jurajkusnier.bitcoinwalletbalance.data.model

import com.jurajkusnier.bitcoinwalletbalance.data.api.TickerDto
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

typealias CurrencyCode = String

@OptIn(ExperimentalTime::class)
data class ExchangeRates(val lastUpdate: Date, val values: Map<CurrencyCode, ExchangeRate>) {
    fun isOld() = (System.currentTimeMillis() - lastUpdate.time) > 15.minutes.toLongMilliseconds()
}

fun Map<CurrencyCode, TickerDto>.toExchangeRates(date: Date): ExchangeRates {
    val values = mutableMapOf<CurrencyCode, ExchangeRate>()
    forEach { item ->
        values[item.key] = item.value.toExchangeRate()
    }
    return ExchangeRates(date, values)
}