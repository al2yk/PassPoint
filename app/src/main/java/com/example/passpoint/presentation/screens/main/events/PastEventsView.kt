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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.passpoint.presentation.components.EventCard
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.viewModel.EventsViewModel

@Composable
fun PastEventsView(
    controller: NavHostController? = null,
    innerPadding: PaddingValues,
    viewModel: EventsViewModel = hiltViewModel()
) {

    val state = viewModel.state
    val events = state.filteredPastEvents

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
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = state.error ?: "Ошибка")
                    SpacerHeight(16)
                    OutlinedButton(
                        onClick = { viewModel.retry() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp).height(ButtonHeight),
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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
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
                                        modifier = Modifier.width(100.dp).height(56.dp),
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
                        if (events.isEmpty() && state.searchQuery.isNotBlank()) {
                            item(key = "nothing") {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Ничего не найдено", style = MaterialTheme.typography.bodyLarge, color = Gray600)
                                }
                            }
                        } else if (events.isEmpty()) {
                            item("past no") {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Прошедших мероприятий пока нет", style = MaterialTheme.typography.bodyLarge, color = Gray600)
                                }
                            }
                        } else {
                            items(
                                items = events,
                                key = { event -> event.id }
                            ) { event ->
                                EventCard(
                                    event = event,
                                    isRegistered = false,
                                    isRegistrationLoading = false,
                                    onRegisterClick = {},
                                    onUnregisterClick = {},
                                    showButtons = false,
                                    onQrClick = { controller?.navigate("qr/${UserRepository.ID}") },
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}