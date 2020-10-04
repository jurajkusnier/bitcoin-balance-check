package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRateWithCurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRates
import java.util.*

class CurrencyListItemGenerator {

    private var exchangeRates: ExchangeRates? = null
    private var currentExchangeRate: CurrencyCode? = null
    private val mutableCurrencyListItems = MutableLiveData<List<CurrencyItem>>()

    val currencyListItems: LiveData<List<CurrencyItem>>
        get() = mutableCurrencyListItems

    fun setExchangeRates(value: ExchangeRates?) {
        if (value == null) return
        exchangeRates = value
        updateCurrencyItemList()
    }

    fun setCurrentExchangeRate(value: CurrencyCode) {
        currentExchangeRate = value
        updateCurrencyItemList()
    }

    private fun updateCurrencyItemList() {
        exchangeRates?.let {
            mutableCurrencyListItems.value = getCurrencyItems(it)
        } ?: run {
            mutableCurrencyListItems.value = getLoadingItems()
        }
    }

    private fun getCurrencyItems(rates: ExchangeRates): List<CurrencyItem> {
        return rates.values.map { item ->
            CurrencyItem.Currency(ExchangeRateWithCurrencyCode(item.value, item.key), item.key == currentExchangeRate)
        }.plus(CurrencyItem.LastUpdate(rates.lastUpdate))
    }

    private fun getLoadingItems(): List<CurrencyItem> {
        return List(LOADING_ITEMS_COUNT) { CurrencyItem.Loading }
    }

    init {
        updateCurrencyItemList()
    }

    companion object {
        private const val LOADING_ITEMS_COUNT = 10
    }

}

sealed class CurrencyItem {
    data class Currency(val exchangeRateWithCurrencyCode: ExchangeRateWithCurrencyCode, val isSelected: Boolean) : CurrencyItem()
    object Loading : CurrencyItem()
    data class LastUpdate(val date: Date) : CurrencyItem()
}