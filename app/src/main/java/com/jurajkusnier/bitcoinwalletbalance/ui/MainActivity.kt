package com.jurajkusnier.bitcoinwalletbalance.ui

import android.os.Bundle
import com.jurajkusnier.bitcoinwalletbalance.R
import com.jurajkusnier.bitcoinwalletbalance.ui.jobs.JobsFragment
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity

class MainActivity: DaggerAppCompatActivity() {

    val TAG = MainActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        //DI activity injection first
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, JobsFragment.newInstance())
                    .commitNow()
        }
    }

}