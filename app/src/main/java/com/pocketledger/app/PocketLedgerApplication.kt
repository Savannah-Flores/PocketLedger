package com.pocketledger.app

import android.app.Application
import com.pocketledger.app.data.AppContainer
import com.pocketledger.app.data.DefaultAppContainer

class PocketLedgerApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
