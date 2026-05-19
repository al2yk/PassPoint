package com.example.passpoint.presentation.screens.main.events

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.components.EventCard
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.screens.main.ConfirmAction
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.Gray800
import com.example.passpoint.presentation.viewModel.EventsViewModel

@Composable
fun EventsView(
    controller: NavHostController? = null,
    innerPadding: PaddingValues,
    viewModel: EventsViewModel = hiltViewModel()
) {

    val state = viewModel.state

    Box(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    Text(text = state.error ?: "Ошибка")
                    SpacerHeight(16)
                    OutlinedButton(
                        onClick = { viewModel.retry() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .height(ButtonHeight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Повторить")
                    }
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SpacerHeight(8)
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
                                        // Текст в зависимости от текущего sortType
                                        Text(
                                            text = when (state.sortType) {
                                                EventSortType.NAME_ASC -> "А-Я"
                                                EventSortType.NAME_DESC -> "Я-А"
                                                EventSortType.DATE_ASC -> "Дата ↑"
                                                EventSortType.DATE_DESC -> "Дата ↓"
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
                                                viewModel.setSortType(EventSortType.NAME_ASC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Я-А") },
                                            onClick = {
                                                viewModel.setSortType(EventSortType.NAME_DESC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Дата ↓") },
                                            onClick = {
                                                viewModel.setSortType(EventSortType.DATE_DESC)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Дата ↑") },
                                            onClick = {
                                                viewModel.setSortType(EventSortType.DATE_ASC)
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
                        if (state.filteredUpcomingEvents.isEmpty() && state.searchQuery.isNotBlank()) {
                            item(key = "nothing1") {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        SpacerHeight(4)
                                        Text(
                                            "Ничего не найдено",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Gray600
                                        )
                                        SpacerHeight(4)
                                    }
                                }
                                SpacerHeight(8)
                            }
                        } else if (state.filteredUpcomingEvents.isEmpty()) {
                            item("no events") {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        SpacerHeight(4)
                                        Text(
                                            "Нет предстоящих мероприятий",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Gray600
                                        )
                                    }
                                }
                            }
                        } else {
                            items(
                                items = state.filteredUpcomingEvents,
                                key = { event -> event.id }
                            ) { event ->
                                val registered = state.registrations.any { it.event == event.id }
                                EventCard(
                                    event = event,
                                    isRegistered = registered,
                                    isRegistrationLoading = state.isRegistrationLoading,
                                    onRegisterClick = { viewModel.showRegisterConfirm(event.id) },
                                    onUnregisterClick = { viewModel.showUnregisterConfirm(event.id) },
                                    onQrClick = { controller?.navigate("qr/${UserRepository.ID}") },
                                    onDeleteClick = { viewModel.showDeleteConfirm(event.id) },
                                    onEditClick = { controller?.navigate("edit_event/${event.id}") }
                                )
                                SpacerHeight(8)
                            }
                        }
                        item("past") {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Button(
                                        onClick = { controller?.navigate(NavigationRoutes.PAST_EVENTS) },
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "Посмотреть прошедшие мероприятия",
                                                modifier = Modifier.weight(1f),
                                                color = BrandColor,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Icon(
                                                contentDescription = "",
                                                painter = painterResource(R.drawable.arrow_outward_24dp),
                                                tint = BrandColor
                                            )
                                        }
                                    }
                                }
                            }
                            SpacerHeight(16)
                        }
                    }
                }
            }
        }
    }

    // Диалог подтверждения
    if (state.confirmDialog != null) {
        val isRegister = state.confirmDialog.action == ConfirmAction.REGISTER
        AlertDialog(
            onDismissRequest = { viewModel.hideDialog() },
            title = { Text(if (isRegister) "Подтверждение участия" else "Отмена участия") },
            text = {
                Text(
                    if (isRegister) "Вы уверены, что хотите принять участие?" else "Вы уверены, что хотите отменить участие?",
                    color = Gray800
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmAction() }) {
                    Text(if (isRegister) "Принять" else "Отменить")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDialog() }) {
                    Text("Отмена")
                }
            }
        )
    }
    if (state.deleteDialog != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text("Удаление мероприятия") },
            text = { Text("Вы уверены, что хотите удалить мероприятие?", color = Gray800) },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteAction() }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDialog() }) { Text("Отмена") }
            }
        )
    }
}