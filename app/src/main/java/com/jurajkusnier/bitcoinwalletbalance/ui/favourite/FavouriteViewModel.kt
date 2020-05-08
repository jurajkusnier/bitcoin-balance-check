package com.jurajkusnier.bitcoinwalletbalance.ui.favourite

import com.jurajkusnier.bitcoinwalletbalance.ui.history.HistoryViewModel
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainRepository
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainViewModel
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class FavouriteViewModel @Inject constructor(mainRepository: MainRepository) : MainViewModel(mainRepository) {

    override val TAG = HistoryViewModel::class.java.simpleName

    val unfavouritedAddress = SingleLiveEvent<String>()

    override fun unfavouriteRecord(address: String) {
        unfavouritedAddress.value = address
        super.unfavouriteRecord(address)
    }

    init {
        mainRepository.getFavourite().subscribe { _data.value = it }.addTo(compositeDisposable)
    }
}