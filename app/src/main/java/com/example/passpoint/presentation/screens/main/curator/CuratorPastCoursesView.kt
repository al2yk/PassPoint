package com.example.passpoint.presentation.screens.main.curator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.presentation.components.CourseCard
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.viewModel.CuratorPastCoursesViewModel

@Composable
fun CuratorPastCoursesView(
    controller: NavHostController?,
    innerPadding: PaddingValues,
    viewModel: CuratorPastCoursesViewModel = hiltViewModel()
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
            state.courses.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет прошедших курсов", style = MaterialTheme.typography.bodyLarge, color = Gray600)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.courses) { course ->
                        SpacerHeight(8)
                        ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                CourseCard(
                                    course = course,
                                    isRegistered = false,
                                    isRegistrationLoading = false,
                                    onRegisterClick = {},
                                    onUnregisterClick = {},
                                    showButtons = false,
                                    showCapacity = false
                                )
                            }
                        }
                    }
                    item { SpacerHeight(8) }
                }
            }
        }
    }
}