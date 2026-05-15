@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.passpoint.presentation.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.components.PagerIndicator
import com.example.passpoint.presentation.components.PrimaryButton
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.components.isCompactLayoutRequired
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.ui.theme.Background
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import kotlin.math.absoluteValue

@Composable
fun OnboardingView(
    controller: NavHostController,
    pagesViewModel: OnboardingPagingViewModel = viewModel()
) {
    val currentScreen = pagesViewModel.currentScreen.collectAsState().value

    val onSkipClicked: () -> Unit = {
        UserRepository.act = 1
        controller.navigate(NavigationRoutes.SIGNIN) {
            popUpTo(NavigationRoutes.ONBOARDING) {
                inclusive = true
            }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = currentScreen.ordinal,
        pageCount = { OnBoardingEnum.entries.size }
    )

    LaunchedEffect(currentScreen.ordinal) {
        val targetPage = currentScreen.ordinal
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val newStep = pagerState.currentPage
        if (newStep != currentScreen.ordinal) {
            pagesViewModel.setCurrentStep(newStep)
        }
    }

    BackHandler(
        enabled = currentScreen != OnBoardingEnum.ONBOARDING1,
        onBack = {
            pagesViewModel.clickBackOnBoarding()
        }
    )
    BoxWithConstraints {
        val isCompact = isCompactLayoutRequired()
        Background(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .padding(top = 95.dp)
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.Center
                ) {
                    PagerIndicator(OnBoardingEnum.entries.size, currentScreen.ordinal)
                }
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = true,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                val pageOffset =
                                    ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)

                                alpha = lerp(
                                    start = 0f,
                                    stop = 1f,
                                    fraction = (0.5f - pageOffset.absoluteValue.coerceIn(
                                        0f,
                                        0.5f
                                    )) * 2f
                                )

                                translationX = size.width * pageOffset * 0.9f
                            }
                            .fillMaxSize()
                    ) {
                        when (page) {
                            0 -> OnBoarding1(isCompact)
                            1 -> OnBoarding2(isCompact)
                            2 -> OnBoarding3(isCompact)
                            3 -> OnBoarding4(isCompact)
                        }
                    }
                }

                // Кнопки навигации
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .padding(bottom = 50.dp)
                ) {
                    TextButton(
                        onClick = onSkipClicked,
                        modifier = Modifier
                            .height(ButtonHeight)
                            .weight(1f),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            "Пропустить",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    SpacerWidth(8)
                    PrimaryButton(
                        onClick = {
                            if (pagerState.currentPage != OnBoardingEnum.ONBOARDING4.ordinal) {
                                pagesViewModel.clickNextOnBoarding()
                            } else {
                                onSkipClicked()
                            }

                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Далее",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}