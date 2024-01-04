package com.example.wallet

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class CardItem @JvmOverloads  constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val cardNumber: String,
    val fullname: String,
    val expireDate: String,
    val cvc: Int,
    val bank: String,
    val phoneNumber: String,
    @Ignore
    val compact:Boolean=true
)
