package com.example.passpoint.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.passpoint.R
import com.example.passpoint.presentation.components.BottomMenu
import com.example.passpoint.presentation.components.MainEnum
import com.example.passpoint.presentation.screens.authorization.SignInView
import com.example.passpoint.presentation.screens.authorization.SignUpView
import com.example.passpoint.presentation.screens.authorization.changePassword.ChangePasswordView
import com.example.passpoint.presentation.screens.authorization.changePassword.otp.OTPVIew
import com.example.passpoint.presentation.screens.authorization.changePassword.setpassword.SetPasswordView
import com.example.passpoint.presentation.screens.main.MainView
import com.example.passpoint.presentation.screens.main.MineView
import com.example.passpoint.presentation.screens.main.ProfileView
import com.example.passpoint.presentation.screens.onboarding.OnboardingView
import com.example.passpoint.presentation.screens.splash.SplashView
import com.example.passpoint.presentation.theme.Background
import com.example.passpoint.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Определяем, нужно ли показывать TopBar и BottomBar
    val isMainScreen = currentRoute in setOf(
        NavigationRoutes.MAIN,
        NavigationRoutes.PROFILE,
        NavigationRoutes.MINE
    )

    Scaffold(
        topBar = {
            if (isMainScreen) {
                TopAppBar(
                    title = { Text(getTitleForRoute(currentRoute), color = White) },
                    navigationIcon = {
                        if (currentRoute != NavigationRoutes.MAIN && currentRoute != NavigationRoutes.PROFILE && currentRoute != NavigationRoutes.MINE) {
                            IconButton(onClick = { controller.navigateUp() }) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back_24dp), // добавьте свою иконку
                                    contentDescription = "Назад",
                                    tint = White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        },
        bottomBar = {
            if (isMainScreen) {
                BottomMenu(
                    selectedScreen = mapRouteToMainEnum(currentRoute),
                    onItemSelected = { selectedEnum ->
                        val route = mapMainEnumToRoute(selectedEnum)
                        controller.navigate(route) {
                            // Очищаем стек до стартового пункта назначения
                            popUpTo(controller.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Избегаем множественных копий одного и того же назначения
                            launchSingleTop = true
                            // Восстанавливаем состояние, если оно было сохранено
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Background(modifier = Modifier.fillMaxSize()) {
            NavHost(
                startDestination = NavigationRoutes.SPLASH,
                navController = controller,
                modifier = Modifier.fillMaxSize()
            ) {
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
                    SignUpView(controller)
                }
                composable(NavigationRoutes.CHANGEPASSWORD) {
                    ChangePasswordView(controller)
                }
                composable(NavigationRoutes.OTP) {
                    OTPVIew(controller)
                }
                composable(NavigationRoutes.SETPASSWORD) {
                    SetPasswordView(controller)
                }
                composable(NavigationRoutes.MAIN) {
                    MainView(controller, innerPadding)
                }
                composable(NavigationRoutes.MINE) {
                    MineView(controller, innerPadding)
                }
                composable(NavigationRoutes.PROFILE) {
                    ProfileView(controller, innerPadding)
                }
            }
        }
    }
}

private fun getTitleForRoute(route: String?): String = when (route) {
    NavigationRoutes.MAIN -> "PassPoint"
    NavigationRoutes.PROFILE -> "Профиль"
    NavigationRoutes.MINE -> "Моё"
    else -> "PassPoint"
}
private fun mapRouteToMainEnum(route: String?): MainEnum = when (route) {
    NavigationRoutes.PROFILE -> MainEnum.PROFILE
    NavigationRoutes.MAIN -> MainEnum.HOME
    NavigationRoutes.MINE -> MainEnum.SETTINGS
    else -> MainEnum.HOME
}

/** Преобразует элемент BottomMenu в маршрут для навигации */
private fun mapMainEnumToRoute(enum: MainEnum): String = when (enum) {
    MainEnum.PROFILE -> NavigationRoutes.PROFILE
    MainEnum.HOME -> NavigationRoutes.MAIN
    MainEnum.SETTINGS -> NavigationRoutes.MINE
}