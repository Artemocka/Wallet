package com.example.wallet

import androidx.recyclerview.widget.DiffUtil

class CardDiffCallback:DiffUtil.ItemCallback<CardItem>() {
    override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
        return  oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
        return  oldItem==newItem

    }

}