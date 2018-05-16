package com.jurajkusnier.bitcoinwalletbalance.data.model

/*

More info at https://blockchain.info/api

 */

data class RawData(
        val hash160: String,
        val address:String,
        val n_tx:Int,
        val total_received:Long,
        val total_sent:Long,
        val final_balance:Long,
        val txs:Array<OneTransaction>)

data class OneTransaction(
        val ver:Int,
        val inputs:Array<OneInput>,
        val out:Array<OneOut>,
        val result:Long,
        val time:Long,
        val hash:String)

data class OneInput(
        val sequence: Long,
        val prev_out: PrevOut,
        val script:String)

data class PrevOut(val addr: String,
              val value:Long)

data class OneOut(
        val addr_tag_link:String?,
        val addr_tag:String?,
        val spent:Boolean,
        val addr:String,
        val value:Long,
        val script:String)