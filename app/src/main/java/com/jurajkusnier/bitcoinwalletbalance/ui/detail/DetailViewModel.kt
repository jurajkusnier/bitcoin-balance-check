package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.jurajkusnier.bitcoinwalletbalance.data.api.OfflineException
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordView
import com.jurajkusnier.bitcoinwalletbalance.utils.isBitcoinAddressValid
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.launch
import java.io.IOException
import javax.inject.Inject


class DetailViewModel @Inject constructor(private val detailRepository: DetailRepository, private val barcodeEncoder: BarcodeEncoder): ViewModel() {

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

    private val _barcodeBitmap:MutableLiveData<Bitmap> = MutableLiveData()
    val barcodeBitmap:LiveData<Bitmap>
        get() = _barcodeBitmap

    init {
        _loadingState.value = LoadingState.DONE
    }

    fun initViewModel(walletID:String) {

        if (mWalletID == walletID) return
        mWalletID = walletID

        if (isBitcoinAddressValid(walletID)) {
            loadWalletDetails()
            launch {
                generateQrCode(walletID)
            }
        } else {
            _loadingState.value = LoadingState.ERROR_INVALID_ADDRESS
        }
    }

    fun generateQrCode(walletID: String) {
        _barcodeBitmap.postValue(barcodeEncoder.encodeBitmap(walletID, BarcodeFormat.QR_CODE, 128, 128))
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