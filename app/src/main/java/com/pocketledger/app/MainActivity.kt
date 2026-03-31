package com.pocketledger.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pocketledger.app.ui.PocketLedgerApp
import com.pocketledger.app.theme.PocketLedgerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketLedgerTheme {
                PocketLedgerApp()
            }
        }
    }
}


