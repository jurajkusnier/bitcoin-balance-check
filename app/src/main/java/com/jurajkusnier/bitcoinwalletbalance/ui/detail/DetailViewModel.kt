package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordAction
import com.jurajkusnier.bitcoinwalletbalance.data.model.CurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRate
import com.jurajkusnier.bitcoinwalletbalance.data.model.ExchangeRateWithCurrencyCode
import com.jurajkusnier.bitcoinwalletbalance.ui.currency.ExchangeRatesRepository
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DetailViewModel @ViewModelInject constructor(
    private val detailRepository: DetailRepository,
    private val exchangeRatesRepository: ExchangeRatesRepository,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mutableWalledDetailsViewState: MutableLiveData<WalledDetailsViewState> =
        MutableLiveData()
    val walledDetailsViewState: LiveData<WalledDetailsViewState>
        get() = mutableWalledDetailsViewState

    val showEditDialog = SingleLiveEvent<EditDialog.Parameters>()
    private val address = savedStateHandle.get<String>(DetailFragment.WALLET_ID) ?: throw Exception(
        "Address not present"
    )

    init {
        Log.d(TAG, "address = $address")
        startDataFlow()
    }

    private fun startDataFlow() {
        viewModelScope.launch {
            detailRepository.getWalletDetailsFlow(address)
                .combine(exchangeRatesRepository.getExchangeRate()) { walletDetails, exchangeRate ->
                    updateViewState(walletDetails, exchangeRate)
                }.collect {}
        }
    }


    fun refresh() {
        viewModelScope.launch {
            val walletRecord = walledDetailsViewState.value?.wallet
            updateViewState(WalletRecordAction.LoadingStart)
            updateViewState(
                WalletRecordAction.LoadingEnd(
                    success = detailRepository.reload(
                        address, walletRecord
                    ) == DetailRepository.ReloadResult.Success
                )
            )
        }
    }

    private fun updateViewState(
        action: WalletRecordAction,
        exchangeRate: ExchangeRateWithCurrencyCode? = null
    ) {
        val oldState = walledDetailsViewState.value ?: WalledDetailsViewState()
        val newState = oldState.copy(
            wallet = if (action is WalletRecordAction.ItemUpdated) action.value else oldState.wallet,
            currencyCode = exchangeRate?.currencyCode ?: oldState.currencyCode,
            exchangeRate = exchangeRate?.exchangeRate ?: oldState.exchangeRate,
            loading = when (action) {
                is WalletRecordAction.LoadingStart -> true
                is WalletRecordAction.LoadingEnd -> false
                else -> oldState.loading
            },
            error = if (action is WalletRecordAction.LoadingEnd && !action.success && oldState.wallet == null) {
                WalledDetailsViewState.ErrorCode.UNKNOWN
            } else {
                null
            }
        )
        mutableWalledDetailsViewState.value = newState
    }

    fun showEditDialog() {
//        showEditDialog.value = EditDialog.Parameters(
//                address = walledDetailsViewState.value?.wallet?.address ?: return,
//                nickname = walledDetailsViewState.value?.wallet?.nickname ?: return)
    }

    companion object {
        const val TAG = "DetailViewModel"
    }
}

data class WalledDetailsViewState(
    val wallet: WalletRecord? = null,
    val currencyCode: CurrencyCode? = null,
    val exchangeRate: ExchangeRate? = null,
    val loading: Boolean = false,
    val error: ErrorCode? = null
) {
    enum class ErrorCode {
        UNKNOWN
    }
}