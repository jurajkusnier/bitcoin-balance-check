package com.jurajkusnier.bitcoinwalletbalance.data.api

import com.jurajkusnier.bitcoinwalletbalance.data.model.CoinmarketcapData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/*

Coinmarketcap API: https://coinmarketcap.com/api/

*/

interface CoinmarketcapApiService {

    @GET("v2/ticker/1/")
    fun getBitcoinPrice(@Query("convert") walletID:String): Observable<CoinmarketcapData>
}