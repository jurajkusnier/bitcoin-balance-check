package com.jurajkusnier.bitcoinwalletbalance.data.api

import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRate

data class TickerDto(val last: Double, val symbol: String) {
    fun toExchangeRate() = ExchangeRate(symbol = symbol, rate = last)
}
