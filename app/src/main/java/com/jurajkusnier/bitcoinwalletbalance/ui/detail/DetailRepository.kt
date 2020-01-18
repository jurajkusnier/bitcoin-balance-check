package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.api.BlockchainApiService
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabase
import com.jurajkusnier.bitcoinwalletbalance.data.db.ConversionPrefs
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordView
import com.jurajkusnier.bitcoinwalletbalance.data.filesystem.FileCacheService
import com.jurajkusnier.bitcoinwalletbalance.data.model.AllTransactions
import com.jurajkusnier.bitcoinwalletbalance.data.model.RawData
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailRepository @Inject constructor(
        private val blockchainApi: BlockchainApiService,
        private val appDatabase: AppDatabase,
        private val conversionPrefs: ConversionPrefs,
        private val fileCacheService: FileCacheService) {

    val TAG = DetailRepository::class.java.simpleName
    var lastWalletDetails: WalletRecordView? = null

    fun getLiveConversion() = conversionPrefs.liveExchangeRate

    private fun loadDetailFromFileSystem(walletID: String): Observable<AllTransactions> {
        return fileCacheService.getTransactionsFromFile(walletID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadDetailFromDatabase(walletID: String): Maybe<WalletRecord> {
        return appDatabase.walletRecordDao().getWalletRecord(walletID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadDetailFromAPI(walletID: String): Observable<RawData> {
        return blockchainApi.getDetails(walletID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun loadDetails(address: String, apiOnly: Boolean = false): Observable<WalletRecordView?> {

        val sources =
                if (apiOnly) {
                    arrayListOf(loadDetailFromAPI(address)).asIterable()
                } else {
                    arrayListOf(
                            loadDetailFromDatabase(address).toObservable(),
                            loadDetailFromFileSystem(address),
                            loadDetailFromAPI(address)).asIterable()
                }

        return Observable.concatDelayError(sources)
                .observeOn(AndroidSchedulers.mainThread(), true)
                .map {
                    when (it) {
                        //API
                        is RawData -> {
                            val timestamp = System.currentTimeMillis()
                            val nickname = lastWalletDetails?.nickname ?: ""

                            Log.d(TAG, "API respone: RawData($address,${it.final_balance})")

                            val newWalletRecord = WalletRecordView(address, nickname, timestamp, timestamp, true, lastWalletDetails?.favourite == true, it.total_received, it.total_sent, it.final_balance, it.txs, false)

                            saveRecordToHistory(newWalletRecord)

                            lastWalletDetails = newWalletRecord

                            lastWalletDetails

                        }
                        //DB
                        is WalletRecord -> {

                            Log.d(TAG, "DB response: WalletRecord($address,${it.finalBalance})")

                            lastWalletDetails = WalletRecordView(it.address, it.nickname, it.lastAccess, it.lastUpdate, it.showInHistory, it.favourite, it.totalReceived, it.totalSent, it.finalBalance, listOf(), true)
                            lastWalletDetails
                        }
                        is AllTransactions -> {
                            Log.d(TAG, "FileSystem response: ${it.transactions.size} Transactions")
                            lastWalletDetails = lastWalletDetails?.copy(transactions = it.transactions)

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

    private fun saveRecordToHistory(record: WalletRecordView) {

        Observable.fromCallable {
            val newRecord = WalletRecord(record.address, record.nickname, record.lastAccess, record.lastUpdate, true, record.favourite, record.totalReceived, record.totalSent, record.finalBalance)

            appDatabase.walletRecordDao().addWalletRecord(newRecord)
            fileCacheService.setTransactionsToFile(record.address, AllTransactions(record.transactions))
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
                .subscribe()
    }

    fun favouriteRecord(walletID: String) {
        Observable.fromCallable {
            appDatabase.walletRecordDao().favouriteRecord(walletID)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
                .subscribe()
    }

    fun unfavouriteRecord(walletID: String) {
        Observable.fromCallable {
            appDatabase.walletRecordDao().unfavouriteRecord(walletID)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.e(TAG, Log.getStackTraceString(it)) }
                .subscribe()
    }

}