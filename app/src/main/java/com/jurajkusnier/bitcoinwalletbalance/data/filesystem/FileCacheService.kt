package com.jurajkusnier.bitcoinwalletbalance.data.filesystem

import android.content.Context
import android.util.Log
import com.jurajkusnier.bitcoinwalletbalance.data.model.AllTransactions
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.ObservableSource
import java.io.File
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject


class FileCacheService @Inject constructor(private val context:Context, private val moshi: Moshi) {

    val TAG =FileCacheService::class.java.simpleName

    private fun getFileName(walletID: String):String {
        return "$walletID.json"
    }


    fun getTransactionsFromFile(walletID:String): Observable<AllTransactions> {

        val filename = getFileName(walletID)

        val fileObserver = ObservableSource<AllTransactions> { observer ->
            try {
                val file = File(context.filesDir,filename)
                val content = file.readText()

                val moshiAdapter =  moshi.adapter(AllTransactions::class.java)
                val transactions = moshiAdapter.fromJson(content)?: AllTransactions(listOf())
                observer.onNext(transactions)
                observer.onComplete()
            } catch (e: Exception) {
                observer.onError(e)
            }
        }

        return Observable.defer<AllTransactions> { fileObserver }
    }

    fun setTransactionsToFile(walletID: String, transactions: AllTransactions) {

        val filename = getFileName(walletID)

        try {
            val file = File(context.filesDir,filename)

            val moshiAdapter =  moshi.adapter(AllTransactions::class.java)
            val transactionsJson = moshiAdapter.toJson(transactions)

            file.writeText(transactionsJson)


        } catch (e: IOException) {
            Log.e(TAG,Log.getStackTraceString(e))
        }
    }
}