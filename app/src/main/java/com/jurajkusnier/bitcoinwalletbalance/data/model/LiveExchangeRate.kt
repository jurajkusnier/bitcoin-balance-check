package com.jurajkusnier.bitcoinwalletbalance.data.model

import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import com.jurajkusnier.bitcoinwalletbalance.data.db.ConversionPrefs
import javax.inject.Inject

data class ExchangeRate(val price:Float, val currency:String)

class LiveExchangeRate @Inject constructor(private val conversionPrefs: ConversionPrefs) : MutableLiveData<ExchangeRate>() {
    private val mListener: SharedPreferences.OnSharedPreferenceChangeListener

    init {
        val value = conversionPrefs.mSharedPreferences.getFloat(conversionPrefs.getCurrencyCodeKey(),0f)
        updateValue(value)

        mListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == conversionPrefs.getCurrencyCodeKey()) {
                updateValue(sharedPreferences.getFloat(key,0f))
            }
        }
    }

    private fun updateValue(value:Float) {
        setValue(ExchangeRate(value, conversionPrefs.getCurrencyCode()))
    }

    override fun onActive() {
        super.onActive()
        conversionPrefs.mSharedPreferences.registerOnSharedPreferenceChangeListener(mListener)
    }

    override fun onInactive() {
        super.onInactive()
        conversionPrefs.mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mListener)
    }
}
