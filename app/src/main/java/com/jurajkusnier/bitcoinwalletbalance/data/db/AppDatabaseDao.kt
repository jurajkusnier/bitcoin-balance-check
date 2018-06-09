package com.jurajkusnier.bitcoinwalletbalance.data.db

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface AppDatabaseDao {

    @Query("SELECT * FROM WalletRecord WHERE showInHistory = 1 order by lastAccess desc")
    fun getHistory(): Flowable<List<WalletRecord>>

    @Query("SELECT * FROM WalletRecord WHERE favourite = 1 order by lastAccess desc")
    fun getFavorites(): Flowable<List<WalletRecord>>

    @Query("SELECT * FROM WalletRecord WHERE address = :address")
    fun getWalletRecord(address:String): Maybe<WalletRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWalletRecord(walletRecord: WalletRecord)

    @Query("UPDATE WalletRecord SET favourite = 1 WHERE address = :address")
    fun favouriteRecord(address: String)

    @Query("UPDATE WalletRecord SET favourite = 0 WHERE address = :address")
    fun unfavouriteRecord(address: String)

    @Delete
    fun deleteWalletRecord(walletRecord: WalletRecord)

    @Query("UPDATE WalletRecord SET showInHistory = 0 WHERE address = :address")
    fun removeRecordFromHistory(address:String)

}