package com.jurajkusnier.bitcoinwalletbalance.data.db

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    val DATABASE_NAME = "db.sql"

    @Provides
    @Singleton
    fun provideAppDatabase(application:Application):AppDatabase{
        return Room.databaseBuilder(application, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}