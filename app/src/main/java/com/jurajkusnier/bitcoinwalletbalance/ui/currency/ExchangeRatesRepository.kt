package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import com.jurajkusnier.bitcoinwalletbalance.api.BlockchainApi
import com.jurajkusnier.bitcoinwalletbalance.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class ExchangeRatesRepository @Inject constructor(
    private val currencyService: CurrencyService,
    private val blockchainApi: BlockchainApi,
    private val exchangeRatesCache: ExchangeRatesCache
) {

    fun getCurrencyCode(): Flow<CurrencyCode> = currencyService.currencyCode

    fun setCurrencyCode(currencyCode: CurrencyCode) {
        currencyService.setCurrencyCode(currencyCode)
    }

    fun getExchangeRate(): Flow<ExchangeRateWithCurrencyCode> {
        return getCurrencyCode().combine(getExchangeRates()) { currencyCode, exchangeRates ->
            val exchangeRate = exchangeRates.value?.values?.get(currencyCode)
            ExchangeRateWithCurrencyCode(exchangeRate, currencyCode)
        }
    }

    fun getExchangeRates(): Flow<RepositoryResponse<ExchangeRates>> = flow {
        val cachedExchangeRates = exchangeRatesCache.getExchangeRates()
        val isLoading = cachedExchangeRates == null || cachedExchangeRates.isOld()

        emit(
            RepositoryResponse(
                isLoading = isLoading,
                value = cachedExchangeRates
            )
        )

        if (isLoading) {
            emit(getExchangeRatesFromApi())
        }
    }

    private suspend fun getExchangeRatesFromApi(): RepositoryResponse<ExchangeRates> = withContext(
        Dispatchers.IO
    ) {
        try {
            val ticker = blockchainApi.getTicker()
            val exchangeRate = RepositoryResponse(false, ticker.toExchangeRates(Date()))
            if (exchangeRate.value != null) {
                exchangeRatesCache.setExchangeRates(exchangeRate.value)
            }
            exchangeRate
        } catch (exception: Exception) {
            exception.printStackTrace()
            RepositoryResponse(false, null)
        }
    }

    companion object {
        const val TAG = "ExchangeRatesRepository"
    }
}