package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.api.OfflineException
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordView
import com.jurajkusnier.bitcoinwalletbalance.data.model.LiveExchangeRate
import com.jurajkusnier.bitcoinwalletbalance.data.model.OneTransaction
import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import com.squareup.moshi.Moshi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class DetailViewModel @Inject constructor(private val moshi: Moshi, private val detailRepository: DetailRepository): ViewModel() {

    private val TAG = DetailViewModel::class.java.simpleName

    enum class LoadingState {DONE, LOADING, ERROR, ERROR_OFFLINE}

    private var disposables = CompositeDisposable()
    private var mWalletID: String? = null

    //Live Data
    @Inject lateinit var liveExchangeRate: LiveExchangeRate

    private val _loadingState:MutableLiveData<LoadingState> = MutableLiveData()
    val loadingState:LiveData<LoadingState>
        get() = _loadingState

    private val _walletDetail:MutableLiveData<WalletRecordView> = MutableLiveData()
    val walletDetail:LiveData<WalletRecordView>
        get() = _walletDetail

//    private val _rawData:MutableLiveData<RawData> = MutableLiveData()
//    val rawData:LiveData<RawData>
//        get() = _rawData
//    private val _walletRecord:MutableLiveData<WalletRecord> = MutableLiveData()
//    val walletRecord:LiveData<WalletRecord>
//            get() = _walletRecord

    init {
        _loadingState.value = LoadingState.DONE
    }

    fun initViewModel(walletID:String) {

        if (mWalletID == walletID) return
        mWalletID = walletID

        loadWalletDetails()
    }

    override fun onCleared() {
        super.onCleared()

        // clear all the subscription
        disposables.clear()
    }

    fun loadWalletDetails() {

        disposables.clear()

        _loadingState.value = LoadingState.LOADING

        mWalletID?.let {address ->

            disposables.add(detailRepository.loadDetail(address)
                    .subscribe(
                            { data ->

                                val timestamp = System.currentTimeMillis()
                                val moshiAdapter =  moshi.adapter(Array<OneTransaction>::class.java)
                                val transactionsJson = moshiAdapter.toJson(data.txs)

                                val newRecord = WalletRecord(address,timestamp,timestamp,true,_walletDetail.value?.favourite == true, data.total_received, data.total_sent, data.final_balance,transactionsJson)
                                detailRepository.saveRecordToHistory(newRecord)

                                _walletDetail.value = WalletRecordView(data.address, timestamp,timestamp,true,_walletDetail.value?.favourite == true,data.total_received,data.total_sent,data.final_balance,data.txs)
                                _loadingState.value = LoadingState.DONE
                            },
                            { error ->
                                if (error is OfflineException) {
                                    _loadingState.value = LoadingState.ERROR_OFFLINE
                                } else {
                                    _loadingState.value = LoadingState.ERROR
                                }
                                Log.e(TAG,Log.getStackTraceString(error))
                            }
                    ))

           disposables.add(detailRepository.loadDetailFromDatabase(address)
                   .doOnSuccess {

                       data ->
                       val moshiAdapter =  moshi.adapter(Array<OneTransaction>::class.java)
                       val transactions = moshiAdapter.fromJson(data.transactions)?: emptyArray()
                       _walletDetail.value = WalletRecordView(data.address,data.lastAccess,data.lastUpdate,data.showInHistory,data.favourite,data.totalReceived,data.totalSent,data.finalBalance,transactions)
                   }.doOnError {
                       _ -> _walletDetail.value = null
                   }.doOnComplete {
                       _walletDetail.value  = null
                   }.subscribe())
        }

    }

    //TODO: refactor ViewModels, duplicated in MainViewModel
    fun favouriteRecord() {
        val address = _walletDetail.value?.address
        if (address != null) detailRepository.favouriteRecord(address)
        val newWalletRecordView = _walletDetail.value?.copy(favourite = true)
        _walletDetail.value = newWalletRecordView
    }

    fun unfavouriteRecord() {
        val address = _walletDetail.value?.address
        if (address != null) detailRepository.unfavouriteRecord(address)
        val newWalletRecordView = _walletDetail.value?.copy(favourite = false)
        _walletDetail.value = newWalletRecordView
    }


}