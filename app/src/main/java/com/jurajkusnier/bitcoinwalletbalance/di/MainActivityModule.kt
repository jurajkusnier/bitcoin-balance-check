package com.jurajkusnier.bitcoinwalletbalance.di

import android.arch.lifecycle.ViewModelProvider
import com.jurajkusnier.bitcoinwalletbalance.ui.MainActivity
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.jobs.JobsFragmentModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class MainActivityModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @ContributesAndroidInjector(modules = [DetailFragmentModule::class])
    internal abstract fun contributeMainActivity(): MainActivity
}