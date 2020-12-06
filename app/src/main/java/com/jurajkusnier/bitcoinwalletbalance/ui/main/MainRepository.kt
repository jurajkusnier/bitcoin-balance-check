package com.jurajkusnier.bitcoinwalletbalance.ui.main

import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabaseDao
import javax.inject.Inject

class MainRepository @Inject constructor(private val databaseDao: AppDatabaseDao) {

    fun getAll() = databaseDao.getAll()

    suspend fun favouriteItem(address: String) = databaseDao.favouriteRecord(address)

    suspend fun unfavouriteItem(address: String) = databaseDao.unfavouriteRecord(address)

    suspend fun deleteItem(address: String) = databaseDao.deleteRecord(address)

    suspend fun undeleteItem(address: String) = databaseDao.undeleteRecord(address)

}