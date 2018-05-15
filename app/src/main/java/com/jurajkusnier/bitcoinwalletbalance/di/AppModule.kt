package com.jurajkusnier.bitcoinwalletbalance.di

import android.app.Application
import android.content.Context
import com.jurajkusnier.bitcoinwalletbalance.data.api.NetworkModule
import dagger.Binds
import dagger.Module

@Module(includes = [NetworkModule::class])
abstract class AppModule {

    @Binds
    abstract fun provideContext(application: Application): Context
}