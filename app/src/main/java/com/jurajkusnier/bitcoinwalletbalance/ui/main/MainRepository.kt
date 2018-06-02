package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabase
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainRepository @Inject constructor(private val appDatabase: AppDatabase) {

    val TAG = MainRepository::class.java.simpleName

    fun getHistory() : Flowable<List<WalletRecord>> {
        return appDatabase.walletRecordDao().getHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun getFavourite() :Flowable<List<WalletRecord>> {
        return appDatabase.walletRecordDao().getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteRecord(record: WalletRecord) {
        Observable.fromCallable {
            appDatabase.walletRecordDao().removeRecordFromHistory(record.address)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
                .subscribe ()
    }

    //Duplicated
    fun saveRecordToHistory(record:WalletRecord) {

        Observable.fromCallable {
            appDatabase.walletRecordDao().addWalletRecord(record)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.e(TAG,Log.getStackTraceString(it)) }
                .subscribe ()
    }
}