package com.jurajkusnier.bitcoinwalletbalance.ui.main

import androidx.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent

class MainViewModel : ViewModel() {

    val action = SingleLiveEvent<Actions>()

    fun openDetails(address:String) {
        action.value = Actions.OpenDetails(address)
    }
}

sealed class Actions {
    data class OpenDetails(val address: String): Actions()
}