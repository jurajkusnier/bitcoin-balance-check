package com.jurajkusnier.bitcoinwalletbalance.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jurajkusnier.bitcoinwalletbalance.ui.MainActivity
import com.jurajkusnier.bitcoinwalletbalance.ui.detail.DetailFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.edit.EditDialogModule
import com.jurajkusnier.bitcoinwalletbalance.ui.favourite.FavouriteFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.history.HistoryFragmentModule
import com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog.QrDialogModule
import com.jurajkusnier.bitcoinwalletbalance.ui.ratenowdialog.RateNowDialog
import com.jurajkusnier.bitcoinwalletbalance.ui.ratenowdialog.RateNowDialogModule
import com.jurajkusnier.bitcoinwalletbalance.ui.settings.SettingDialogModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class MainActivityModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @ContributesAndroidInjector(modules = [
        DetailFragmentModule::class,
        HistoryFragmentModule::class,
        FavouriteFragmentModule::class,
        SettingDialogModule::class,
        QrDialogModule::class,
        RateNowDialogModule::class,
        EditDialogModule::class])

    internal abstract fun contributeMainActivity(): MainActivity
}