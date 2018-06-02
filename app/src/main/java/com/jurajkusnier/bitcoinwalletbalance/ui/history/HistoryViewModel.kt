package com.jurajkusnier.bitcoinwalletbalance.ui.history

import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainRepository
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainViewModel
import javax.inject.Inject

class HistoryViewModel @Inject constructor (private val mainRepository: MainRepository): MainViewModel(mainRepository) {

    override val TAG = HistoryViewModel::class.java.simpleName

    init {
        mainRepository.getHistory().subscribe { _data.value = it }
    }
}