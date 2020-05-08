package com.jurajkusnier.bitcoinwalletbalance.ui.main

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabase
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainRepository @Inject constructor(private val appDatabase: AppDatabase) {

    fun getHistory(): Flowable<List<WalletRecordEntity>> {
        return appDatabase.walletRecordDao().getHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getFavourite(): Flowable<List<WalletRecordEntity>> {
        return appDatabase.walletRecordDao().getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteRecord(address: String): Completable {
        return appDatabase.walletRecordDao().removeRecordFromHistory(address)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
    }

    fun recoverDeletedRecord(address: String): Completable {
        return appDatabase.walletRecordDao().returnRecordToHistory(address)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
    }


    //TODO: refactor Repositories, Duplicated in DetailViewModel
    fun saveRecordToHistory(recordEntity: WalletRecordEntity) {

        Observable.fromCallable {
            appDatabase.walletRecordDao().insertOrUpdateWalletRecord(recordEntity)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
                .subscribe()
    }

    fun favouriteRecord(address: String) {
        appDatabase.walletRecordDao().favouriteRecord(address)
                .subscribeOn(Schedulers.io())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
                .subscribe()
    }

    fun unfavouriteRecord(address: String) {
        appDatabase.walletRecordDao().unfavouriteRecord(address)
                .subscribeOn(Schedulers.io())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
                .subscribe()
    }

    companion object {
        private const val TAG = "MainRepository"
    }

}