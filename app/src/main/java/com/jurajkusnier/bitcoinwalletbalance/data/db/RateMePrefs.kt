package com.jurajkusnier.bitcoinwalletbalance.data.db

import android.app.Application
import android.preference.PreferenceManager
import javax.inject.Inject

class RateMePrefs @Inject constructor(context: Application) {

    val TAG = RateMePrefs::class.java.simpleName

    private val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val DONT_SHOW = "rate_me_not"
    private val SHOW_TIME = "rate_me_time"
    private val TIME = 1000 * 60 * 60 * 48 //2 days in ms

    fun isTimeToShow():Boolean
    {
        if (mSharedPreferences.getBoolean(DONT_SHOW,false)) return false

        val lastTime = mSharedPreferences.getLong(SHOW_TIME,0)
        val timeDiff = System.currentTimeMillis() - lastTime
        return (timeDiff > TIME && lastTime > 0)
    }

    fun setShowed() {
        val editor = mSharedPreferences.edit()
        editor.putLong(SHOW_TIME,System.currentTimeMillis())
        editor.apply()
    }

    fun setNeverShowAgain() {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(DONT_SHOW,true)
        editor.apply()
    }

}