package com.example.wallet

import android.app.Application
import com.google.android.material.color.DynamicColors

class Wallet: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}