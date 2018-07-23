package com.jurajkusnier.bitcoinwalletbalance.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.jurajkusnier.bitcoinwalletbalance.ui.MainActivity
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialogModule
import com.jurajkusnier.bitcoinwalletbalance.ui.favourite.FavouriteFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.history.HistoryFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.main.MainActivityViewModel
import com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog.QrDialogModule
import com.jurajkusnier.bitcoinwalletbalance.ui.settings.SettingDialogModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindViewModel( viewModel: MainActivityViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @ContributesAndroidInjector(modules = [
        DetailFragmentModule::class,
        HistoryFragmentModule::class,
        FavouriteFragmentModule::class,
        SettingDialogModule::class,
        QrDialogModule::class,
        EditDialogModule::class])

    internal abstract fun contributeMainActivity(): MainActivity
}