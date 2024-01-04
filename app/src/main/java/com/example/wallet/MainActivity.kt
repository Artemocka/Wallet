package com.example.wallet

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updateMarginsRelative
import androidx.core.view.updatePaddingRelative
import androidx.core.widget.addTextChangedListener
import com.example.wallet.databinding.ActivityMainBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.utilities.DynamicColor

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseProviderWrap.createDao(this)

        binding = ActivityMainBinding.inflate(layoutInflater)



//        binding.searchView.setOnMenuItemClickListener{
//            binding.toolbar.isVisible = true
//            binding.searchView.isVisible = false
//            true
//        }



        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)

        WindowCompat.setDecorFitsSystemWindows(window, false)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}