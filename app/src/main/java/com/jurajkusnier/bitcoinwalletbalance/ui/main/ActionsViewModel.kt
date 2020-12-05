package com.jurajkusnier.bitcoinwalletbalance.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialog
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ActionsViewModel @ViewModelInject constructor(private val repository: MainRepository) :
    ViewModel() {

    val action = SingleLiveEvent<Actions>()

    fun toggleFavourite(item: WalletRecordEntity) {
        if (item.favourite) {
            unfavouriteItem(item.address)
        } else {
            favouriteItem(item.address)
        }
    }

    private fun favouriteItem(address: String) {
        viewModelScope.launch {
            repository.favouriteItem(address)
            action.value = Actions.ItemFavourited(address)
        }
    }

    private fun unfavouriteItem(address: String) {
        viewModelScope.launch {
            repository.unfavouriteItem(address)
            action.value = Actions.ItemUnfavourited(address)
        }
    }

    fun deleteItem(address: String) {
        viewModelScope.launch {
            repository.deleteItem(address)
            action.value = Actions.ItemDeleted(address)
        }
    }

    fun recoverItem(address: String) {
        viewModelScope.launch {
            repository.undeleteItem(address)
        }
    }

    fun showEditDialog(item: WalletRecordEntity) {
        action.value = Actions.ShowEditDialog(
            EditDialog.Parameters(
                address = item.address,
                nickname = item.nickname
            )
        )
    }

    sealed class Actions() {
        data class ItemDeleted(val address: String) : Actions()
        data class ItemFavourited(val address: String) : Actions()
        data class ItemUnfavourited(val address: String) : Actions()
        data class ShowEditDialog(val parameters: EditDialog.Parameters) : Actions()
    }

}