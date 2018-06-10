package com.jurajkusnier.bitcoinwalletbalance.ui.settings

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.api.CoinmarketcapApiService
import com.jurajkusnier.bitcoinwalletbalance.data.db.ConversionPrefs
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SettingRepository @Inject constructor(private val conversionPrefs: ConversionPrefs, private val coinmarketcapApiService: CoinmarketcapApiService) {

    val TAG = SettingRepository::class.java.simpleName

    val UPDATE_DELAY = 10*60*1000L //10 min

    fun changeCurrency(id:Int) {
        conversionPrefs.changeCurrency(id)
    }

    fun getCurrencyId():Int {
        return conversionPrefs.getCurrencyIndex()
    }

    fun getCurrencyCode():String {
        return conversionPrefs.getCurrencyCode()
    }

    var disposable:Disposable? = null

    fun getBitcoinPrice(currencyCode:String, priceLoaderCallback: priceLoaderCallback ) {
        val now = System.currentTimeMillis()

        //Load from prefs
        val exchangeRate = conversionPrefs.getCurrencyExchangeRate(currencyCode)
        val lastUpdate = conversionPrefs.getLastUpdate(currencyCode)

        if (lastUpdate > 0 && exchangeRate > 0) {
            priceLoaderCallback.setResults(exchangeRate, currencyCode, lastUpdate)
        }

        if (now - lastUpdate > UPDATE_DELAY) {
            //Load from api
            disposable = coinmarketcapApiService.getBitcoinPrice(currencyCode)
                    .subscribeOn(Schedulers.io())
                    .subscribe( {
                        data -> conversionPrefs.saveCurrency(data.data)
                        val price = data.data.quotes[currencyCode]?.price
                        if (price != null) {
                            priceLoaderCallback.setResults(price,currencyCode, now)
                            priceLoaderCallback.loadingDone()
                        } else {
                            priceLoaderCallback.loadingFailed()
                        }
                    }, {
                        error ->
                        priceLoaderCallback.loadingFailed()
                        Log.e(TAG, Log.getStackTraceString(error))
                    })
        } else {
            priceLoaderCallback.loadingDone()
        }
    }

    interface priceLoaderCallback {
        fun setResults(currencyRate:Float, currencyCode: String, lastUpdate:Long)
        fun loadingDone()
        fun loadingFailed()
    }
}
