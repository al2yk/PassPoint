package com.example.passpoint.presentation.screens.main.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.Gray500
import com.example.passpoint.presentation.viewModel.AdminMainViewModel

@Composable
fun AdminMainView(
    controller: NavHostController,
    viewModel: AdminMainViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Column {
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
                        Text("Создать курс", modifier = Modifier.weight(1f), color = BrandColor, style = MaterialTheme.typography.titleMedium)
                        Icon(painter = painterResource(R.drawable.arrow_outward_24dp), contentDescription = "", tint = BrandColor)
                    }
                }
                Button(
                    onClick = { controller.navigate(NavigationRoutes.CREATE_EVENT) },
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Создать мероприятие", modifier = Modifier.weight(1f), color = BrandColor, style = MaterialTheme.typography.titleMedium)
                        Icon(painter = painterResource(R.drawable.arrow_outward_24dp), contentDescription = "", tint = BrandColor)
                    }
                }
                Button(
                    onClick = { controller.navigate(NavigationRoutes.CREATE_NEWS) },
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Опубликовать новость", modifier = Modifier.weight(1f), color = BrandColor, style = MaterialTheme.typography.titleMedium)
                        Icon(painter = painterResource(R.drawable.arrow_outward_24dp), contentDescription = "", tint = BrandColor)
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
                Text("Курсы", style = MaterialTheme.typography.titleMedium, color = BrandColor)
                SpacerHeight(8)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatItem(label = "Сегодня", value = state.todayCourses)
                    StatItem(label = "Будущих", value = state.activeCourses)
                    StatItem(label = "Всего", value = state.totalCourses)
                }

                SpacerHeight(16)

                // Блок мероприятий
                Text("Мероприятия", style = MaterialTheme.typography.titleMedium, color = BrandColor)
                SpacerHeight(8)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatItem(label = "Сегодня", value = state.todayEvents)
                    StatItem(label = "Будущих", value = state.upcomingEvents)
                    StatItem(label = "Всего", value = state.totalEvents)
                }
            }
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