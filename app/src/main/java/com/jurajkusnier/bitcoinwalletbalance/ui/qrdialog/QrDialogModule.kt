package com.jurajkusnier.bitcoinwalletbalance.ui.qrdialog

import androidx.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface QrDialogModule {
    @Binds
    @IntoMap
    @ViewModelKey(QrViewModel::class)
    abstract fun bindViewModel( viewModel: QrViewModel): ViewModel

    @ContributesAndroidInjector
    abstract fun contributeDialog(): QrDialog
}