package com.jurajkusnier.bitcoinwalletbalance

import androidx.appcompat.app.AppCompatActivity
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun openDetails(address: String) {
        DetailFragment.open(supportFragmentManager, address)
    }
}