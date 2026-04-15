package com.example.passpoint.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.passpoint.presentation.screens.authorization.SignInView
import com.example.passpoint.presentation.screens.onboarding.OnboardingView
import com.example.passpoint.presentation.screens.splash.SplashView
import com.example.passpoint.presentation.theme.Background

@Composable
fun Navigation() {
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (shouldNotShowTopBar(currentRoute)) {

            }
        }
    ) { innerPadding ->
        val innerPadding = innerPadding
        Background(modifier = Modifier.fillMaxSize()) {
            NavHost(startDestination = NavigationRoutes.SPLASH, navController = controller) {
                composable(NavigationRoutes.SPLASH) {
                    SplashView(controller)
                }
                composable(NavigationRoutes.ONBOARDING) {
                    OnboardingView(controller = controller)
                }
                composable(NavigationRoutes.SIGNIN) {
                    SignInView(controller)
                }
                composable(NavigationRoutes.SIGNUP) {

                }

            }
        }
    }
}

private fun shouldNotShowTopBar(currentRoute: String?): Boolean {
    val hiddenRoutes = listOf(
        NavigationRoutes.SPLASH,
        NavigationRoutes.SIGNIN,
        NavigationRoutes.SIGNUP,
    )
    return currentRoute != null && currentRoute !in hiddenRoutes
}