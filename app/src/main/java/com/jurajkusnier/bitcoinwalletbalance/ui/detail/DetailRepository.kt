package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.api.BlockchainApiService
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabase
import com.jurajkusnier.bitcoinwalletbalance.data.db.ConversionPrefs
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordView
import com.jurajkusnier.bitcoinwalletbalance.data.model.OneTransaction
import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import com.squareup.moshi.Moshi
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailRepository @Inject constructor(
        private val blockchainApi: BlockchainApiService,
        private val appDatabase: AppDatabase,
        private val conversionPrefs: ConversionPrefs,
        private val moshi: Moshi) {

    val TAG = DetailRepository::class.java.simpleName
    var lastWalletDetails:WalletRecordView? = null

    fun getLiveConversion() = conversionPrefs.liveExchangeRate

    private fun loadDetailFromDatabase(walletID: String):Maybe<WalletRecord> {
        return appDatabase.walletRecordDao().getWalletRecord(walletID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadDetailFromAPI(walletID:String):Observable<RawData> {
        return blockchainApi.getDetails(walletID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun loadDetails(address:String):Observable<WalletRecordView?> {

        val sources= arrayListOf(
                loadDetailFromDatabase(address).toObservable(),
                loadDetailFromAPI(address)).asIterable()

        return Observable.concatDelayError(sources)
                .observeOn(AndroidSchedulers.mainThread(),true)
                .map {
                    when (it) {
                        //API
                        is RawData -> {
                            val timestamp = System.currentTimeMillis()
                            val moshiAdapter =  moshi.adapter(Array<OneTransaction>::class.java)
                            val transactionsJson = moshiAdapter.toJson(it.txs)
                            val newRecord = WalletRecord(address,timestamp,timestamp,true,lastWalletDetails?.favourite == true, it.total_received, it.total_sent, it.final_balance, transactionsJson)
                            saveRecordToHistory(newRecord)

                            Log.d(TAG,"API respone: RawData($address,${it.final_balance})")

                            lastWalletDetails = WalletRecordView(address,timestamp,timestamp,true,lastWalletDetails?.favourite == true, it.total_received, it.total_sent, it.final_balance,it.txs, false)
                            lastWalletDetails

                        }
                        //DB
                        is WalletRecord -> {
                            val moshiAdapter =  moshi.adapter(Array<OneTransaction>::class.java)
                            val transactions = moshiAdapter.fromJson(it.transactions)?: emptyArray()

                            Log.d(TAG,"DB response: WalletRecord($address,${it.finalBalance})")

                            lastWalletDetails = WalletRecordView(it.address,it.lastAccess,it.lastUpdate,it.showInHistory,it.favourite,it.totalReceived,it.totalSent,it.finalBalance,transactions,true)
                            lastWalletDetails
                        }
                        //UNKNOWN
                        else -> {
                            Log.e(TAG, "Unknown response: $it")
                            null
                        }
                    }
        }
    }

    private fun saveRecordToHistory(record:WalletRecord) {

        Observable.fromCallable {
            appDatabase.walletRecordDao().addWalletRecord(record)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError { Log.e(TAG,Log.getStackTraceString(it)) }
        .subscribe ()
    }

    fun favouriteRecord(walletID: String) {
        Observable.fromCallable {
            appDatabase.walletRecordDao().favouriteRecord(walletID)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.e(TAG,Log.getStackTraceString(it)) }
                .subscribe ()
    }

    fun unfavouriteRecord(walletID: String) {
        Observable.fromCallable {
            appDatabase.walletRecordDao().unfavouriteRecord(walletID)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.e(TAG,Log.getStackTraceString(it)) }
                .subscribe ()
    }

}