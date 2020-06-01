package com.jurajkusnier.bitcoinwalletbalance.exchangerates

import com.jurajkusnier.bitcoinwalletbalance.data.api.BlockchainApiService
import com.jurajkusnier.bitcoinwalletbalance.data.model.*
import com.jurajkusnier.bitcoinwalletbalance.utils.TimeConstants.Companion.FIFTEEN_MINUTES_IN_MS
import com.jurajkusnier.bitcoinwalletbalance.utils.isOlderThan
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.*
import javax.inject.Inject

class ExchangeRatesRepository @Inject constructor(
        private val currencyService: CurrencyService,
        private val blockchainApiService: BlockchainApiService,
        private val exchangeRatesCache: ExchangeRatesCache) {

    fun getCurrencyCode() = currencyService.getCurrencyCode()

    fun setCurrencyCode(currencyCode: CurrencyCode) {
        currencyService.setCurrencyCode(currencyCode)
    }

    fun getExchangeRate(): Observable<RepositoryResponse<ExchangeRateWithCurrencyCode>> {
        return Observable.combineLatest<CurrencyCode, RepositoryResponse<ExchangeRates>, Pair<RepositoryResponse<ExchangeRates>, CurrencyCode>>(
                currencyService.getCurrencyCode(),
                getExchangeRates(),
                BiFunction { currencyCode, exchangeRates ->
                    Pair(exchangeRates, currencyCode)
                }).map {
            val (exchangeRates, currencyCode) = it
            RepositoryResponse(
                    RepositoryResponse.Source.CACHE,
                    exchangeRates.value?.values?.get(currencyCode)?.let { exchangeRate ->
                        ExchangeRateWithCurrencyCode(exchangeRate, currencyCode)
                    }
            )
        }
                .filter { it.source != RepositoryResponse.Source.API }
                .startWith(
                        RepositoryResponse<ExchangeRateWithCurrencyCode>(RepositoryResponse.Source.CACHE, null)
                )
    }

    fun getExchangeRates(): Observable<RepositoryResponse<ExchangeRates>> {
        val lastUpdate = exchangeRatesCache.getCacheLastUpdate()
        return if (lastUpdate.isOlderThan(FIFTEEN_MINUTES_IN_MS)) {
            Observable.merge(
                    getExchangeRatesFromCache(),
                    getExchangeRatesFromApi().toObservable())
        } else {
            getExchangeRatesFromCache()
        }
    }

    private fun getExchangeRatesFromCache() = exchangeRatesCache.getExchangeRates()
            .map {
                RepositoryResponse(RepositoryResponse.Source.CACHE, it)
            }

    private fun getExchangeRatesFromApi() = blockchainApiService.getTicker()
            .map {
                RepositoryResponse(RepositoryResponse.Source.API, it.toExchangeRates(Date()))
            }.doAfterSuccess {
                exchangeRatesCache.setExchangeRates(it.value!!)
            }.onErrorReturn { RepositoryResponse(RepositoryResponse.Source.API, null) }

    companion object {
        const val TAG = "ExchangeRatesRepository"
    }
}
