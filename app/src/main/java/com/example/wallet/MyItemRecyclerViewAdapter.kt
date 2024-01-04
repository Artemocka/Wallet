package com.example.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wallet.databinding.ItemCardBinding


class MyItemRecyclerViewAdapter(
) : ListAdapter<CardItem, MyItemRecyclerViewAdapter.ViewHolder>(CardDiffCallback()) {

    var listener: CardItemListener? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].id.toLong()
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
        binding.cardNumber.copyOnClick()
        binding.expireDate.copyOnClick()
        binding.cvc.copyOnClick()
        binding.fullname.copyOnClick()
        binding.bankName.copyOnClick()
        binding.phoneNumber.copyOnClick()

        return viewHolder

    }

    private fun TextView.copyOnClick() {
        setOnClickListener {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            val clip: ClipData = ClipData.newPlainText("Expire date", text)
            clipboard.setPrimaryClip(clip)
        }
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
                phoneNumber.text = item.phoneNumber

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
        fun onLongClick(item: CardItem)
    }

}

