package com.jurajkusnier.bitcoinwalletbalance.data.api

import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path


interface BlockchainApiService {

    @GET("rawaddr/{walletID}")
    fun getDetails(@Path("walletID") walletID:String): Observable<RawData>

}