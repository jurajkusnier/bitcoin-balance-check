package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import kotlinx.coroutines.launch


class CurrencyViewModel @ViewModelInject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val currencyListItemGenerator: CurrencyListItemGenerator
) : ViewModel() {

    init {
        viewModelScope.launch {
//            exchangeRatesRepository.getCurrencyCode().collect {
//                currencyListItemGenerator.setCurrentExchangeRate(it)
//            }
        }

        viewModelScope.launch {
//            exchangeRatesRepository.getExchangeRates().collect {
//                currencyListItemGenerator.setExchangeRates(it.value)
//            }
        }
    }

    fun getCurrencyListItems(): LiveData<List<CurrencyItem>> = currencyListItemGenerator.currencyListItems

    fun setCurrencyCode(currencyCode: CurrencyCode) {
        //exchangeRatesRepository.setCurrencyCode(currencyCode)
    }

}