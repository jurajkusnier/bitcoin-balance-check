package com.jurajkusnier.bitcoinwalletbalance.exchangerates

import android.content.Context
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRates
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesCache @Inject constructor(context: Context, moshi: Moshi) {
    private val file = File(context.filesDir, FILE_NAME)
    private val moshiAdapter: JsonAdapter<ExchangeRates> = moshi.adapter(ExchangeRates::class.java)
    private val subject = BehaviorSubject.create<ExchangeRates>()
    private var lastExchangeRates: ExchangeRates? = null

    fun getExchangeRates(): Observable<ExchangeRates> {
        return subject
                .doOnSubscribe { emitExchangeRates() }
                .share()
    }

    fun setExchangeRates(exchangeRates: ExchangeRates) {
        lastExchangeRates = exchangeRates
        file.writeText(moshiAdapter.toJson(exchangeRates))
        subject.onNext(exchangeRates)
    }

    fun getCacheLastUpdate(): Date? =
            lastExchangeRates?.lastUpdate ?: run {
                lastExchangeRates = getExchangeRatesFromFile()
                lastExchangeRates?.lastUpdate
            }

    private fun emitExchangeRates() {
        lastExchangeRates?.let {
            subject.onNext(it)
        } ?: run {
            lastExchangeRates = getExchangeRatesFromFile()
            subject.onNext(lastExchangeRates ?: ExchangeRates.empty)
        }
    }

    private fun getExchangeRatesFromFile(): ExchangeRates? =
            try {
                moshiAdapter.fromJson(file.readText())
            } catch (err: Exception) {
                null
            }


    companion object {
        const val FILE_NAME = "exchange_rates.json"
    }
}