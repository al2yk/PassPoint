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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.example.passpoint.presentation.screens.main.events.EventsView
import com.example.passpoint.presentation.screens.main.events.PastEventsView
import com.example.passpoint.presentation.screens.main.news.NewsDetailView
import com.example.passpoint.presentation.screens.main.news.NewsView
import com.example.passpoint.presentation.screens.nointernet.NoInternetView
import com.example.passpoint.presentation.screens.onboarding.OnboardingView
import com.example.passpoint.presentation.screens.splash.SplashView
import com.example.passpoint.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(isOnline: Boolean) {
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Экраны с верхним и нижним баром
    val screensWithBottomBar = setOf(
        NavigationRoutes.MAIN,
        NavigationRoutes.PROFILE,
        NavigationRoutes.MINE
    )

    // Экраны только с верхним баром (без нижнего) — точное совпадение
    val screensWithTopBarOnly = setOf(
        NavigationRoutes.NEWS,
        NavigationRoutes.EVENTS,
        NavigationRoutes.PAST_EVENTS
    )

    // Проверка, является ли текущий маршрут детальным экраном новости
    val isNewsDetail = currentRoute?.startsWith(NavigationRoutes.NEWS_DETAIL) == true

    val showBottomBar = isOnline && currentRoute in screensWithBottomBar
    val showTopBar = isOnline && (
            currentRoute in screensWithBottomBar ||
                    currentRoute in screensWithTopBarOnly ||
                    isNewsDetail
            )

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(getTitleForRoute(currentRoute, isNewsDetail), color = White) },
                    navigationIcon = {
                        // Показываем кнопку "Назад", если это не главные экраны и не экран входа
                        if (currentRoute != NavigationRoutes.MAIN &&
                            currentRoute != NavigationRoutes.PROFILE &&
                            currentRoute != NavigationRoutes.MINE &&
                            !isNewsDetail
                        ) {
                            // Для NEWS_DETAIL кнопка назад всё равно нужна, поэтому условие исправлено
                        }
                        // Нужно показывать кнопку назад на всех экранах, кроме MAIN, PROFILE, MINE
                        if (currentRoute != NavigationRoutes.MAIN &&
                            currentRoute != NavigationRoutes.PROFILE &&
                            currentRoute != NavigationRoutes.MINE
                        ) {
                            IconButton(onClick = { controller.navigateUp() }) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back_24dp),
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
            if (showBottomBar) {
                BottomMenu(
                    selectedScreen = mapRouteToMainEnum(currentRoute),
                    onItemSelected = { selectedEnum ->
                        val route = mapMainEnumToRoute(selectedEnum)
                        controller.navigate(route) {
                            popUpTo(controller.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (isOnline) {
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
                composable(NavigationRoutes.NEWS) {
                    NewsView(controller, innerPadding)
                }
                composable(
                    route = NavigationRoutes.NEWS_DETAIL + "?newsId={newsId}",
                    arguments = listOf(navArgument("newsId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val newsId = backStackEntry.arguments?.getInt("newsId") ?: -1
                    NewsDetailView(innerPadding)
                }
                composable(NavigationRoutes.EVENTS) {
                    EventsView(controller = controller, innerPadding = innerPadding)
                }
                composable(NavigationRoutes.PAST_EVENTS) {
                    PastEventsView(controller = controller, innerPadding = innerPadding)
                }
            }
        } else {
            NoInternetView()
        }
    }
}

private fun getTitleForRoute(route: String?, isNewsDetail: Boolean): String = when {
    route == NavigationRoutes.MAIN -> "PassPoint"
    route == NavigationRoutes.PROFILE -> "Профиль"
    route == NavigationRoutes.MINE -> "Моё"
    route == NavigationRoutes.NEWS -> "Новости"
    isNewsDetail -> "Новость"
    route == NavigationRoutes.EVENTS -> "Мероприятия"
    route == NavigationRoutes.PAST_EVENTS -> "Прошедшие мероприятия"
    else -> "PassPoint"
}

private fun mapRouteToMainEnum(route: String?): MainEnum = when (route) {
    NavigationRoutes.PROFILE -> MainEnum.PROFILE
    NavigationRoutes.MAIN -> MainEnum.HOME
    NavigationRoutes.MINE -> MainEnum.SETTINGS
    else -> MainEnum.HOME
}

private fun mapMainEnumToRoute(enum: MainEnum): String = when (enum) {
    MainEnum.PROFILE -> NavigationRoutes.PROFILE
    MainEnum.HOME -> NavigationRoutes.MAIN
    MainEnum.SETTINGS -> NavigationRoutes.MINE
}