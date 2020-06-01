package com.jurajkusnier.bitcoinwalletbalance.ui

import android.os.Bundle
import android.view.MenuItem
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.data.db.RateMePrefs
import com.jurajkusnier.bitcoinwalletbalance.ui.main.ParentFragment
import com.jurajkusnier.bitcoinwalletbalance.ui.ratenowdialog.RateNowDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.settings.SettingsDialog
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var rateMePrefs: RateMePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ParentFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rateMePrefs.isTimeToShow()) {
            RateNowDialog().show(supportFragmentManager, RateNowDialog.TAG)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            R.id.menu_settings -> {
                SettingsDialog().show(supportFragmentManager, SettingsDialog.TAG)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}