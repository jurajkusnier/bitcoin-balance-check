package com.jurajkusnier.bitcoinwalletbalance.data.api

import com.jurajkusnier.bitcoinwalletbalance.data.db.WalletRecord

/*

Holds data from Blockain API
https://blockchain.info/api

 */

data class WalletDetailsDTO(
    val total_received: Long,
    val total_sent: Long,
    val final_balance: Long,
    val txs: List<OneTransaction>
) {

    fun toWalletRecord(address: String, timestamp: Long = System.currentTimeMillis()): WalletRecord {
        return WalletRecord(
            address = address,
            lastAccess = timestamp,
            lastUpdate = timestamp,
            totalReceived =  total_received,
            totalSent = total_sent,
            finalBalance = final_balance,
            transactions = txs
        )
    }
}

data class AllTransactions(val transactions: List<OneTransaction>)

data class OneTransaction(
    val ver: Int,
    val inputs: List<OneInput>,
    val out: List<OneOut>,
    val time: Long,
    val hash: String
)

data class OneInput(
    val sequence: Long,
    val prev_out: PrevOut?,
    val script: String
)

data class PrevOut(
    val addr: String?,
    val value: Long
)

data class OneOut(
    val addr_tag_link: String?,
    val addr_tag: String?,
    val addr: String?,
    val value: Long
)