package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.api.BlockchainApi
import com.jurajkusnier.bitcoinwalletbalance.data.db.*
import com.jurajkusnier.bitcoinwalletbalance.cache.FileCacheService
import com.jurajkusnier.bitcoinwalletbalance.data.api.AllTransactions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val blockchainApi: BlockchainApi,
    private val databaseDao: AppDatabaseDao,
    private val fileCacheService: FileCacheService
) {

    suspend fun getWalletDetailsFlow(address: String) = flow {
        databaseDao.updateWalletRecordAccessTime(address)
        databaseDao.getWalletRecordFlow(address).collect {
            Log.d(
                "=TEST=",
                "getWalletDetailsFlow($address).address = ${it?.address} lastUpdate = ${it?.lastUpdate}"
            )
            if (it != null) {
                val transactions = fileCacheService.getTransactionsFromFile(address)?.transactions
                emit(WalletRecordAction.ItemUpdated(it.toWalletRecord(transactions)))
            }

            if (it == null || it.isOld()) {
                if (it == null) emit(WalletRecordAction.LoadingStart)
                val walletRecord = getWalletDetailsFromApi(address)
                if (it == null) emit(WalletRecordAction.LoadingEnd(walletRecord != null))
                if (walletRecord != null) {
                    if (it == null) {
                        insertNewWalletRecord(walletRecord)
                    } else {
                        updateWalletRecord(walletRecord)
                    }
                }
            }
        }
    }

    suspend fun reload(address: String, walletRecord: WalletRecord?): ReloadResult {
        val apiWalletRecord = getWalletDetailsFromApi(address) ?: return ReloadResult.Failed
        if (walletRecord != null) {
            updateWalletRecord(apiWalletRecord)
        } else {
            insertNewWalletRecord(apiWalletRecord)
        }
        return ReloadResult.Success
    }

    private suspend fun getWalletDetailsFromApi(address: String): WalletRecord? = try {
        Log.d("=TEST=", "getWalletDetailsFromApi($address)")
        blockchainApi.getDetails(address).toWalletRecord(address)
    } catch (exception: Exception) {
        null
    }

    private suspend fun insertNewWalletRecord(walletRecord: WalletRecord) {
        Log.d("=TEST=", "insertNewWalletRecord(${walletRecord.address})")
        databaseDao.addWalletRecord(WalletRecordEntity.fromWalletRecord(walletRecord))
        fileCacheService.setTransactionsToFile(
            walletRecord.address,
            AllTransactions(walletRecord.transactions)
        )
    }

    private suspend fun updateWalletRecord(walletRecord: WalletRecord) {
        Log.d(
            "=TEST=",
            "updateWalletRecord(${walletRecord.address}) latUpdate = ${walletRecord.lastUpdate}"
        )
        databaseDao.updateWalletRecord(WalletRecordEntity.fromWalletRecord(walletRecord))
        fileCacheService.setTransactionsToFile(
            walletRecord.address,
            AllTransactions(walletRecord.transactions)
        )
    }

    enum class ReloadResult {
        Success, Failed
    }

}