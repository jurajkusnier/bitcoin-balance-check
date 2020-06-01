package com.jurajkusnier.bitcoinwalletbalance.data.db

import android.content.SharedPreferences
import com.jurajkusnier.bitcoinwalletbalance.utils.TimeConstants.Companion.FIFTEEN_MINUTES_IN_MS
import com.jurajkusnier.bitcoinwalletbalance.utils.TimeConstants.Companion.TWO_DAYS_IN_MS
import javax.inject.Inject

class RateMePrefs @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun isTimeToShow(): Boolean {
        if (sharedPreferences.getBoolean(DONT_SHOW, false)) return false

        val currentTime = System.currentTimeMillis()
        val lastTime = sharedPreferences.getLong(SHOW_TIME, 0)

        if (lastTime <= 0) {
            val defaultTime = currentTime - TWO_DAYS_IN_MS + FIFTEEN_MINUTES_IN_MS
            setShowed(defaultTime)
            return false
        }

        val timeDiff = currentTime - lastTime
        return (timeDiff > TWO_DAYS_IN_MS)
    }

    fun setShowed(time: Long = System.currentTimeMillis()) {
        val editor = sharedPreferences.edit()
        editor.putLong(SHOW_TIME, time)
        editor.apply()
    }

    fun setNeverShowAgain() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(DONT_SHOW, true)
        editor.apply()
    }

    companion object {
        private const val TAG = "RateMePrefs"
        private const val DONT_SHOW = "rate_me_not"
        private const val SHOW_TIME = "rate_me_time"
    }

}