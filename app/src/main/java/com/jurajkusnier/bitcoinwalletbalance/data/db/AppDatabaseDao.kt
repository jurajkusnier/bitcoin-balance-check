package com.jurajkusnier.bitcoinwalletbalance.data.db

import androidx.room.*
import com.jurajkusnier.bitcoinwalletbalance.data.model.UpdateTime
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface AppDatabaseDao {

    @Query("SELECT * FROM WalletRecord WHERE showInHistory = 1 order by lastAccess desc")
    fun getHistory(): Flowable<List<WalletRecordEntity>>

    @Query("SELECT * FROM WalletRecord WHERE favourite = 1 order by lastAccess desc")
    fun getFavorites(): Flowable<List<WalletRecordEntity>>

    @Query("SELECT * FROM WalletRecord WHERE address = :address LIMIT 1")
    fun getWalletRecord(address: String): Flowable<WalletRecordEntity>

    @Query("SELECT lastUpdate FROM WalletRecord WHERE address = :address LIMIT 1")
    fun getUpdateTime(address: String): Single<UpdateTime>

    @Query("SELECT * FROM WalletRecord WHERE address = :address LIMIT 1")
    fun _getWalletRecord(address: String): List<WalletRecordEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWalletRecord(walletRecordEntity: WalletRecordEntity)

    fun insertOrUpdateWalletRecord(walletRecordEntity: WalletRecordEntity) {
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
    fun favouriteRecord(address: String): Completable

    @Query("UPDATE WalletRecord SET favourite = 0 WHERE address = :address")
    fun unfavouriteRecord(address: String): Completable

    @Query("UPDATE WalletRecord SET nickname = :nickname WHERE address = :address")
    fun setNickName(address: String, nickname: String)

    @Delete
    fun deleteWalletRecord(walletRecordEntity: WalletRecordEntity)

    @Query("UPDATE WalletRecord SET showInHistory = 0 WHERE address = :address")
    fun removeRecordFromHistory(address: String): Completable

    @Query("UPDATE WalletRecord SET showInHistory = 1 WHERE address = :address")
    fun returnRecordToHistory(address: String) : Completable

}