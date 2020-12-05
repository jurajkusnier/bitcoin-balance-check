package com.jurajkusnier.bitcoinwalletbalance.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import kotlinx.coroutines.launch

class ActionsViewModel @ViewModelInject constructor(private val repository: MainRepository) :
    ViewModel() {

    fun toggleFavourite(item: WalletRecordEntity) {
        viewModelScope.launch {
            if (item.favourite) {
                repository.unfavouriteItem(item.address)
            } else {
                repository.favouriteItem(item.address)
            }
        }
    }

    fun deleteItem(address: String) {
        viewModelScope.launch {
            repository.removeItem(address)
        }
    }


}