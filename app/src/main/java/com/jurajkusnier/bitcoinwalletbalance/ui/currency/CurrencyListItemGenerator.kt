package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRateWithCurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRates
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CurrencyListItemGenerator {

    private val mutableCurrencyListItems = MutableLiveData<List<CurrencyItem>>()
    var exchangeRates: ExchangeRates? by UpdateDelegate()
    var currentExchangeRate: CurrencyCode? by UpdateDelegate()
    val currencyListItems: LiveData<List<CurrencyItem>>
        get() = mutableCurrencyListItems

    init {
        updateCurrencyItemList()
    }

    private fun updateCurrencyItemList() {
        exchangeRates?.let {
            mutableCurrencyListItems.value = generateCurrencyItems(it)
        } ?: run {
            mutableCurrencyListItems.value = generateLoadingItems()
        }
    }

    private fun generateCurrencyItems(rates: ExchangeRates): List<CurrencyItem> {
        return rates.values.map { item ->
            CurrencyItem.Currency(
                ExchangeRateWithCurrencyCode(item.value, item.key),
                item.key == currentExchangeRate
            )
        }.plus(CurrencyItem.LastUpdate(rates.lastUpdate))
    }

    private fun generateLoadingItems(): List<CurrencyItem> {
        return List(LOADING_ITEMS_COUNT) { CurrencyItem.Loading }
    }

    companion object {
        private const val LOADING_ITEMS_COUNT = 10
    }

    class UpdateDelegate<T> : ReadWriteProperty<CurrencyListItemGenerator?, T?> {
        var value: T? = null

        override fun setValue(
            thisRef: CurrencyListItemGenerator?,
            property: KProperty<*>,
            value: T?
        ) {
            if (value != null) {
                this.value = value
            }
            thisRef?.updateCurrencyItemList()
        }

        override fun getValue(thisRef: CurrencyListItemGenerator?, property: KProperty<*>): T? {
            return this.value
        }
    }
}

sealed class CurrencyItem {
    data class Currency(
        val exchangeRateWithCurrencyCode: ExchangeRateWithCurrencyCode,
        val isSelected: Boolean
    ) : CurrencyItem()

    object Loading : CurrencyItem()
    data class LastUpdate(val date: Date) : CurrencyItem()
}