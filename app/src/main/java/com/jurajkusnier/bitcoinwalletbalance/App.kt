package com.jurajkusnier.bitcoinwalletbalance

import android.content.Context
import com.jurajkusnier.bitcoinwalletbalance.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import android.support.multidex.MultiDex

class App: DaggerApplication() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val builder = DaggerAppComponent.builder().application(this).build()
        builder.inject(this)
        return builder
    }
}