package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.api.OfflineException
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class DetailViewModel @Inject constructor(private val detailRepository: DetailRepository): ViewModel() {

    private val TAG = DetailViewModel::class.java.simpleName

    enum class LoadingState {DONE, LOADING, ERROR, ERROR_OFFLINE}

    private var disposables = mutableListOf<Disposable>()
    private var mWalletID: String? = null

    //Live Data
    private val _loadingState:MutableLiveData<LoadingState> = MutableLiveData()
    val loadingState:LiveData<LoadingState>
        get() = _loadingState
    private val _rawData:MutableLiveData<RawData> = MutableLiveData()
    val rawData:LiveData<RawData>
        get() = _rawData
    private val _walletRecord:MutableLiveData<WalletRecord> = MutableLiveData()
    val walletRecord:LiveData<WalletRecord>
            get() = _walletRecord

    init {
        _loadingState.value = LoadingState.DONE
    }

    fun initViewModel(walletID:String) {

        if (mWalletID == walletID) return
        mWalletID = walletID

        loadWalletDetails()
    }

    override fun onCleared() {
        clearDisposables()
        super.onCleared()
    }

    private fun clearDisposables() {
        for (disposable in disposables) {
            disposable.dispose()
        }
        disposables.clear()
    }

    fun loadWalletDetails() {

        clearDisposables()

        _loadingState.value = LoadingState.LOADING

        mWalletID?.let {address ->

            disposables.add(detailRepository.loadDetail(address)
                    .subscribe(
                            { data ->
                                _loadingState.value = LoadingState.DONE
                                _rawData.value = data

                                val timestamp = System.currentTimeMillis()
                                val v = walletRecord.value ?: WalletRecord(address,timestamp,true,false,data.final_balance)
                                v.lastAccess = timestamp
                                v.satoshis = data.final_balance
                                v.showInHistory = true

                                detailRepository.saveRecordToHistory(v)
                                _walletRecord.value = v
                            },
                            { error ->
                                if (error is OfflineException) {
                                    _loadingState.value = LoadingState.ERROR_OFFLINE
                                } else {
                                    _loadingState.value = LoadingState.ERROR
                                }
                                _rawData.value = null

                                Log.e(TAG,Log.getStackTraceString(error))
                            }
                    ))

           disposables.add(detailRepository.loadDetailFromDatabase(address)
                   .doOnSuccess {
                       data -> _walletRecord.value = data
                   }.doOnError {
                       _ -> _walletRecord.value = null
                   }.doOnComplete {
                       _walletRecord.value = null
                   }.subscribe())
        }

    }

    //TODO: refactor ViewModels, duplicated in MainViewModel
    fun favouriteRecord(record: WalletRecord) {
        record.favourite = true
        detailRepository.saveRecordToHistory(record)

        _walletRecord.value = record
    }

    fun unfavouriteRecord(record: WalletRecord) {
        record.favourite = false
        detailRepository.saveRecordToHistory(record)

        _walletRecord.value = record
    }


}