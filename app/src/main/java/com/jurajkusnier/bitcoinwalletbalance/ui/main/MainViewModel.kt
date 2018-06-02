package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent

abstract class MainViewModel constructor(private val mainRepository: MainRepository): ViewModel() {

    open val TAG = MainViewModel::class.java.simpleName

    val deletedItem = SingleLiveEvent<WalletRecord>()

    protected val _data: MutableLiveData<List<WalletRecord>> = MutableLiveData()
    val data:LiveData<List<WalletRecord>>
        get() = _data

    fun deleteRecord(record: WalletRecord) {
        deletedItem.value = record
        mainRepository.deleteRecord(record)
    }

    fun favouriteRecord(record: WalletRecord) {
        record.favourite = true
        mainRepository.saveRecordToHistory(record)
    }

    open fun unfavouriteRecord(record: WalletRecord) {
        record.favourite = false
        mainRepository.saveRecordToHistory(record)
    }

    fun addRecord(record: WalletRecord) {
        mainRepository.saveRecordToHistory(record)
    }
}