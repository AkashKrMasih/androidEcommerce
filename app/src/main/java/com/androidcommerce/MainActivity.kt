package com.androidcommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.androidcommerce.ui.navigation.CommerceNavHost
import com.androidcommerce.ui.theme.CommerceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as CommerceApplication
        setContent {
            CommerceTheme {
                CommerceNavHost(application = app)
            }
        }
    }
}
