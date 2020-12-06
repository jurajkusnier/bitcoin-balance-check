package com.jurajkusnier.bitcoinwalletbalance.utils

import android.content.Context
import com.jurajkusnier.bitcoinwalletbalance.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun isBitcoinAddressValid(address:String):Boolean {
    //TODO: use checksum to validate addresses
    return address.matches("^[1-9A-HJ-NP-Za-z]+$".toRegex())
}

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

class CustomDate(context: Context) {

    private val dateFormat = context.getString(R.string.date_format)
    private val aFewSecondsAgo = context.getString(R.string.a_few_seconds_ago)
    private val res = context.resources
    private val lastUpdated = context.getString(R.string.last_updated)
    private val lastUpdatedOn = context.getString(R.string.last_updated_on)
    private val lastUpdatedDateFormat = context.getString(R.string.last_updated_time_format)

    fun getDate(time: Long): String {
        if (time <= 0 ) return ""
        val timeDiffMs = (System.currentTimeMillis() - time * 1000L) / 1000L
        if (timeDiffMs < 60) return aFewSecondsAgo
        if (timeDiffMs < 60 * 60) {
            val min = (timeDiffMs.toDouble() / 60).roundToInt()
            return res.getQuantityString(R.plurals.x_min_ago, min, min)
        }
        if (timeDiffMs < 60 * 60 * 24) {
            val hour = (timeDiffMs.toDouble() / (60 * 60)).roundToInt()
            return res.getQuantityString(R.plurals.x_h_ago, hour, hour)
        }

        val date = Date(time * 1000L)
        val sdf = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        return sdf.format(date)
    }

    fun getLastUpdatedString(time: Long):String {
        if (time <= 0 ) return ""
        val timeDiffMs = (System.currentTimeMillis() - time) / 1000L
        if (timeDiffMs < 60) return String.format(lastUpdated, aFewSecondsAgo)
        if (timeDiffMs < 60 * 60) {
            val min = (timeDiffMs.toDouble() / 60).roundToInt()
            return String.format(lastUpdated,res.getQuantityString(R.plurals.x_min_ago, min, min))
        }
        if (timeDiffMs < 60 * 60 * 24) {
            val hour = (timeDiffMs.toDouble() / (60 * 60)).roundToInt()
            return String.format(lastUpdated,res.getQuantityString(R.plurals.x_h_ago, hour, hour))
        }

        val date = Date(time)
        val sdf = SimpleDateFormat(lastUpdatedDateFormat, Locale.ENGLISH)
        return String.format(lastUpdatedOn,sdf.format(date))
    }
}









