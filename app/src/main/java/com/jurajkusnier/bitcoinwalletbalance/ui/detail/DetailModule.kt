package com.jurajkusnier.bitcoinwalletbalance.ui.detail

import android.content.Context
import androidx.room.Room
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabase
import com.jurajkusnier.bitcoinwalletbalance.data.db.AppDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DetailModule {

    private const val DATABASE_NAME = "db.sql"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAppDatabaseDao(appDatabase: AppDatabase): AppDatabaseDao = appDatabase.getDao()

    @Provides
    fun provideBarcodeEncoder() = BarcodeEncoder()
}