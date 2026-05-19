package com.example.passpoint.presentation.screens.main.curator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.presentation.components.CourseCard
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.components.WarningMessage
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.viewModel.CuratorMainViewModel
import java.time.LocalDate

@Composable
fun CuratorMainView(
    controller: NavHostController,
    viewModel: CuratorMainViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val listState = rememberLazyListState()

    // Прокрутка вверх при изменении типа сортировки
    LaunchedEffect(state.sortType) {
        listState.animateScrollToItem(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.error ?: "Ошибка", style = MaterialTheme.typography.bodyLarge)
                    SpacerHeight(16)
                    OutlinedButton(
                        onClick = { viewModel.retry() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp).height(ButtonHeight),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Повторить") }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SpacerHeight(8)
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Мои курсы", style = MaterialTheme.typography.headlineSmall)
                            SpacerHeight(12)

                            Row(verticalAlignment = Alignment.Bottom) {
                                OutlinedTextField(
                                    value = state.searchQuery,
                                    onValueChange = { viewModel.updateSearchQuery(it) },
                                    label = { Text("Поиск") },
                                    trailingIcon = {
                                        if (state.searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                                Icon(
                                                    painter = painterResource(R.drawable.close_24dp),
                                                    contentDescription = "Очистить"
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = MaterialTheme.shapes.small,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandColor,
                                        unfocusedBorderColor = Gray600
                                    ),
                                    singleLine = true
                                )
                                SpacerWidth(12)

                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    OutlinedButton(
                                        onClick = { expanded = true },
                                        modifier = Modifier.width(100.dp).height(56.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = when (state.sortType) {
                                                CuratorCourseSortType.NAME_ASC -> "А-Я"
                                                CuratorCourseSortType.NAME_DESC -> "Я-А"
                                                CuratorCourseSortType.DATE_ASC -> "Дата ↑"
                                                CuratorCourseSortType.DATE_DESC -> "Дата ↓"
                                            },
                                            style = MaterialTheme.typography.displaySmall
                                        )
                                        SpacerWidth(4)
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.width(100.dp),
                                        containerColor = MaterialTheme.colorScheme.background,
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("А-Я") },
                                            onClick = {
                                                viewModel.setSortType(CuratorCourseSortType.NAME_ASC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Я-А") },
                                            onClick = {
                                                viewModel.setSortType(CuratorCourseSortType.NAME_DESC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Дата ↓") },
                                            onClick = {
                                                viewModel.setSortType(CuratorCourseSortType.DATE_DESC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Дата ↑") },
                                            onClick = {
                                                viewModel.setSortType(CuratorCourseSortType.DATE_ASC)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    SpacerHeight(8)

                    when {
                        state.filteredCourses.isEmpty() && state.searchQuery.isNotBlank() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Ничего не найдено", style = MaterialTheme.typography.bodyLarge, color = Gray600)
                            }
                        }
                        state.filteredCourses.isEmpty() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("На вас не назначено курсов", style = MaterialTheme.typography.bodyLarge, color = Gray600)
                            }
                        }
                        else -> {
                            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                                items(state.filteredCourses, key = { it.id }) { course ->
                                    ElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp)
                                            .clickable {
                                                controller.navigate(
                                                    NavigationRoutes.CURATOR_COURSE_DETAIL.replace("{courseId}", course.id.toString())
                                                )
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            CourseCard(
                                                course = course,
                                                isRegistered = false,
                                                isRegistrationLoading = false,
                                                onRegisterClick = {},
                                                onUnregisterClick = {},
                                                showButtons = false,
                                                showCapacity = true
                                            )
                                            if (course.date == LocalDate.now().toString()) {
                                                SpacerHeight(8)
                                                WarningMessage(text = "Не забудь отметить посещаемость")
                                            }
                                        }
                                    }
                                    SpacerHeight(8)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}