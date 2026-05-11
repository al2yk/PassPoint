package com.example.passpoint.presentation.screens.main.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.presentation.components.AttendancePieChart
import com.example.passpoint.presentation.components.LegendItem
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.BrandTonal200
import com.example.passpoint.presentation.theme.Gray500
import com.example.passpoint.presentation.theme.Gray800
import com.example.passpoint.presentation.theme.Green500
import com.example.passpoint.presentation.theme.Yellow500
import com.example.passpoint.presentation.viewModel.AdminMainViewModel

@Composable
fun AdminMainView(
    controller: NavHostController,
    viewModel: AdminMainViewModel = hiltViewModel()
) {
    val state = viewModel.state
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SpacerHeight(8)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Управление", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { controller.navigate(NavigationRoutes.CREATE_COURSE) },
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Создать курс",
                                modifier = Modifier.weight(1f),
                                color = BrandColor,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                painter = painterResource(R.drawable.arrow_outward_24dp),
                                contentDescription = "",
                                tint = BrandColor
                            )
                        }
                    }
                    Button(
                        onClick = { controller.navigate(NavigationRoutes.CREATE_EVENT) },
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Создать мероприятие",
                                modifier = Modifier.weight(1f),
                                color = BrandColor,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                painter = painterResource(R.drawable.arrow_outward_24dp),
                                contentDescription = "",
                                tint = BrandColor
                            )
                        }
                    }
                    Button(
                        onClick = { controller.navigate(NavigationRoutes.CREATE_NEWS) },
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Опубликовать новость",
                                modifier = Modifier.weight(1f),
                                color = BrandColor,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                painter = painterResource(R.drawable.arrow_outward_24dp),
                                contentDescription = "",
                                tint = BrandColor
                            )
                        }
                    }
                }
            }

            SpacerHeight(8)

            // Сводка
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Сводка", style = MaterialTheme.typography.headlineSmall)
                    SpacerHeight(16)

                    // Блок курсов
                    Text("Курсы", style = MaterialTheme.typography.titleMedium)
                    SpacerHeight(8)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(label = "Сегодня", value = state.todayCourses)
                        StatItem(label = "Будущих", value = state.activeCourses)
                        StatItem(label = "Всего", value = state.totalCourses)
                    }

                    SpacerHeight(16)

                    // Блок мероприятий
                    Text(
                        "Мероприятия",
                        style = MaterialTheme.typography.titleMedium
                    )
                    SpacerHeight(8)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(label = "Сегодня", value = state.todayEvents)
                        StatItem(label = "Будущих", value = state.upcomingEvents)
                        StatItem(label = "Всего", value = state.totalEvents)
                    }
                }
            }

            SpacerHeight(8)

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Посещаемость курсов", style = MaterialTheme.typography.headlineSmall)
                    SpacerHeight(16)
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier.width(240.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AttendancePieChart(
                                attended = state.attended,
                                missed = state.missed,
                                participating = state.participating  // новое поле
                            )
                        }
                        SpacerWidth(8)
                        Column(
                            modifier = Modifier.fillMaxWidth().offset(y= (-20).dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val total = state.attended + state.missed + state.participating
                            val attendedFraction = if (total > 0) state.attended.toFloat() / total else 0f
                            val missedFraction = if (total > 0) state.missed.toFloat() / total else 0f
                            val participatingFraction = if (total > 0) state.participating.toFloat() / total else 0f

                            SpacerHeight(16)

                            // Присутствовало
                            LegendItem(
                                color = Yellow500,
                                value = state.attended,
                                fraction = attendedFraction,
                                label = "Присутствовало"
                            )
                            SpacerHeight(12)

                            // Отсутствовало
                            LegendItem(
                                color = Green500,
                                value = state.missed,
                                fraction = missedFraction,
                                label = "Отсутствовало"
                            )
                            SpacerHeight(12)

                            // Участвует
                            LegendItem(
                                color = BrandColor,
                                value = state.participating,
                                fraction = participatingFraction,
                                label = "Участвует"
                            )
                            SpacerHeight(12)

                            // Всего записей
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$total",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Всего записей",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Gray800
                                )
                            }
                        }
                    }
                }
            }
            SpacerHeight(8)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = BrandColor)
        Text(text = label, style = MaterialTheme.typography.displaySmall, color = Gray500)
    }
}