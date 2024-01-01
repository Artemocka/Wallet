package com.example.wallet

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CardItem::class,
    ],
    version = DatabaseProvider.VERSION
)
abstract class DatabaseProvider : RoomDatabase() {
    abstract val dao: CardDao

    companion object {
        const val VERSION = 1
    }
}