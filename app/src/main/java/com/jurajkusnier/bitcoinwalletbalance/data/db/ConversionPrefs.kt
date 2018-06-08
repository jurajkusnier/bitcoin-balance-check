package com.jurajkusnier.bitcoinwalletbalance.data.db

import android.app.Application
import android.preference.PreferenceManager
import com.jurajkusnier.bitcoinwalletbalance.data.model.CryptocurrencyInfo
import javax.inject.Inject

class ConversionPrefs @Inject constructor(context: Application) {

    val TAG = ConversionPrefs::class.java.simpleName

    val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val currencyCodes = arrayOf("USD", "EUR")

    fun saveCurrency(currency: CryptocurrencyInfo) {
        val editor = mSharedPreferences.edit()

        for ((key,data) in currency.quotes) {
            val keyUppercase = key.toUpperCase()
            if (!currencyCodes.contains(keyUppercase)) continue
            editor.putFloat(getCurrencyCodeFromKey(keyUppercase), data.price)
            editor.putLong(getLastUpdateFromKey(keyUppercase),System.currentTimeMillis())
        }

        editor.apply()
    }

    fun getLastUpdate(): Long = mSharedPreferences.getLong(getLastUpdateKey(),0L)

    fun getCurrencyCode():String = currencyCodes[1]

    fun getCurrencyCodeKey():String = getCurrencyCodeFromKey(getCurrencyCode())

    private fun getLastUpdateKey():String = getLastUpdateFromKey(getCurrencyCode())

    private fun getLastUpdateFromKey(key:String):String = "UPDATED_$key"

    private fun getCurrencyCodeFromKey(key:String):String = "PRICE_$key"

}