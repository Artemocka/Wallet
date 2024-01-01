package com.example.wallet

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import com.example.wallet.placeholder.PlaceholderContent.PlaceholderItem
import com.example.wallet.databinding.ItemCardBinding



class MyItemRecyclerViewAdapter(
) : ListAdapter<CardItem, MyItemRecyclerViewAdapter.ViewHolder>(CardDiffCallback()) {

    var listener: CardItemListener? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].cardNumber.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.e("", "onCreateViewHolder")

        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = ViewHolder(
            binding
        )

        binding.root.setOnClickListener {
            listener?.onClick(currentList[viewHolder.bindingAdapterPosition])
        }
        binding.root.setOnLongClickListener {
            listener?.onLongClick(currentList[viewHolder.bindingAdapterPosition])

            true
        }

        return viewHolder

    }

    override fun onBindViewHolder(holder: MyItemRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = currentList[position]


        Log.e("", "onBindViewHolder   $position")
        holder.bind(item)

    }


    inner class ViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardItem) {
            binding.run {
                cardNumber.text = item.cardNumber
                fullname.text = item.fullname
                expireDate.text = item.expireDate
                cvc.text = item.cvc.toString()
                bankName.text = item.bank

                //comact view
                compactCardNumber.text = item.cardNumber
                compactFullname.text = item.fullname

                fullview.isVisible = !item.compact
                compactView.isVisible = item.compact

            }
        }


    }

    interface CardItemListener {
        fun onClick(item: CardItem)
        fun onLongClick(item:CardItem)
    }

}

