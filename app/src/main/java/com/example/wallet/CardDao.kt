package com.example.wallet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM CardItem WHERE id = :id")
    fun getById(id: Long): CardItem

    @Query("SELECT * FROM CardItem ORDER BY id")
    fun getAll(): Flow<List<CardItem>>

    @Query("SELECT COUNT(*) FROM CardItem")
    fun count(): Int

    @Query("DELETE FROM CardItem")
    fun clear()

    @Insert
    fun insert(item: CardItem): Long

    @Update
    fun update(item: CardItem)

    @Delete
    fun delete(item: CardItem)
}