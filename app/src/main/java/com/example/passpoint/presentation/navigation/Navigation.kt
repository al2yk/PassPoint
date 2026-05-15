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
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.components.AdminBottomMenu
import com.example.passpoint.presentation.components.BottomMenu
import com.example.passpoint.presentation.components.MainEnum
import com.example.passpoint.presentation.screens.authorization.SignInView
import com.example.passpoint.presentation.screens.authorization.SignUpView
import com.example.passpoint.presentation.screens.authorization.changePassword.ChangePasswordView
import com.example.passpoint.presentation.screens.authorization.changePassword.otp.OTPVIew
import com.example.passpoint.presentation.screens.authorization.changePassword.setpassword.SetPasswordView
import com.example.passpoint.presentation.screens.main.MainView
import com.example.passpoint.presentation.screens.main.MineView
import com.example.passpoint.presentation.screens.main.profile.ProfileView
import com.example.passpoint.presentation.screens.main.admin.UsersView
import com.example.passpoint.presentation.screens.main.course.CoursesView
import com.example.passpoint.presentation.screens.main.course.CreateCourseView
import com.example.passpoint.presentation.screens.main.course.PastCoursesView
import com.example.passpoint.presentation.screens.main.curator.CuratorCourseDetailView
import com.example.passpoint.presentation.screens.main.curator.CuratorPastCoursesView
import com.example.passpoint.presentation.screens.main.curator.CuratorUsersView
import com.example.passpoint.presentation.screens.main.events.CreateEventView
import com.example.passpoint.presentation.screens.main.events.EventsView
import com.example.passpoint.presentation.screens.main.events.PastEventsView
import com.example.passpoint.presentation.screens.main.news.CreateNewsView
import com.example.passpoint.presentation.screens.main.news.NewsDetailView
import com.example.passpoint.presentation.screens.main.news.NewsView
import com.example.passpoint.presentation.screens.main.profile.EditProfileView
import com.example.passpoint.presentation.screens.main.profile.certificates.CertificatesView
import com.example.passpoint.presentation.screens.nointernet.NoInternetView
import com.example.passpoint.presentation.screens.onboarding.OnboardingView
import com.example.passpoint.presentation.screens.qr.QrView
import com.example.passpoint.presentation.screens.splash.SplashView
import com.example.passpoint.presentation.ui.theme.White

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
        NavigationRoutes.MINE,
        NavigationRoutes.USERS
    )

    // Экраны только с верхним баром (без нижнего) — точное совпадение
    val screensWithTopBarOnly = setOf(
        NavigationRoutes.NEWS,
        NavigationRoutes.EVENTS,
        NavigationRoutes.PAST_EVENTS,
        NavigationRoutes.COURSES,
        NavigationRoutes.PAST_COURSES,
        NavigationRoutes.QR,
        NavigationRoutes.CREATE_NEWS,
        NavigationRoutes.CREATE_EVENT,
        NavigationRoutes.CREATE_COURSE,
        NavigationRoutes.EDIT_COURSE,
        NavigationRoutes.EDIT_EVENT,
        NavigationRoutes.EDIT_PROFILE,
        NavigationRoutes.CURATOR_PAST_COURSES,
        NavigationRoutes.CURATOR_COURSE_DETAIL,
        NavigationRoutes.CERTIFICATES
    )

    // Проверка, является ли текущий маршрут детальным экраном новости
    val isNewsDetail = currentRoute?.startsWith(NavigationRoutes.NEWS_DETAIL) == true

    val showBottomBar = isOnline && currentRoute in screensWithBottomBar
    val showTopBar = isOnline && (
            currentRoute in screensWithBottomBar ||
                    currentRoute in screensWithTopBarOnly ||
                    isNewsDetail
            )
    val isAdmin = UserRepository.role == 3
    val isCurator = UserRepository.role == 2
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
                            currentRoute != NavigationRoutes.MINE &&
                            currentRoute != NavigationRoutes.USERS
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
                if (isAdmin || isCurator) {
                    AdminBottomMenu(
                        selectedScreen = mapRouteToMainEnum(currentRoute, isAdmin,isCurator),
                        onItemSelected = { selectedEnum ->
                            val route = mapMainEnumToRoute(selectedEnum, isAdmin)
                            controller.navigate(route) {
                                popUpTo(controller.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                } else {
                    BottomMenu(
                        selectedScreen = mapRouteToMainEnum(currentRoute, isAdmin, isCurator),
                        onItemSelected = { selectedEnum ->
                            val route = mapMainEnumToRoute(selectedEnum, isAdmin)
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
                composable(NavigationRoutes.COURSES) {
                    CoursesView(controller = controller, innerPadding = innerPadding)
                }
                composable(NavigationRoutes.PAST_COURSES) {
                    PastCoursesView(controller = controller, innerPadding = innerPadding)
                }
                composable(
                    route = NavigationRoutes.QR,
                    arguments = listOf(navArgument("qrData") { type = NavType.StringType })
                ) { backStackEntry ->
                    val qrData = backStackEntry.arguments?.getString("qrData") ?: ""
                    QrView(qrData = qrData)
                }
                composable(NavigationRoutes.USERS) {
                    if (isCurator) CuratorUsersView(controller, innerPadding)
                    else UsersView(controller, innerPadding)
                }
                composable(NavigationRoutes.CREATE_COURSE) {
                    CreateCourseView(controller, innerPadding)
                }
                composable(NavigationRoutes.EDIT_COURSE) {
                    CreateCourseView(controller = controller, innerPadding = innerPadding)
                }
                composable(NavigationRoutes.CREATE_EVENT) {
                    CreateEventView(controller = controller, innerPadding = innerPadding)
                }
                composable(
                    route = NavigationRoutes.EDIT_EVENT,
                    arguments = listOf(navArgument("eventId") { type = NavType.IntType })
                ) {
                    CreateEventView(controller = controller, innerPadding = innerPadding)
                }
                composable(NavigationRoutes.CREATE_NEWS) {
                    CreateNewsView(controller = controller, innerPadding = innerPadding)
                }
                composable(
                    route = NavigationRoutes.EDIT_NEWS,
                    arguments = listOf(navArgument("newsId") { type = NavType.IntType })
                ) {
                    CreateNewsView(controller = controller, innerPadding = innerPadding)
                }
                composable(NavigationRoutes.EDIT_PROFILE) {
                    EditProfileView(controller = controller, innerPadding = innerPadding)
                }
                composable(NavigationRoutes.CURATOR_PAST_COURSES) {
                    CuratorPastCoursesView(controller = controller, innerPadding = innerPadding)
                }
                composable(
                    route = NavigationRoutes.CURATOR_COURSE_DETAIL,
                    arguments = listOf(navArgument("courseId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
                    CuratorCourseDetailView(
                        courseId = courseId,
                        controller = controller,
                        innerPadding = innerPadding
                    )
                }
                composable(NavigationRoutes.CERTIFICATES) {
                    CertificatesView(controller = controller, innerPadding = innerPadding)
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
    route == NavigationRoutes.COURSES -> "Курсы"
    route == NavigationRoutes.PAST_COURSES -> "Прошедшие курсы"
    route == NavigationRoutes.QR -> "QR"
    route == NavigationRoutes.CREATE_COURSE -> "Создание курса"
    route == NavigationRoutes.CREATE_EVENT -> "Создание мероприятия"
    route == NavigationRoutes.CREATE_NEWS -> "Создание новости"
    route == NavigationRoutes.EDIT_COURSE -> "Редактирование курса"
    route == NavigationRoutes.EDIT_EVENT -> "Редактирование мероприятия"
    route == NavigationRoutes.EDIT_NEWS -> "Редактирование новости"
    route == NavigationRoutes.EDIT_PROFILE -> "Редактирование профиля"
    route == NavigationRoutes.USERS -> "Пользователи"
    route == NavigationRoutes.CURATOR_PAST_COURSES -> "Мои прошедшие курсы"
    route == NavigationRoutes.CERTIFICATES -> "Мои сертификаты"
    route?.startsWith("curator_course_detail") == true -> "Посещаемость курса"
    else -> "PassPoint"
}

private fun mapRouteToMainEnum(route: String?, isAdmin: Boolean, isCurator: Boolean): MainEnum = when (route) {
    NavigationRoutes.PROFILE -> MainEnum.PROFILE
    NavigationRoutes.MAIN -> MainEnum.HOME
    NavigationRoutes.MINE -> if (isAdmin || isCurator) MainEnum.USERS else MainEnum.SETTINGS
    NavigationRoutes.USERS -> MainEnum.USERS
    else -> MainEnum.HOME
}

private fun mapMainEnumToRoute(enum: MainEnum, isAdmin: Boolean): String = when (enum) {
    MainEnum.PROFILE -> NavigationRoutes.PROFILE
    MainEnum.HOME -> NavigationRoutes.MAIN
    MainEnum.SETTINGS -> NavigationRoutes.MINE
    MainEnum.USERS -> NavigationRoutes.USERS
}