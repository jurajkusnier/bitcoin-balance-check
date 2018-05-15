package com.jurajkusnier.bitcoinwalletbalance.ui.jobs

import android.arch.lifecycle.ViewModel
import com.jurajkusnier.bitcoinwalletbalance.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class JobsFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(JobsViewModel::class)
    abstract fun bindJobsViewModel( viewModel: JobsViewModel): ViewModel

    @ContributesAndroidInjector
    abstract fun contributeJobsFragment(): JobsFragment
}