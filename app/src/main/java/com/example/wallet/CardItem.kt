package com.example.wallet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val cardNumber: String,
    val fullname: String,
    val expireDate: String,
    val cvc: Int,
    val bank: String,
    val compact:Boolean=true,
    val phoneNumber: String,
)
