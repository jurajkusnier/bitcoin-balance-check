package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
object CurrencyModule {

    @Provides
    fun provideExchangeRatesRepository(): ExchangeRatesRepository = ExchangeRatesRepository()

    @Provides
    fun provideCurrencyListItemGenerator(): CurrencyListItemGenerator = CurrencyListItemGenerator()
}