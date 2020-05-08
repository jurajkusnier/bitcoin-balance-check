package com.jurajkusnier.bitcoinwalletbalance.ui.ratenowdialog

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface RateNowDialogModule {

    @ContributesAndroidInjector
    abstract fun contributeDialog(): RateNowDialog
}