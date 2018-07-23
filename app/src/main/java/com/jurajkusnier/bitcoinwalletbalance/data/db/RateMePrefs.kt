package com.jurajkusnier.bitcoinwalletbalance.data.db

import android.content.SharedPreferences
import javax.inject.Inject

class RateMePrefs @Inject constructor(private val sharedPreferences: SharedPreferences) {

    val TAG = RateMePrefs::class.java.simpleName

    private val DONT_SHOW = "rate_me_not"
    private val SHOW_TIME = "rate_me_time"
    private val WAITITNG_TIME = 1000 * 60 * 60 * 48 //2 days in ms
    private val FIFTEEN_MINUTES= 1000 * 60 * 15 //15min in ms

    fun isTimeToShow():Boolean
    {
        if (sharedPreferences.getBoolean(DONT_SHOW,false)) return false

        val currentTime = System.currentTimeMillis()
        val lastTime = sharedPreferences.getLong(SHOW_TIME,0)

        if (lastTime <= 0) {
            val defaultTime = currentTime - WAITITNG_TIME + FIFTEEN_MINUTES
            setShowed(defaultTime)
            return false
        }

        val timeDiff = currentTime - lastTime
        return (timeDiff > WAITITNG_TIME)
    }

    private fun setShowed(time:Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(SHOW_TIME,time)
        editor.apply()
    }

    fun setShowed() {
        setShowed(System.currentTimeMillis())
    }

    fun setNeverShowAgain() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(DONT_SHOW,true)
        editor.apply()
    }

}