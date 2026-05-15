package com.example.passpoint.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.components.CourseCard
import com.example.passpoint.presentation.components.EventCard
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.Gray800
import com.example.passpoint.presentation.viewModel.MineViewModel

@Composable
fun MineView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MineViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
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

            state.registeredEvents.isEmpty() && state.registeredCourses.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Вы пока нигде не участвуете",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray600
                    )
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Мероприятия
                    if (state.registeredEvents.isNotEmpty()) {
                        item {
                            Text(
                                "Мероприятия",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(
                                    top = 16.dp,
                                    bottom = 16.dp,
                                    start = 16.dp
                                )
                            )
                        }
                        items(state.registeredEvents, key = { "event_${it.id}" }) { event ->
                            Column() {
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    EventCard(
                                        event = event,
                                        isRegistered = true,
                                        isRegistrationLoading = state.isRegistrationLoading,
                                        onRegisterClick = {},
                                        onUnregisterClick = {
                                            viewModel.showEventUnregisterConfirm(
                                                event.id
                                            )
                                        },
                                        showButtons = true,
                                        onQrClick = { controller.navigate("qr/${UserRepository.ID}") }
                                    )
                                }
                                SpacerHeight(8)
                            }
                        }
                    }

                    // Курсы
                    if (state.registeredCourses.isNotEmpty()) {
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
                        items(state.registeredCourses, key = { "course_${it.id}" }) { course ->
                            Column() {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        CourseCard(
                                            course = course,
                                            isRegistered = true,
                                            isRegistrationLoading = state.isRegistrationLoading,
                                            onRegisterClick = {},
                                            onUnregisterClick = {
                                                viewModel.showCourseUnregisterConfirm(
                                                    course.id
                                                )
                                            },
                                            showButtons = true,
                                            onQrClick = { controller.navigate("qr/${UserRepository.ID}") }
                                        )
                                    }
                                }
                                SpacerHeight(8)
                            }
                        }
                    }
                    item { SpacerHeight(16) }
                }
            }
        }
    }

    // Диалог отмены для мероприятия
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

    // Диалог отмены для курса
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