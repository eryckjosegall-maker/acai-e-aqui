package com.example.acaieaqui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.acaieaqui.navigation.AppNavHost
import com.example.acaieaqui.ui.theme.AcaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcaiTheme {
                AppNavHost()
            }
        }
    }
}
