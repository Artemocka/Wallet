package com.example.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wallet.databinding.FragmentListBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class ListFragment : Fragment(), MyItemRecyclerViewAdapter.CardItemListener {

    private var columnCount = 1
    private val adapter = com.example.wallet.MyItemRecyclerViewAdapter()
    private var bg: ColorStateList? = null
    private lateinit var textColor: ColorStateList

    private lateinit var binding: FragmentListBinding

    val filter = MutableStateFlow<String>("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.listener = this
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        lifecycleScope.launch {
            combine(
                DatabaseProviderWrap.cardDao.getAll(), filter
            ) { list, query ->
                if (query.isEmpty()) {
                    Log.e("", "combine if $query")
                    list
                } else {
                    Log.e("", "combine else $query")

                    list.filter {
                        it.phoneNumber.contains(
                            query,
                            true
                        ) || it.fullname.contains(query, true) || it.bank.contains(query, true)
                    }
                }
            }.collect {
                Log.e("", "${it.size}")
                adapter.submitList(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentListBinding.inflate(inflater, container, false)


        binding.toolbar.setOnMenuItemClickListener {
//            binding.toolbar.isVisible = false
            binding.searchBar.root.isVisible = true
            binding.toolbar.menu.findItem(R.id.app_bar_search).isVisible = false
            binding.searchBar.searchEditText.requestFocus()
            binding.searchBar.searchIcon.isVisible = false


            val imm =
                binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(binding.searchBar.searchEditText, 0)





            true
        }
        binding.searchBar.cancelButton.setOnClickListener {
            binding.toolbar.menu.findItem(R.id.app_bar_search).isVisible = true
            binding.searchBar.searchEditText.clearFocus()
            binding.searchBar.searchEditText.text.clear()
            binding.searchBar.root.isVisible = false
            binding.searchBar.searchIcon.isVisible = true
            hideKeyboard()

        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->


            binding.toolbar.updatePaddingRelative(top = insets.getInsets(Type.statusBars() or Type.displayCutout()).top)

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
            binding.list.updatePaddingRelative(
                bottom = binding.list.paddingTop + insets.getInsets(
                    Type.navigationBars()
                ).bottom
            )


            insets
        }
        binding.searchBar.searchEditText.addTextChangedListener {
//            Log.e("","combine if ${it.toString()}")

            lifecycleScope.launch {
                filter.emit(it.toString())
            }
        }

        binding.fab.setOnClickListener {
            createBottomSheet()
        }
        setTextWatchers()


        val behavior = binding.bottomSheet.getBehavior()
        val select_bottom_sheet_behavior = binding.included.standardBottomSheet.getBehavior()


        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        select_bottom_sheet_behavior.state = BottomSheetBehavior.STATE_HIDDEN

        select_bottom_sheet_behavior.addBottomSheetCallback(BottomSheetCallbackImpl(binding.included.selectBottomSheetUnderlay))

        behavior.addBottomSheetCallback(BottomSheetCallbackImpl(binding.bottomSheetUnderlay))


        binding.list.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }
        binding.list.adapter = adapter






        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            com.example.wallet.ListFragment().apply {
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
        showSelectBottomSheet(item)

    }


    private inner class BottomSheetCallbackImpl(private val underlay: View) :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            Log.e("", "onStateChanged\t$newState")
            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                    hideKeyboard()
                    underlay.setOnClickListener(null)
                    underlay.isClickable = false
                }

                BottomSheetBehavior.STATE_DRAGGING -> {
                    hideKeyboard()
                }

                else -> {
                    underlay.setOnClickListener {
                        val behavior = bottomSheet.getBehavior()

                        behavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

    }

    private fun createBottomSheet() {
        val behavior = binding.bottomSheet.getBehavior()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.run {
            cardNumber.text?.clear()
            cvc.text?.clear()
            expireDate1.text?.clear()
            expireDate2.text?.clear()
            fullname.text?.clear()
            bankName.text?.clear()
            phoneNumber.text?.clear()
        }

        binding.save.setOnClickListener {
            hideKeyboard()

            lifecycleScope.launch {
                delay(100)
                binding.run {
                    val newItem = com.example.wallet.CardItem(
                        id = 0,
                        cardNumber = cardNumber.text.toString(),
                        fullname = fullname.text.toString(),
                        expireDate = "${expireDate1.text.toString()}/${expireDate2.text.toString()}",
                        cvc = cvc.text.toString().toInt(),
                        bank = bankName.text.toString(),
                        phoneNumber = phoneNumber.text.toString()

                    )
                    DatabaseProviderWrap.cardDao.insert(newItem)

                }
                binding.bankName.clearFocus()
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

        }
        binding.cardNumber.requestFocus()


    }

    private fun copyAllFields(item: CardItem) {
        val clipboard =
            this.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val cardText =
            "${item.cardNumber}\n${item.expireDate}\t${item.cvc}\n${item.fullname}\n${item.bank}\n${item.phoneNumber}"
        val clip: ClipData = ClipData.newPlainText("Card", cardText)
        clipboard.setPrimaryClip(clip)
    }

    private fun editBottomSheet(item: CardItem) {

        val behavior = binding.bottomSheet.getBehavior()

        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        binding.run {
            cardNumber.setText(item.cardNumber)
            cvc.setText(item.cvc.toString())
            expireDate1.setText(item.expireDate.subSequence(0..1))
            expireDate2.setText(item.expireDate.subSequence(3..4))
            fullname.setText(item.fullname)
            bankName.setText(item.bank)
            phoneNumber.setText(item.phoneNumber)




            binding.save.setOnClickListener {


                val newItem = item.copy(
                    cardNumber = cardNumber.text.toString().trim(),
                    fullname = fullname.text.toString().trim(),
                    expireDate = "${expireDate1.text}/${expireDate2.text}".trim(),
                    cvc = cvc.text.toString().trim().toInt(),
                    bank = bankName.text.toString().trim(),
                    phoneNumber = phoneNumber.text.toString().trim()
                )

                DatabaseProviderWrap.cardDao.update(newItem)


                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                hideKeyboard()


            }

        }
        binding.cardNumber.requestFocus()

    }


    private fun View.getBehavior(): BottomSheetBehavior<View> {
        val params = layoutParams as CoordinatorLayout.LayoutParams

        return params.behavior as BottomSheetBehavior
    }

    private fun hideKeyboard() {
        val imm =
            binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showSelectBottomSheet(item: CardItem) {
        val behavior = binding.included.standardBottomSheet.getBehavior()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.included.copy.icon.setImageResource(R.drawable.ic_copy)
        binding.included.copy.title.setText(R.string.copy)

        binding.included.copy.root.setOnClickListener {
            copyAllFields(item)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.included.edit.icon.setImageResource(R.drawable.ic_edit)
        binding.included.edit.title.setText(R.string.edit)
        binding.included.edit.root.setOnClickListener {
            editBottomSheet(item)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.included.remove.icon.setImageResource(R.drawable.ic_delete)
        binding.included.remove.icon.imageTintList = ColorStateList.valueOf(Color.RED)
        binding.included.remove.title.setTextColor(Color.RED)
        binding.included.remove.title.setText(R.string.remove)
        binding.included.remove.root.setOnClickListener {


            MaterialAlertDialogBuilder(it.context)
                .setTitle(getString(R.string.alert_title))
                .setMessage(getString(R.string.alert_description))
                .setPositiveButton(getString(R.string.alert_confirm)) { dialog, _ ->
//                    dialog.cancel()
                    DatabaseProviderWrap.cardDao.delete(item)
                }.setNegativeButton(getString(R.string.alert_cancel)) { dialog, _ ->
//                    dialog.cancel()
                }.create().show()
//            DatabaseProviderWrap.cardDao.delete(item)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setTextWatchers() {
        binding.expireDate1.addTextChangedListener {
            if (it?.length == 2)
                binding.expireDate2.requestFocus()
        }
        binding.expireDate2.addTextChangedListener {
            if (it?.length == 2)
                binding.cvc.requestFocus()
        }

        binding.cvc.addTextChangedListener {
            if (it?.length == 3)
                binding.fullname.requestFocus()
        }
        binding.cardNumber.addTextChangedListener {
            if (it?.length == 16)
                binding.expireDate1.requestFocus()
        }

    }

}

