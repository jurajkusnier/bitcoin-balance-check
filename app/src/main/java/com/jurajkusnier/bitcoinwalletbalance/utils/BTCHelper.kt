package com.jurajkusnier.bitcoinwalletbalance.utils

import android.content.Context
import com.jurajkusnier.bitcoinwalletbalance.R
import java.text.SimpleDateFormat
import java.util.*


fun sathoshiToBTCstring(s:Long):String {
    val sStr = s.toString()
    var sOut: String

    if (sStr.length <= 8) {
        sOut = sStr
        for (i in 1..(8 - sStr.length)) {
            sOut = "0$sOut"
        }
        sOut = "0.$sOut"
    } else {
        sOut = sStr.substring(sStr.length - 8)
        sOut = "${sStr.substring(0,sStr.length - 8)}.$sOut"
    }

    return "$sOut BTC"
}
class CustomDate(private val context: Context) {

    val dateFormat = context.getString(R.string.date_format)
    val aFewSecondsAgo = context.getString(R.string.a_few_seconds_ago)
    val res = context.resources

    fun getDate(time: Long): String {

        val timeDiffMs = (System.currentTimeMillis() - time * 1000L) / 1000L
        if (timeDiffMs < 60) return aFewSecondsAgo
        if (timeDiffMs < 60 * 60) {
            val min = Math.round(timeDiffMs.toDouble() / 60).toInt()
            return res.getQuantityString(R.plurals.x_min_ago, min, min)
        }
        if (timeDiffMs < 60 * 60 * 24) {
            val hour = Math.round(timeDiffMs.toDouble() / (60 * 60)).toInt()
            return res.getQuantityString(R.plurals.x_h_ago, hour, hour)
        }

        val date = Date(time * 1000L)
        val sdf = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        return sdf.format(date)
    }
}









