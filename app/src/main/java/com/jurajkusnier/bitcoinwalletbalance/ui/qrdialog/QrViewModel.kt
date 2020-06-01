package com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class QrViewModel @Inject constructor(private val barcodeEncoder: BarcodeEncoder) : ViewModel() {

    private val _barcodeBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val barcodeBitmap: LiveData<Bitmap>
        get() = _barcodeBitmap

    private var lastQrCode: String? = null
    private val compositeDisposable = CompositeDisposable()

    fun generateQrCode(walletID: String) {
        if (walletID != lastQrCode) {
            lastQrCode = walletID
            Maybe.fromCallable {
                barcodeEncoder.encodeBitmap(walletID, BarcodeFormat.QR_CODE, 256, 256)
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        _barcodeBitmap.value = it
                    }.addTo(compositeDisposable)

        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}