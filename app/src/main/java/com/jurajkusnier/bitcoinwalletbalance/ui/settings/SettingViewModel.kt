package com.jurajkusnier.bitcoinwalletbalance.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRates
import com.jurajkusnier.bitcoinwalletbalance.data.model.RepositoryResponse
import com.jurajkusnier.bitcoinwalletbalance.exchangerates.ExchangeRatesRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SettingViewModel @Inject constructor(
        private val exchangeRatesRepository: ExchangeRatesRepository) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val mutableCurrencyViewState = MutableLiveData<CurrencyViewState>()
    val currencyViewState: LiveData<CurrencyViewState>
        get() = mutableCurrencyViewState

    fun selectCurrencyCode(currencyCode: CurrencyCode) {
        if (currencyViewState.value?.currencyCode == currencyCode) return
        mutableCurrencyViewState.value = currencyViewState.value?.copy(currencyCode = currencyCode)
    }

    fun setCurrencyCode() = currencyViewState.value?.currencyCode?.let {
        exchangeRatesRepository.setCurrencyCode(it)
    }

    private fun updateViewState(repositoryResponse: RepositoryResponse<ExchangeRates>?) {
        if (repositoryResponse == null) {
            mutableCurrencyViewState.value = currencyViewState.value?.copy(
                    connectionError = false,
                    loading = true)
                    ?: CurrencyViewState(null, null, false, true)
            return
        }

        mutableCurrencyViewState.value = currencyViewState.value?.copy(
                data = repositoryResponse.value ?: currencyViewState.value?.data,
                connectionError = if (repositoryResponse.source == RepositoryResponse.Source.API) {
                    repositoryResponse.value == null || repositoryResponse.value.values.isEmpty()
                } else {
                    currencyViewState.value?.connectionError ?: false
                },
                loading = if (repositoryResponse.source == RepositoryResponse.Source.API || repositoryResponse.value?.values?.isNotEmpty() == true) {
                    false
                } else {
                    currencyViewState.value?.loading ?: true
                }
        )
    }

    init {
        updateViewState(null)

        exchangeRatesRepository.getCurrencyCode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                    mutableCurrencyViewState.value = currencyViewState.value?.copy(currencyCode = it)
                }, onError = {
                    it.printStackTrace()
                })
                .addTo(disposables)

        exchangeRatesRepository.getExchangeRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            updateViewState(it)
                        },
                        onError = {
                            it.printStackTrace()
                            mutableCurrencyViewState.postValue(currencyViewState.value?.copy(connectionError = true))
                        }
                ).addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    companion object {
        const val TAG = "SettingViewModel"
    }
}

data class CurrencyViewState(
        val currencyCode: CurrencyCode?,
        val data: ExchangeRates?,
        val connectionError: Boolean,
        val loading: Boolean)