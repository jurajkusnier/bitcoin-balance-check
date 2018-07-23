package com.jurajkusnier.bitcoinwalletbalance.data.model

/*

Holds Bitcoin data from Coinmarketcap API
https://coinmarketcap.com/api/

*/

data class CryptocurrencyInfo (
        val id: Int,
        val name: String,
        val symbol: String,
        val quotes: Map<String,ConversionData>
)

data class CryptocurrencyMetadata(
        val timestamp: Long
)

data class CoinmarketcapData (
        val data:CryptocurrencyInfo,
        val metadata:CryptocurrencyMetadata
)

data class ConversionData(
        val price: Float,
        val volume_24h: Float,
        val market_cap: Float,
        val percent_change_1h: Float,
        val percent_change_24h: Float,
        val percent_change_7d: Float
)