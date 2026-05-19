package com.example.passpoint.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.components.CourseCard
import com.example.passpoint.presentation.components.EventCard
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.screens.main.mine.RegisteredSortType
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.Gray800
import com.example.passpoint.presentation.viewModel.MineViewModel
import java.time.LocalDate

@Composable
fun MineView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MineViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Box(
        modifier = Modifier
            .padding(innerPadding)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.error ?: "Ошибка", style = MaterialTheme.typography.bodyLarge)
                    SpacerHeight(16)
                    OutlinedButton(
                        onClick = { viewModel.retry() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .height(ButtonHeight),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Повторить") }
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SpacerHeight(8)
                    // Карточка поиска и сортировки (без изменений)
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(56.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = when (state.sortType) {
                                                RegisteredSortType.NAME_ASC -> "А-Я"
                                                RegisteredSortType.NAME_DESC -> "Я-А"
                                                RegisteredSortType.DATE_ASC -> "Дата ↑"
                                                RegisteredSortType.DATE_DESC -> "Дата ↓"
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
                                                viewModel.setSortType(RegisteredSortType.NAME_ASC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Я-А") },
                                            onClick = {
                                                viewModel.setSortType(RegisteredSortType.NAME_DESC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Дата ↓") },
                                            onClick = {
                                                viewModel.setSortType(RegisteredSortType.DATE_DESC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Дата ↑") },
                                            onClick = {
                                                viewModel.setSortType(RegisteredSortType.DATE_ASC)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    SpacerHeight(8)

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        // Секция "Мероприятия"
                        if (state.filteredEvents.isNotEmpty()) {
                            item {
                                Text(
                                    "Мероприятия",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(
                                        top = 8.dp,
                                        bottom = 8.dp,
                                        start = 16.dp
                                    )
                                )
                            }
                            items(state.filteredEvents, key = { "event_${it.id}" }) { event ->
                                val isPast = runCatching {
                                    LocalDate.parse(event.date).isBefore(LocalDate.now())
                                }.getOrDefault(false)
                                EventCard(
                                    event = event,
                                    isRegistered = true,
                                    isRegistrationLoading = state.isRegistrationLoading,
                                    onRegisterClick = {},
                                    onUnregisterClick = {
                                        if (!isPast) viewModel.showEventUnregisterConfirm(event.id)
                                    },
                                    showButtons = !isPast,
                                    onQrClick = { controller.navigate("qr/${UserRepository.ID}") }
                                )
                                SpacerHeight(8)
                            }
                        } else if (state.searchQuery.isNotBlank() && state.filteredEvents.isEmpty()) {
                            item {
                                Text(
                                    "Мероприятия",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(
                                        top = 8.dp,
                                        bottom = 8.dp,
                                        start = 16.dp
                                    )
                                )
                                Text(
                                    "Нет мероприятий по вашему запросу",
                                    modifier = Modifier.padding(start = 16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Gray600
                                )
                                SpacerHeight(8)
                            }
                        }

                        // Секция "Курсы"
                        if (state.filteredCourses.isNotEmpty()) {
                            item {
                                Text(
                                    "Курсы",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(
                                        top = 16.dp,
                                        bottom = 8.dp,
                                        start = 16.dp
                                    )
                                )
                            }
                            items(state.filteredCourses, key = { "course_${it.id}" }) { course ->
                                val isPast = runCatching {
                                    LocalDate.parse(course.date).isBefore(LocalDate.now())
                                }.getOrDefault(false)

                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        CourseCard(
                                            course = course,
                                            isRegistered = true,
                                            isRegistrationLoading = state.isRegistrationLoading,
                                            onRegisterClick = {},
                                            onUnregisterClick = {
                                                if (!isPast) viewModel.showCourseUnregisterConfirm(
                                                    course.id
                                                )
                                            },
                                            showButtons = !isPast,
                                            onQrClick = { controller.navigate("qr/${UserRepository.ID}") }
                                        )
                                    }
                                }
                                SpacerHeight(8)
                            }
                        } else if (state.searchQuery.isNotBlank() && state.filteredCourses.isEmpty()) {
                            item {
                                Text(
                                    "Курсы",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(
                                        top = 16.dp,
                                        bottom = 8.dp,
                                        start = 16.dp
                                    )
                                )
                                Text(
                                    "Нет курсов по вашему запросу",
                                    modifier = Modifier.padding(start = 16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Gray600
                                )
                                SpacerHeight(8)
                            }
                        }

                        // Если вообще ничего не найдено (и нет ни одного раздела)
                        if (state.filteredEvents.isEmpty() && state.filteredCourses.isEmpty()) {
                            item {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (state.searchQuery.isNotBlank()) "Ничего не найдено"
                                        else "Вы пока нигде не участвуете",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Gray600
                                    )
                                }
                            }
                        }
                        item { SpacerHeight(16) }
                    }
                }
            }
        }
    }

    // Диалоги отмены (без изменений)
    if (state.eventConfirmDialog != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideEventDialog() },
            title = { Text("Отмена участия") },
            text = {
                Text(
                    "Вы уверены, что хотите отменить участие в мероприятии?",
                    color = Gray800
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmEventAction() }) { Text("Отменить") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideEventDialog() }) { Text("Назад") }
            }
        )
    }

    if (state.courseConfirmDialog != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideCourseDialog() },
            title = { Text("Отмена записи на курс") },
            text = { Text("Вы уверены, что хотите отменить запись на курс?", color = Gray800) },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmCourseAction() }) { Text("Отменить") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideCourseDialog() }) { Text("Назад") }
            }
        )
    }
}