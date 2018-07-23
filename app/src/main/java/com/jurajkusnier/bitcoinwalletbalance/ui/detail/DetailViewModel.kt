package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.api.OfflineException
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordView
import com.jurajkusnier.bitcoinwalletbalance.utils.isBitcoinAddressValid
import io.reactivex.disposables.Disposable
import java.io.IOException
import javax.inject.Inject


class DetailViewModel @Inject constructor(private val detailRepository: DetailRepository): ViewModel() {

    private val TAG = DetailViewModel::class.java.simpleName

    enum class LoadingState {DONE, LOADING, ERROR, ERROR_OFFLINE, ERROR_INVALID_ADDRESS}

    private var mWalletID: String? = null

    //Live Data
    val liveExchangeRate = detailRepository.getLiveConversion()

    private val _loadingState:MutableLiveData<LoadingState> = MutableLiveData()
    val loadingState:LiveData<LoadingState>
        get() = _loadingState

    private val _walletDetail:MutableLiveData<WalletRecordView> = MutableLiveData()
    val walletDetail:LiveData<WalletRecordView>
        get() = _walletDetail

    init {
        _loadingState.value = LoadingState.DONE
    }

    fun initViewModel(walletID:String) {

        if (mWalletID == walletID) return
        mWalletID = walletID

        if (isBitcoinAddressValid(walletID)) {
            loadWalletDetails()
        } else {
            _loadingState.value = LoadingState.ERROR_INVALID_ADDRESS
        }
    }

    var disposable:Disposable? = null

    override fun onCleared() {
        super.onCleared()

        disposable?.dispose()
    }

    fun loadWalletDetails(apiOnly:Boolean = false) {
        mWalletID?.let { address ->
            _loadingState.value = LoadingState.LOADING

            disposable?.dispose()

            disposable = detailRepository.loadDetails(address, apiOnly).subscribe (
            {
                data ->
                    _walletDetail.value = data
                    if (data?.fromCache == false) {
                        _loadingState.value = LoadingState.DONE
                    }
            }, {
                error ->
                Log.e(TAG,Log.getStackTraceString(error))
                when (error) {
                    is OfflineException -> _loadingState.value = LoadingState.ERROR_OFFLINE
                    is IOException -> {
                        //Ignore so far
                    }
                    else -> _loadingState.value = LoadingState.ERROR
                }
            })
        }
    }



    //TODO: refactor ViewModels, duplicated in MainViewModel
    fun favouriteRecord() {
        val address = _walletDetail.value?.address
        if (address != null) {
            detailRepository.favouriteRecord(address)
            val newWalletRecordView = _walletDetail.value?.copy(favourite = true)
            _walletDetail.value = newWalletRecordView
        }
    }

    fun unfavouriteRecord() {
        val address = _walletDetail.value?.address
        if (address != null) {
            detailRepository.unfavouriteRecord(address)
            val newWalletRecordView = _walletDetail.value?.copy(favourite = false)
            _walletDetail.value = newWalletRecordView
        }
    }


}