package com.example.passpoint.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.navigation.Navigation
import com.example.passpoint.presentation.theme.AppTheme
import com.example.passpoint.presentation.theme.PassPointTheme
import com.example.passpoint.presentation.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by themeManager.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
            PassPointTheme(selectedTheme = theme) {
                val context = LocalContext.current
                UserRepository.init(context)
                Navigation()
            }
        }
    }
}