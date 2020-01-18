package com.jurajkusnier.bitcoinwalletbalance.data.model

/*

Holds data from Blockain API
https://blockchain.info/api

 */

data class RawData(
        val total_received:Long,
        val total_sent:Long,
        val final_balance:Long,
        val txs:List<OneTransaction>)

data class AllTransactions(val transactions:List<OneTransaction>)

data class OneTransaction(
        val ver:Int,
        val inputs:List<OneInput>,
        val out:List<OneOut>,
        val time:Long,
        val hash:String)

data class OneInput(
        val sequence: Long,
        val prev_out: PrevOut?,
        val script:String)

data class PrevOut(val addr: String?,
              val value:Long)

data class OneOut(
        val addr_tag_link:String?,
        val addr_tag:String?,
        val addr:String?,
        val value:Long)