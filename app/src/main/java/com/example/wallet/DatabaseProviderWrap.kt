package com.example.wallet

import android.content.Context
import androidx.room.Room

object DatabaseProviderWrap {

    const val VERSION = DatabaseProvider.VERSION
    private lateinit var provider: DatabaseProvider


    val cardDao: CardDao get() = provider.dao

    fun closeDao() = provider.close()


    fun createDao(context: Context) {
        provider = Room.databaseBuilder(context, DatabaseProvider::class.java, "cards")
            .allowMainThreadQueries()
            .build()
    }
}