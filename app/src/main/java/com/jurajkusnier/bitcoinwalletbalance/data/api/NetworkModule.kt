package com.jurajkusnier.bitcoinwalletbalance.data.api

import android.content.Context
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.jurajkusnier.bitcoinwalletbalance.BuildConfig
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {
    private val TAG = NetworkModule::class.simpleName
    private val blockchainServer = "https://blockchain.info/"

    companion object {
        private const val offlineInterceptorName = "offlineInterceptor"
        private const val delayInterceptorName = "delayInterceptor"
        private const val blockchainUrlName = "blockchainUrl"
        private const val blockchainName = "blockchain"
    }

    @Provides
    @Singleton
    fun provideNetworkInfo(context: Context): NetworkInfo {
        return NetworkInfo(context)
    }

    @Provides
    @Singleton
    @Named(offlineInterceptorName)
    fun provideOfflineCheckInterceptor(networkInfo: NetworkInfo): Interceptor {
        return Interceptor { chain ->
            if (networkInfo.isNetworkAvailable()) {
                chain.proceed(chain.request())
            } else {
                throw OfflineException()
            }
        }
    }

    private fun getInterceptorLevel(): HttpLoggingInterceptor.Level {
        return if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BASIC
        else
            HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    fun provideHttpLoginInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val level = getInterceptorLevel()
        httpLoggingInterceptor.level = level
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    @Named(delayInterceptorName)
    fun provideDelayInterceptor(): Interceptor {
        return Interceptor { chain ->
            try {
                Thread.sleep(BuildConfig.NETWORK_DELAY)
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }

            try {
                chain.proceed(chain.request())
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
                throw e
            }
        }
    }

    @Provides
    @Singleton
    @Named(blockchainUrlName)
    fun provideBlockchainUrl() = blockchainServer

    @Provides
    @Singleton
    fun provideBlockchainApiService(@Named(blockchainName) retrofit: Retrofit): BlockchainApiService = retrofit.create(BlockchainApiService::class.java)

    @Provides
    @Singleton
    @Named(blockchainName)
    fun provideRetrofitForBlockchainApi(@Named(blockchainUrlName) baseUrl: String, moshi: Moshi, httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(httpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideHttpClient(loggingInterceptor: HttpLoggingInterceptor, @Named(delayInterceptorName) delayInterceptor: Interceptor, @Named(offlineInterceptorName) offlineCheckInterceptor: Interceptor): OkHttpClient {

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(offlineCheckInterceptor)

        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(delayInterceptor)
        }

        return okHttpClient.build()
    }

    @Provides
    @Singleton
    fun provideMoshi() = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()
}