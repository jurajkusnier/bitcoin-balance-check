package com.jurajkusnier.bitcoinwalletbalance.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

abstract class MainViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {


    open val TAG = MainViewModel::class.java.simpleName

    val deletedAddress = SingleLiveEvent<String>()

    protected val compositeDisposable = CompositeDisposable()
    protected val _data: MutableLiveData<List<WalletRecordEntity>> = MutableLiveData()
    val data: LiveData<List<WalletRecordEntity>>
        get() = _data

    fun deleteRecord(address: String) {
        deletedAddress.value = address
        mainRepository.deleteRecord(address).subscribe().addTo(compositeDisposable)
    }

    fun recoverDeletedRecord(address: String) {
        mainRepository.recoverDeletedRecord(address).subscribe().addTo(compositeDisposable)
    }

    fun favouriteRecord(address: String) {
        mainRepository.favouriteRecord(address)
    }

    open fun unfavouriteRecord(address: String) {
        mainRepository.unfavouriteRecord(address)
    }

    fun addRecord(recordEntity: WalletRecordEntity) {
        mainRepository.saveRecordToHistory(recordEntity)
    }

    fun editRecord(recordEntity: WalletRecordEntity) {

    }
}