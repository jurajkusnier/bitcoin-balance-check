package com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog

import android.graphics.Bitmap
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class QrViewModel @ViewModelInject constructor(
    private val barcodeEncoder: BarcodeEncoder,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _barcodeBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val barcodeBitmap: LiveData<Bitmap>
        get() = _barcodeBitmap

    init {
        val address = savedStateHandle.get<String>(QrDialog.WALLET_ID) ?: throw Exception(
            "Address not present"
        )
        (viewModelScope + Dispatchers.IO).launch {
            _barcodeBitmap.postValue(barcodeEncoder.encodeBitmap(address, BarcodeFormat.QR_CODE, 256, 256))
        }
    }
}