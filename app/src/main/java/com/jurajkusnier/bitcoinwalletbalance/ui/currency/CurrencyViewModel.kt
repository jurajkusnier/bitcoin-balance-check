package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class CurrencyViewModel @ViewModelInject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val currencyListItemGenerator: CurrencyListItemGenerator
) : ViewModel() {

    init {
        (viewModelScope + Dispatchers.IO).launch {
            exchangeRatesRepository.getCurrencyCode().collect {
                withContext(Dispatchers.Main) {
                    currencyListItemGenerator.currentExchangeRate = it
                }
            }
        }

        (viewModelScope + Dispatchers.IO).launch {
            exchangeRatesRepository.getExchangeRates().collect {
                withContext(Dispatchers.Main) {
                    currencyListItemGenerator.exchangeRates = it.value
                }
            }
        }
    }

    fun getCurrencyListItems(): LiveData<List<CurrencyItem>> =
        currencyListItemGenerator.currencyListItems

    fun setCurrencyCode(currencyCode: CurrencyCode) {
        exchangeRatesRepository.setCurrencyCode(currencyCode)
    }

}