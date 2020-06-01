package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.data.db.OptionalWalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.model.*
import com.jurajkusnier.bitcoinwalletbalance.exchangerates.ExchangeRatesRepository
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailViewModel @Inject constructor(private val detailRepository: DetailRepository, private val exchangeRatesRepository: ExchangeRatesRepository) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val mutableWalledDetailsViewState: MutableLiveData<WalledDetailsViewState> = MutableLiveData()
    val walledDetailsViewState: LiveData<WalledDetailsViewState>
        get() = mutableWalledDetailsViewState

    val showEditDialog = SingleLiveEvent<EditDialog.Parameters>()

    private fun updateViewState(optionalWalletRecord: OptionalWalletRecord, exchangeRate: ExchangeRateWithCurrencyCode?) {
        val builder = WalledDetailsViewState.Builder(walledDetailsViewState.value)

        if (optionalWalletRecord.source == RepositoryResponse.Source.API) {
            if (optionalWalletRecord.value == null) {
                builder.setError(WalledDetailsViewState.ErrorCode.OFFLINE)
            }
        }
        if (optionalWalletRecord.source == RepositoryResponse.Source.API ||
                optionalWalletRecord.source == RepositoryResponse.Source.CACHE_ONLY
        ) {
            builder.setLoading(false)
        }

        if (optionalWalletRecord.value != null) {
            if ((walledDetailsViewState.value?.wallet?.transactions?.size
                            ?: 0) <= optionalWalletRecord.value.transactions.size) {
                builder.setWallet(optionalWalletRecord.value)
            }
        }
        if (exchangeRate != null) {
            builder.setExchangeRate(exchangeRate.exchangeRate)
            builder.setCurrencyCode(exchangeRate.currencyCode)
        }
        mutableWalledDetailsViewState.value = builder.build()
    }

    fun loadWalletDetails(address: String, forceRefresh: Boolean = false) {
        data class WalletExchangeRate(val wallet: OptionalWalletRecord, val exchangeRate: RepositoryResponse<ExchangeRateWithCurrencyCode>)

        if (walledDetailsViewState.value?.wallet?.address == address && !forceRefresh) {
            return
        }

        disposables.clear()
        Observable.combineLatest(
                listOf(
                        detailRepository.getWalletDetails(address, forceRefresh),
                        exchangeRatesRepository.getExchangeRate())
        ) { result ->
            WalletExchangeRate(
                    result[0] as OptionalWalletRecord,
                    result[1] as RepositoryResponse<ExchangeRateWithCurrencyCode>)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    initLiveDataObject(address)
                }
                .subscribeBy(
                        onNext = {
                            updateViewState(it.wallet, it.exchangeRate.value)
                        },
                        onError = {
                            mutableWalledDetailsViewState.value = WalledDetailsViewState(loading = false, error = WalledDetailsViewState.ErrorCode.UNKNOWN)
                            it.printStackTrace()
                        }
                ).addTo(disposables)
    }

    //TODO: refactor ViewModels, duplicated in MainViewModel
    fun favouriteRecord() {
        walledDetailsViewState.value?.wallet?.address?.let { address ->
            detailRepository.favouriteRecord(address).subscribe().addTo(disposables)
        }
    }

    fun unfavouriteRecord() {
        walledDetailsViewState.value?.wallet?.address?.let { address ->
            detailRepository.unfavouriteRecord(address).subscribe().addTo(disposables)
        }
    }

    fun showEditDialog() {
        showEditDialog.value = EditDialog.Parameters(
                address = walledDetailsViewState.value?.wallet?.address ?: return,
                nickname = walledDetailsViewState.value?.wallet?.nickname ?: return)
    }

    private fun initLiveDataObject(address: String) {
        val viewState = walledDetailsViewState.value
        mutableWalledDetailsViewState.value = if (viewState == null || viewState.wallet?.address != address) {
            WalledDetailsViewState(loading = true)
        } else {
            viewState.copy(loading = true, error = null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}

data class WalledDetailsViewState(
        val wallet: WalletRecord? = null,
        val currencyCode: CurrencyCode? = null,
        val exchangeRate: ExchangeRate? = null,
        val loading: Boolean = false,
        val error: ErrorCode? = null) {

    class Builder constructor(viewState: WalledDetailsViewState?) {
        private var currencyCode: CurrencyCode? = viewState?.currencyCode
        private var loading: Boolean = viewState?.loading ?: true
        private var error: ErrorCode? = viewState?.error
        private var wallet: WalletRecord? = viewState?.wallet
        private var exchangeRate: ExchangeRate? = viewState?.exchangeRate

        fun setLoading(value: Boolean): Builder {
            loading = value
            return this
        }

        fun setError(value: ErrorCode?): Builder {
            error = value
            return this
        }

        fun setWallet(value: WalletRecord): Builder {
            wallet = value
            return this
        }

        fun setExchangeRate(value: ExchangeRate?): Builder {
            exchangeRate = value
            return this
        }

        fun setCurrencyCode(value: CurrencyCode?): Builder {
            currencyCode = value
            return this
        }

        fun build() = WalledDetailsViewState(
                loading = loading,
                error = error,
                wallet = wallet,
                exchangeRate = exchangeRate,
                currencyCode = currencyCode
        )
    }

    enum class ErrorCode {
        UNKNOWN,
        OFFLINE,
        INVALID_ADDRESS
    }
}