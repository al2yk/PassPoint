package com.example.passpoint.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.navigation.Navigation
import com.example.passpoint.presentation.theme.PassPointTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PassPointTheme {
                val context = LocalContext.current
                UserRepository.init(context)
                Navigation()
            }
        }
    }
}