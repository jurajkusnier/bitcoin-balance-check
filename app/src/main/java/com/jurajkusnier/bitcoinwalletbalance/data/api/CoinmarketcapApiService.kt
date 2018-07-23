package com.jurajkusnier.bitcoinwalletbalance.data.api

import com.jurajkusnier.bitcoinwalletbalance.data.model.CoinmarketcapData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/*

Coinmarketcap API Documentaion: https://coinmarketcap.com/api/


Example Request:
https://api.coinmarketcap.com/v2/ticker/1/?convert=EUR

Example Response:
{
    "data": {
        "id": 1,
        "name": "Bitcoin",
        "symbol": "BTC",
        "website_slug": "bitcoin",
        "rank": 1,
        "circulating_supply": 17166350.0,
        "total_supply": 17166350.0,
        "max_supply": 21000000.0,
        "quotes": {
            "USD": {
                "price": 7743.56,
                "volume_24h": 5014590000.0,
                "market_cap": 132928661206.0,
                "percent_change_1h": 0.26,
                "percent_change_24h": 3.11,
                "percent_change_7d": 16.32
            },
            "EUR": {
                "price": 6622.9558672597,
                "volume_24h": 4288906944.9196033,
                "market_cap": 113691978452.0,
                "percent_change_1h": 0.26,
                "percent_change_24h": 3.11,
                "percent_change_7d": 16.32
            }
        },
        "last_updated": 1532370620
    },
    "metadata": {
        "timestamp": 1532370283,
        "error": null
    }
}


*/

interface CoinmarketcapApiService {

    @GET("v2/ticker/1/")
    fun getBitcoinPrice(@Query("convert") walletID:String): Observable<CoinmarketcapData>
}