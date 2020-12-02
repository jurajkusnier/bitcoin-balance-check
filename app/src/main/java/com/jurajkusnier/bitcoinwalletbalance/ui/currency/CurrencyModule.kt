package com.jurajkusnier.bitcoinwalletbalance.ui.currency

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.jurajkusnier.bitcoinwalletbalance.api.BlockchainApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object CurrencyModule {

    private const val baseUrl = "https://blockchain.info/"

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofitForBlockchainApi(moshi: Moshi, httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
            .client(httpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideBlockchainApiService(retrofit: Retrofit): BlockchainApiService =
        retrofit.create(BlockchainApiService::class.java)

    @Provides
    @Singleton
    fun provideCurrencyListItemGenerator(): CurrencyListItemGenerator = CurrencyListItemGenerator()

}