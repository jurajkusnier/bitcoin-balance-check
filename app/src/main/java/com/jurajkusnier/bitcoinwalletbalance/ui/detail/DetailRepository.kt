package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import com.jurajkusnier.bitcoinwalletbalance.data.api.BlockchainApiService
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabase
import com.jurajkusnier.bitcoinwalletbalance.data.db.OptionalWalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.data.filesystem.FileCacheService
import com.jurajkusnier.bitcoinwalletbalance.data.model.AllTransactions
import com.jurajkusnier.bitcoinwalletbalance.data.model.RepositoryResponse
import com.jurajkusnier.bitcoinwalletbalance.data.model.UpdateTime
import com.jurajkusnier.bitcoinwalletbalance.utils.TimeConstants.Companion.FIFTEEN_MINUTES_IN_MS
import com.jurajkusnier.bitcoinwalletbalance.utils.Transformations
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class DetailRepository @Inject constructor(
        private val blockchainApi: BlockchainApiService,
        private val appDatabase: AppDatabase,
        private val fileCacheService: FileCacheService) {

    fun favouriteRecord(walletID: String): Completable = appDatabase.walletRecordDao()
            .favouriteRecord(walletID)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())


    fun unfavouriteRecord(walletID: String) = appDatabase.walletRecordDao()
            .unfavouriteRecord(walletID)
            .subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())

    fun getWalletDetails(address: String, forceRefresh: Boolean): Observable<OptionalWalletRecord> {
        return getLastUpdated(address).toObservable().flatMap {
            if (it.isOlderThan(FIFTEEN_MINUTES_IN_MS) || forceRefresh) {
                Observable.merge(
                        getWalletDetailsFromLocalCache(address),
                        getWalletDetailFromApi(address).toObservable())
            } else {
                getWalletDetailsOnlyFromLocalCache(address)
            }
        }.compose(Transformations.doAfterFirst {
            if (!forceRefresh) {
                updateWalletRecordAccessTime(address)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .startWith(OptionalWalletRecord(RepositoryResponse.Source.CACHE, null))
                .map {
                    it
                }
    }

    private fun getWalletDetailsOnlyFromLocalCache(address: String): Observable<OptionalWalletRecord> {
        return getWalletDetailsFromLocalCache(address).map {
            it.copy(source = RepositoryResponse.Source.CACHE_ONLY)
        }
    }

    private fun getWalletDetailsFromLocalCache(address: String): Observable<OptionalWalletRecord> {
        return Observable.combineLatest(
                appDatabase.walletRecordDao().getWalletRecord(address).toObservable(),
                fileCacheService.getTransactionsFromFile(address),
                BiFunction { walletRecordEntity, transactions ->
                    OptionalWalletRecord(RepositoryResponse.Source.CACHE, walletRecordEntity.toWalletRecord(transactions.transactions))
                }
        )
    }

    private fun getWalletDetailFromApi(address: String): Single<OptionalWalletRecord> {
        return blockchainApi.getDetails(address).map {
            OptionalWalletRecord(RepositoryResponse.Source.API, it.toWalletDetails(address, System.currentTimeMillis()))
        }.doAfterSuccess {
            it.value?.let { wallet ->
                appDatabase.walletRecordDao().insertOrUpdateWalletRecord(WalletRecordEntity.fromWalletRecord(wallet))
                fileCacheService.setTransactionsToFile(wallet.address, AllTransactions(wallet.transactions))
            }
        }.onErrorReturn {
            OptionalWalletRecord(RepositoryResponse.Source.API, null)
        }
    }

    private fun updateWalletRecordAccessTime(address: String) = appDatabase.walletRecordDao().updateWalletRecord(address)

    private fun getLastUpdated(address: String): Single<UpdateTime> {
        return appDatabase.walletRecordDao()
                .getUpdateTime(address)
                .onErrorReturn { UpdateTime(Date(0)) }
    }
}