package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.api.BlockchainApiService
import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class DetailViewModel @Inject constructor(val blockchainApi: BlockchainApiService): ViewModel() {

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

        mWalletID?.let {
            disposable = blockchainApi.getDetails(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { data ->
                                _loadingState.value = LoadingState.DONE
                                _rawData.postValue(data);
                            },
                            { error ->
                                _loadingState.value = LoadingState.ERROR
                                Log.e(TAG,Log.getStackTraceString(error))
                            }
                    )
        }

    }


}