package com.jurajkusnier.bitcoinwalletbalance.di

import android.arch.lifecycle.ViewModelProvider
import com.jurajkusnier.bitcoinwalletbalance.ui.MainActivity
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.favourite.FavouriteFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.history.HistoryFragmentModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class MainActivityModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @ContributesAndroidInjector(modules = [
        DetailFragmentModule::class,
        HistoryFragmentModule::class,
        FavouriteFragmentModule::class])
    internal abstract fun contributeMainActivity(): MainActivity
}