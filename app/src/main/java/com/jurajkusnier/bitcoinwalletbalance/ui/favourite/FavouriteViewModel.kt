package com.jurajkusnier.bitcoinwalletbalance.ui.favourite

import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.ui.history.HistoryViewModel
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainRepository
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainViewModel
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent
import javax.inject.Inject

class FavouriteViewModel @Inject constructor (private val mainRepository: MainRepository): MainViewModel(mainRepository) {

    override val TAG = HistoryViewModel::class.java.simpleName

    val unfavouritedItem = SingleLiveEvent<WalletRecord>()

    override fun unfavouriteRecord(record: WalletRecord) {
        val recordCopy= record.copy()
        unfavouritedItem.value = recordCopy

        super.unfavouriteRecord(record)
    }

    init {
        mainRepository.getFavourite().subscribe { _data.value = it }
    }
}