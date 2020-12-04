package com.jurajkusnier.bitcoinwalletbalance

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}