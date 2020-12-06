package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import android.content.SharedPreferences
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyService @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val mutableCurrencyCode = MutableStateFlow(DEFAULT_CURRENCY_CODE)
    val currencyCode: StateFlow<CurrencyCode> get() = mutableCurrencyCode

    init {
        mutableCurrencyCode.value = sharedPreferences.getString(CURRENCY_CODE, DEFAULT_CURRENCY_CODE)
                ?: DEFAULT_CURRENCY_CODE
    }

    fun setCurrencyCode(currencyCode: CurrencyCode) = sharedPreferences.edit()
            ?.apply {
                putString(CURRENCY_CODE, currencyCode)
                apply()
                mutableCurrencyCode.value = currencyCode
            }

    companion object {
        private const val CURRENCY_CODE = "CURRENCY_CODE"
        private const val DEFAULT_CURRENCY_CODE = "USD" //TODO: use default currency code from locale
    }
}