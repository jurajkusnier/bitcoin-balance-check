package com.jurajkusnier.bitcoinwalletbalance.ui.edit

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabase
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EditDialogRepository @Inject constructor(private val appDatabase: AppDatabase) {

    val TAG = EditDialogRepository::class.java.simpleName

    fun setNickname(address:String, nickname: String) {
        Observable.fromCallable {
            appDatabase.walletRecordDao().setNickName(address,nickname)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
                .subscribe ()
    }

}