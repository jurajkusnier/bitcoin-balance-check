package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.arch.lifecycle.ViewModel
import android.os.Handler
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val mainActivityRepository: MainActivityRepository) :ViewModel() {

    val UPDATE_DELAY = 10*60*1000L //10 min

    val TAG = MainActivityViewModel::class.simpleName

    private val handler = Handler()
    private val runnable:Runnable = object:Runnable {
        override fun run() {
            mainActivityRepository.updateCurrency()
            handler.postDelayed(this, UPDATE_DELAY)
        }
    }

    init {
        handler.post(runnable)
    }

    override fun onCleared() {
        mainActivityRepository.clear()
        super.onCleared()
    }

}