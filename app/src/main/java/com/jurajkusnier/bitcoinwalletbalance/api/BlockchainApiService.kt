package com.jurajkusnier.bitcoinwalletbalance.api

import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.TickerDto
import com.jurajkusnier.bitcoinwalletbalance.data.model.WalletDetailsDTO
import retrofit2.http.GET
import retrofit2.http.Path

/*
 * Blockchain API Documentation: https://www.blockchain.com/api/
 *
 *
 */

interface BlockchainApiService {

    @GET("rawaddr/{walletID}")
    suspend fun getDetails(@Path("walletID") walletID: String): WalletDetailsDTO

    @GET("ticker")
    suspend fun getTicker(): Map<CurrencyCode, TickerDto>
}