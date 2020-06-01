package com.jurajkusnier.bitcoinwalletbalance.exchangerates

import android.content.SharedPreferences
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyService @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val subject = BehaviorSubject.create<CurrencyCode>()
            .toSerialized()

    fun getCurrencyCode(): Observable<CurrencyCode> {
        return subject.doOnSubscribe { emitCurrencyCode() }
                .share()
    }

    fun setCurrencyCode(currencyCode: CurrencyCode) = sharedPreferences.edit()
            ?.apply {
                putString(CURRENCY_CODE, currencyCode)
                apply()
                subject.onNext(currencyCode)
            }

    private fun emitCurrencyCode() {
        subject.onNext(
                sharedPreferences.getString(CURRENCY_CODE, DEFAULT_CURRENCY_CODE)
                        ?: DEFAULT_CURRENCY_CODE)
    }

    companion object {
        private const val CURRENCY_CODE = "CURRENCY_CODE"
        private const val DEFAULT_CURRENCY_CODE = "USD" //TODO: use default currency code from locale
    }
}