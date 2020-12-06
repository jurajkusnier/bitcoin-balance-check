package com.jurajkusnier.bitcoinwalletbalance.ui.mainlist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListViewModel @ViewModelInject constructor(private val repository: MainRepository) :
    ViewModel() {

    private val _items = MutableLiveData<List<WalletRecordEntity>>()
    val items: LiveData<List<WalletRecordEntity>>
        get() = _items

    init {
        viewModelScope.launch {
            repository.getAll().collect {
                _items.value = it
            }
        }
    }
}