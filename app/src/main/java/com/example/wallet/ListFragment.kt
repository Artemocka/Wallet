package com.example.wallet

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Insets
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import androidx.lifecycle.lifecycleScope
import com.example.wallet.databinding.FragmentListBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class ListFragment : Fragment(), MyItemRecyclerViewAdapter.CardItemListener {

    private var columnCount = 1
    val adapter = MyItemRecyclerViewAdapter()
    private var bg: ColorStateList? = null
    private lateinit var textColor: ColorStateList

    private lateinit var binding: FragmentListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.listener = this
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        lifecycleScope.launch {
            DatabaseProviderWrap.cardDao.getAll().collect {
                adapter.submitList(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentListBinding.inflate(inflater, container, false)

        val params = binding.bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as BottomSheetBehavior
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.list.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }
        binding.list.adapter = adapter

        bg = binding.save.backgroundTintList!!
        textColor = binding.save.textColors
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->

            binding.fab.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = marginEnd + insets.getInsets(Type.navigationBars()).bottom
            }
            binding.bottomSheet.updatePaddingRelative(
                bottom = if (insets.getInsets(Type.ime()).bottom == 0) {
                    resources.getDimensionPixelSize(R.dimen.large)
                } else {
                    resources.getDimensionPixelSize(R.dimen.common)
                }
            )

            insets
        }



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
        reset()
        val params = binding.bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as BottomSheetBehavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        var job: Job? = null
//        binding.bottomSheet.
        binding.run {
            cardNumber.setText(item.cardNumber)
            cvc.setText(item.cvc.toString())
            expireDate.setText(item.expireDate)
            fullname.setText(item.fullname)
            bankName.setText(item.bank)
            phoneNumber.setText(item.phoneNumber)


            binding.delete.setOnClickListener {
                save.setText(R.string.confirm)
                save.backgroundTintList = ColorStateList.valueOf(Color.RED)
                save.setTextColor(Color.WHITE)
                job?.cancel()
                job = lifecycleScope.launch {
                    delay(2000)
                    reset()
                    job = null
                }


            }

            binding.save.setOnClickListener {

                if (job == null) {
                    val newItem = item.copy(
                        cardNumber = cardNumber.text.toString(),
                        fullname = fullname.text.toString(),
                        expireDate = expireDate.text.toString(),
                        cvc = cvc.text.toString().toInt(),
                        bank = bankName.text.toString(),
                        phoneNumber = phoneNumber.text.toString()
                    )

                    DatabaseProviderWrap.cardDao.update(newItem)


                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
                    val imm =
                        root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(root.windowToken, 0)
                } else {
                    DatabaseProviderWrap.cardDao.delete(item)
                }

            }
        }
    }

    private fun reset() {
        binding.save.run {
            backgroundTintList = bg
            setTextColor(textColor)
            setText(R.string.save)
        }
    }
}