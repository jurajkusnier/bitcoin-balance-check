package com.jurajkusnier.bitcoinwalletbalance.data.model

data class ExchangeRate(val rate: Double, val symbol: String)
data class ExchangeRateWithCurrencyCode(val exchangeRate: ExchangeRate?, val currencyCode: CurrencyCode)
