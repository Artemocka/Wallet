package com.example.wallet

data class CardItem(
//    val id: Int,
    val cardNumber: String,
    val fullname: String,
    val expireDate: String,
    val cvc: Int,
    val bank: String,
    val compact:Boolean=true,
)
