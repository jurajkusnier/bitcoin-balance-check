package com.jurajkusnier.bitcoinwalletbalance.data.api

import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.TickerDto
import com.jurajkusnier.bitcoinwalletbalance.data.model.WalletDetailsDTO
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/*
    Blockchain API Documentaion: https://www.blockchain.com/api/
*/

interface BlockchainApiService {

    @GET("rawaddr/{walletID}")
    fun getDetails(@Path("walletID") walletID: String): Single<WalletDetailsDTO>

    @GET("ticker")
    fun getTicker(): Single<Map<CurrencyCode, TickerDto>>
}