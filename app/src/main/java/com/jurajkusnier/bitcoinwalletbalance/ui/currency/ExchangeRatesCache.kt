package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import android.content.Context
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRates
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesCache @Inject constructor(@ApplicationContext context: Context, moshi: Moshi) {
    private val file = File(context.filesDir, FILE_NAME)
    private val moshiAdapter: JsonAdapter<ExchangeRates> = moshi.adapter(ExchangeRates::class.java)

    fun getExchangeRates(): ExchangeRates? = try {
        moshiAdapter.fromJson(file.readText())
    } catch (err: Exception) {
        null
    }

    fun setExchangeRates(exchangeRates: ExchangeRates) {
        file.writeText(moshiAdapter.toJson(exchangeRates))
    }

    companion object {
        const val FILE_NAME = "exchange_rates.json"
    }
}