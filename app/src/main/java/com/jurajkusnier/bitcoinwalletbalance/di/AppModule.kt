package com.jurajkusnier.bitcoinwalletbalance.di

import android.app.Application
import android.content.Context
import com.jurajkusnier.bitcoinwalletbalance.data.api.NetworkModule
import com.jurajkusnier.bitcoinwalletbalance.data.db.DatabaseModule
import com.jurajkusnier.bitcoinwalletbalance.data.db.PrefsModule
import dagger.Binds
import dagger.Module

@Module(includes = [NetworkModule::class, DatabaseModule::class, PrefsModule::class])
abstract class AppModule {

    @Binds
    abstract fun provideContext(application: Application): Context
}