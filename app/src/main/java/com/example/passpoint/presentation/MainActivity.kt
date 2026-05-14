package com.example.passpoint.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.utils.AndroidNetworkMonitor
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

    private lateinit var networkMonitor: AndroidNetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        networkMonitor = AndroidNetworkMonitor(this)

        setContent {
            val isOnline by networkMonitor.isConnected.collectAsState(initial = false)
            val theme by themeManager.themeFlow.collectAsState(initial = AppTheme.SYSTEM)

            PassPointTheme(selectedTheme = theme, content = {
                UserRepository.init(this)
                Navigation(isOnline = isOnline)
            })
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        intent?.let {
            val openScreen = it.getStringExtra("open_screen")
            when (openScreen) {
                "login" -> {
                    // Перенаправьте пользователя на экран авторизации
                }
                "events" -> { /* переход к событиям */ }
                "courses" -> { /* переход к курсам */ }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.unregister()
    }
}