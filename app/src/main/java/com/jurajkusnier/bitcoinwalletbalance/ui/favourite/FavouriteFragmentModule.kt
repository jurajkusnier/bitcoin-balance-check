package com.jurajkusnier.bitcoinwalletbalance.ui.favourite

import androidx.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class FavouriteFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(FavouriteViewModel::class)
    abstract fun bindViewModel( viewModel: FavouriteViewModel): ViewModel

    @ContributesAndroidInjector
    abstract fun contributeFragment(): FavouriteFragment
}