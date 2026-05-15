package com.example.passpoint.presentation.screens.main.events

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.presentation.components.EventCard
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.viewModel.EventsViewModel

@Composable
fun PastEventsView(
    controller: NavHostController? = null,
    innerPadding: PaddingValues,
    viewModel: EventsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.showPast()
    }

    val state = viewModel.state
    val events = state.pastEvents

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
            events.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Прошедших мероприятий пока нет", style = MaterialTheme.typography.bodyLarge, color = Gray600)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(events) { event ->
                        SpacerHeight(8)
                        EventCard(
                            event = event,
                            isRegistered = false,
                            isRegistrationLoading = false,
                            onRegisterClick = {},
                            onUnregisterClick = {},
                            showButtons = false,
                            onQrClick = { controller?.navigate("qr/${UserRepository.ID}") }
                        )
                    }
                }
            }
        }
    }
}