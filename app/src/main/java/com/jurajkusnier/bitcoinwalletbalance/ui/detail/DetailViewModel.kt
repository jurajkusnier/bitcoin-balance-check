package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class DetailViewModel @Inject constructor(private val detailRepository: DetailRepository): ViewModel() {

    private val TAG = DetailViewModel::class.java.simpleName

    enum class LoadingState {DONE, LOADING, ERROR}

    private var disposable: Disposable? = null
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
        disposable?.dispose()
        super.onCleared()
    }

    fun loadWalletDetails() {

        disposable?.dispose()

        _loadingState.value = LoadingState.LOADING

        mWalletID?.let {address ->

            detailRepository.loadDetailFromDatabase(address).subscribe({
                        data -> _walletRecord.value = data
            })

            disposable = detailRepository.loadDetail(address)
                    .subscribe(
                            { data ->
                                _loadingState.value = LoadingState.DONE
                                _rawData.postValue(data)

                                val timestamp = System.currentTimeMillis()
                                val v = walletRecord.value ?: WalletRecord(address,timestamp,true,false,data.final_balance)
                                v.lastAccess = timestamp
                                v.satoshis = data.final_balance
                                v.showInHistory = true

                                detailRepository.saveRecordToHistory(v)

                            },
                            { error ->
                                _loadingState.value = LoadingState.ERROR
                                Log.e(TAG,Log.getStackTraceString(error))

                            }
                    )
        }

    }


}