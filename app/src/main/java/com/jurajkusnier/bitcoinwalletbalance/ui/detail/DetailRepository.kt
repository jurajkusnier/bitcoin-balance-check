package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.api.BlockchainApiService
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabaseDao
import com.jurajkusnier.bitcoinwalletbalance.data.db.OptionalWalletRecord
import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecordEntity
import com.jurajkusnier.bitcoinwalletbalance.data.filesystem.FileCacheService
import com.jurajkusnier.bitcoinwalletbalance.data.model.AllTransactions
import com.jurajkusnier.bitcoinwalletbalance.utils.TimeConstants.Companion.FIFTEEN_MINUTES_IN_MS
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val blockchainApi: BlockchainApiService,
    private val databaseDao: AppDatabaseDao,
    private val fileCacheService: FileCacheService
) {

    suspend fun favouriteRecord(walletID: String) = databaseDao.favouriteRecord(walletID)

    suspend fun unfavouriteRecord(walletID: String) = databaseDao.unfavouriteRecord(walletID)

    suspend fun getWalletDetails(address: String, forceRefresh: Boolean) = flow {
        val lastUpdate = getLastUpdated(address)
        if (lastUpdate == null || lastUpdate.isOlderThan(FIFTEEN_MINUTES_IN_MS) || forceRefresh) {
            emit(getWalletDetailFromApi(address))
        } else {
            getCachedDetails(address).collect {
                emit(it)
            }
        }
    }

    private fun getCachedDetails(address: String) =
        databaseDao.getWalletRecord(address).map {
            val transactions = fileCacheService.getTransactionsFromFile(address)
            OptionalWalletRecord(
                false,
                value = it?.toWalletRecord(transactions?.transactions)
            )
        }

    private suspend fun getWalletDetailFromApi(address: String): OptionalWalletRecord {
        Log.d("TEST", "getWalletDetailFromApi(address)")
        return try {
            val walletDetailsDTO = blockchainApi.getDetails(address)
            Log.d("TEST", "getWalletDetailFromApi(address) = $walletDetailsDTO")
            val walletRecord = walletDetailsDTO.toWalletDetails(address, System.currentTimeMillis())
            val optionalWalletRecord = OptionalWalletRecord(false, walletRecord)
            databaseDao.insertOrUpdateWalletRecord(WalletRecordEntity.fromWalletRecord(walletRecord))
            fileCacheService.setTransactionsToFile(
                walletRecord.address,
                AllTransactions(walletRecord.transactions)
            )
            optionalWalletRecord
        } catch (exception: Exception) {
            Log.e("TEST", "getWalletDetailFromApi(address) Exception")
            exception.printStackTrace()
            OptionalWalletRecord(false, null)
        }
    }

    private fun updateWalletRecordAccessTime(address: String) = databaseDao.updateWalletRecord(address)

    private suspend fun getLastUpdated(address: String) = databaseDao.getUpdateTime(address)
}