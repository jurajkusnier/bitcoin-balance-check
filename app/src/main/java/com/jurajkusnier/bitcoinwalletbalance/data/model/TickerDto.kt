package com.jurajkusnier.bitcoinwalletbalance.data.model

data class TickerDto(val last: Double, val symbol: String) {
    fun toExchangeRate() = ExchangeRate(symbol = symbol, rate = last)
}
