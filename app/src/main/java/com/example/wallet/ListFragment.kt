package com.example.wallet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.getSystemService
import com.example.wallet.databinding.FragmentListBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * A fragment representing a list of Items.
 */
class ListFragment : Fragment(), MyItemRecyclerViewAdapter.CardItemListener {

    private var columnCount = 1
    val adapter = MyItemRecyclerViewAdapter()

    private lateinit var binding: FragmentListBinding
    private var list = listOf(
        CardItem("1234567890123456", "Petr Petrov Petrovich", "12/23", 123, "Alpha"),
        CardItem("0123456789012345", "Ivanov Ivan Ivanovich", "12/23", 111, "Tinkoff")

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.listener = this
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentListBinding.inflate(inflater, container, false)

        binding.list.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }
        binding.list.adapter = adapter
        adapter.submitList(list)



        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onClick(item: CardItem) {
        adapter.currentList.toMutableList().run {
            val index = indexOf(item)
            this[index] = item.copy(compact = !item.compact)
            adapter.submitList(this)
        }
    }

    override fun onLongClick(item: CardItem) {
        val params = binding.bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as BottomSheetBehavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

//        binding.bottomSheet.
        binding.run {
            cardNumber.setText(item.cardNumber)
            cvc.setText(item.cvc.toString())
            expireDate.setText(item.expireDate)
            fullname.setText(item.fullname)
            bankName.setText(item.bank)

            binding.save.setOnClickListener {
                adapter.currentList.toMutableList().run {
                    val index = indexOf(item)
                    this[index] = item.copy(
                        cardNumber = cardNumber.text.toString(),
                        fullname = fullname.text.toString(),
                        expireDate = expireDate.text.toString(),
                        cvc = cvc.text.toString().toInt(),
                        bank = bankName.text.toString(),
                        )
                    adapter.submitList(this)
                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
//                    behavior.
                    val imm = root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(root.windowToken, 0)

                }

            }
        }
    }
}