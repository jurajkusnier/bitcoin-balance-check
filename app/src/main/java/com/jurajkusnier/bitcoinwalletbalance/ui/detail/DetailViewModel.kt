package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jurajkusnier.bitcoinwalletbalance.data.db.OptionalWalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRate
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRateWithCurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.ui.currency.ExchangeRatesRepository
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DetailViewModel @ViewModelInject constructor(
    private val detailRepository: DetailRepository,
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {

    private val mutableWalledDetailsViewState: MutableLiveData<WalledDetailsViewState> =
        MutableLiveData()
    val walledDetailsViewState: LiveData<WalledDetailsViewState>
        get() = mutableWalledDetailsViewState

    val showEditDialog = SingleLiveEvent<EditDialog.Parameters>()

    fun load(address: String, forceRefresh: Boolean = false) {
        if (walledDetailsViewState.value?.wallet?.address == address && !forceRefresh) {
            return
        }

        initLiveDataObject(address)

        viewModelScope.launch(Dispatchers.IO) {
            detailRepository.getWalletDetails(address, forceRefresh)
                .combine(exchangeRatesRepository.getExchangeRate()) { walletDetails, exchangeRate ->
                    updateViewState(walletDetails, exchangeRate)
                }.collect {}
        }
    }

    private fun updateViewState(
        optionalWalletRecord: OptionalWalletRecord,
        exchangeRate: ExchangeRateWithCurrencyCode?
    ) {
        val builder = WalledDetailsViewState.Builder(walledDetailsViewState.value)

        if (optionalWalletRecord.isLoading) {
            if (optionalWalletRecord.value == null) {
                builder.setError(WalledDetailsViewState.ErrorCode.OFFLINE)
            }
        }
        if (!optionalWalletRecord.isLoading) {
            builder.setLoading(false)
        }

        if (optionalWalletRecord.value != null) {
            if ((walledDetailsViewState.value?.wallet?.transactions?.size
                    ?: 0) <= optionalWalletRecord.value.transactions.size
            ) {
                builder.setWallet(optionalWalletRecord.value)
            }
        }
        if (exchangeRate != null) {
            builder.setExchangeRate(exchangeRate.exchangeRate)
            builder.setCurrencyCode(exchangeRate.currencyCode)
        }
        mutableWalledDetailsViewState.postValue(builder.build())
    }



    //TODO: refactor ViewModels, duplicated in MainViewModel
    fun favouriteRecord() {
        walledDetailsViewState.value?.wallet?.address?.let { address ->
            viewModelScope.launch(Dispatchers.IO) {
                detailRepository.favouriteRecord(address)
            }
        }
    }

    fun unfavouriteRecord() {
        walledDetailsViewState.value?.wallet?.address?.let { address ->
            viewModelScope.launch(Dispatchers.IO) {
                detailRepository.unfavouriteRecord(address)
            }
        }
    }

    fun showEditDialog() {
//        showEditDialog.value = EditDialog.Parameters(
//                address = walledDetailsViewState.value?.wallet?.address ?: return,
//                nickname = walledDetailsViewState.value?.wallet?.nickname ?: return)
    }

    private fun initLiveDataObject(address: String) {
        val viewState = walledDetailsViewState.value
        mutableWalledDetailsViewState.value =
            if (viewState == null || viewState.wallet?.address != address) {
                WalledDetailsViewState(loading = true)
            } else {
                viewState.copy(loading = true, error = null)
            }
    }
}

data class WalledDetailsViewState(
    val wallet: WalletRecord? = null,
    val currencyCode: CurrencyCode? = null,
    val exchangeRate: ExchangeRate? = null,
    val loading: Boolean = false,
    val error: ErrorCode? = null
) {

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