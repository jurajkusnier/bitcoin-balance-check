package com.jurajkusnier.bitcoinwalletbalance.data.db

import androidx.room.*
import com.jurajkusnier.bitcoinwalletbalance.data.model.UpdateTime
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDatabaseDao {

    @Query("SELECT * FROM WalletRecord WHERE showInHistory = 1 order by lastAccess desc")
    fun getAll(): Flow<List<WalletRecordEntity>>

//    @Query("SELECT * FROM WalletRecord WHERE showInHistory = 1 order by lastAccess desc")
//    fun getHistory(): Flow<List<WalletRecordEntity>>
//
//    @Query("SELECT * FROM WalletRecord WHERE favourite = 1 order by lastAccess desc")
//    fun getFavorites(): Flow<List<WalletRecordEntity>>

    @Query("SELECT * FROM WalletRecord WHERE address = :address LIMIT 1")
    fun getWalletRecord(address: String): Flow<WalletRecordEntity?>

    @Query("SELECT lastUpdate FROM WalletRecord WHERE address = :address LIMIT 1")
    suspend fun getUpdateTime(address: String): UpdateTime?

    @Query("SELECT * FROM WalletRecord WHERE address = :address LIMIT 1")
    fun _getWalletRecord(address: String): List<WalletRecordEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWalletRecord(walletRecordEntity: WalletRecordEntity)

    suspend fun insertOrUpdateWalletRecord(walletRecordEntity: WalletRecordEntity) {
        if (_getWalletRecord(walletRecordEntity.address).isEmpty()) {
            addWalletRecord(walletRecordEntity)
        } else {
            updateWalletRecord(
                    address = walletRecordEntity.address,
                    totalReceived = walletRecordEntity.totalReceived,
                    totalSent = walletRecordEntity.totalSent,
                    finalBalance = walletRecordEntity.finalBalance
            )
        }
    }

    @Query("UPDATE WalletRecord SET lastAccess = :lastAccess, showInHistory = 1 WHERE address = :address")
    fun updateWalletRecord(address: String, lastAccess: Long = System.currentTimeMillis())

    @Query("UPDATE WalletRecord SET totalReceived = :totalReceived, totalSent = :totalSent, finalBalance = :finalBalance, lastUpdate = :lastUpdate  WHERE address = :address")
    fun updateWalletRecord(address: String, totalReceived: Long, totalSent: Long, finalBalance: Long, lastUpdate: Long = System.currentTimeMillis())

    @Query("UPDATE WalletRecord SET favourite = 1 WHERE address = :address")
    suspend fun favouriteRecord(address: String)

    @Query("UPDATE WalletRecord SET favourite = 0 WHERE address = :address")
    suspend fun unfavouriteRecord(address: String)

//    @Query("UPDATE WalletRecord SET nickname = :nickname WHERE address = :address")
//    fun setNickName(address: String, nickname: String)
//
//    @Delete
//    fun deleteWalletRecord(walletRecordEntity: WalletRecordEntity)
//
//    @Query("UPDATE WalletRecord SET showInHistory = 0 WHERE address = :address")
//    fun removeRecordFromHistory(address: String): Completable
//
//    @Query("UPDATE WalletRecord SET showInHistory = 1 WHERE address = :address")
//    fun returnRecordToHistory(address: String) : Completable

}