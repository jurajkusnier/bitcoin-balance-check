package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.api.BlockchainApiService
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabase
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailRepository @Inject constructor(private val blockchainApi: BlockchainApiService, private val appDatabase: AppDatabase) {

    val TAG = DetailRepository::class.java.simpleName

    fun loadDetailFromDatabase(walletID: String):Maybe<WalletRecord> {
        return appDatabase.walletRecordDao().getWalletRecord(walletID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun loadDetail(walletID:String):Observable<RawData> {
        return blockchainApi.getDetails(walletID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

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