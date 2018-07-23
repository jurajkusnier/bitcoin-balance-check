package com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class QrViewModel @Inject constructor(private val barcodeEncoder: BarcodeEncoder): ViewModel() {

    private val _barcodeBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val barcodeBitmap: LiveData<Bitmap>
        get() = _barcodeBitmap

    private var lastQrCode:String? = null

    fun generateQrCode(walletID: String) {
        if (walletID != lastQrCode) {
            lastQrCode = walletID
            launch {
                _barcodeBitmap.postValue(barcodeEncoder.encodeBitmap(walletID, BarcodeFormat.QR_CODE, 256, 256))
            }
        }
    }

}