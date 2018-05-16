package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.arch.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class DetailFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindJobsViewModel( viewModel: DetailViewModel): ViewModel

    @ContributesAndroidInjector
    abstract fun contributeJobsFragment(): DetailFragment
}