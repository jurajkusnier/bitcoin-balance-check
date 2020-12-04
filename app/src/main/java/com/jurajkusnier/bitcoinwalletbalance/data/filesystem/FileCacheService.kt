package com.jurajkusnier.bitcoinwalletbalance.data.filesystem

import android.content.Context
import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.model.AllTransactions
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject


class FileCacheService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {

    private fun getFileName(walletID: String): String {
        return "$walletID.json"
    }

    fun getTransactionsFromFile(walletID: String): AllTransactions? {
        val filename = getFileName(walletID)
        return try {
            val file = File(context.filesDir, filename)
            val content = file.readText()
            val moshiAdapter = moshi.adapter(AllTransactions::class.java)
            val transactions = moshiAdapter.fromJson(content) ?: AllTransactions(listOf())
            transactions
        } catch (e: Exception) {
            null
        }
    }

    fun setTransactionsToFile(walletID: String, transactions: AllTransactions) {
        val filename = getFileName(walletID)
        try {
            val file = File(context.filesDir, filename)
            val moshiAdapter = moshi.adapter(AllTransactions::class.java)
            val transactionsJson = moshiAdapter.toJson(transactions)
            file.writeText(transactionsJson)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    companion object {
        private const val TAG = "FileCacheService"
    }
}