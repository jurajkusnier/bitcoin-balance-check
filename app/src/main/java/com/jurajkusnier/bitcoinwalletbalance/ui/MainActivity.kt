package com.jurajkusnier.bitcoinwalletbalance.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import com.jurajkusnier.bitcoinwalletbalance.BuildConfig
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelFactory
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainActivityViewModel
import com.jurajkusnier.bitcoinwalletbalance.ui.main.ParentFragment
import com.jurajkusnier.bitcoinwalletbalance.ui.settings.SettingsDialog
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity: DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    val TAG = MainActivity::class.simpleName

    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        //DI activity injection first
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ParentFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()

        if (viewModel.rateMePrefs.isTimeToShow()) {
            showRateMeDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            R.id.menu_settings -> {
                SettingsDialog().show(supportFragmentManager,SettingsDialog.TAG)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showRateMeDialog() {
        viewModel.rateMePrefs.setShowed()

        val alertRateMe = AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle)
        alertRateMe.setPositiveButton(getString(R.string.rate_now)) {
            _, _ ->
            viewModel.rateMePrefs.setNeverShowAgain()
            openPlayStore()
        }
        alertRateMe.setNegativeButton(getString(R.string.rate_never)) {
            _, _ -> viewModel.rateMePrefs.setNeverShowAgain()
        }
        alertRateMe.setTitle(getString(R.string.rate_title))
        alertRateMe.setMessage(getString(R.string.rate_text))
        alertRateMe.create().show()
    }

    private fun openPlayStore() = try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")))
    } catch (_: android.content.ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
    }

}