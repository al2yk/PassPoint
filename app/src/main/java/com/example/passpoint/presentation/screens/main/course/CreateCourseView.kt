package com.example.passpoint.presentation.screens.main.course

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.WarningMessage
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.theme.RobotoRegular
import com.example.passpoint.presentation.theme.White
import com.example.passpoint.presentation.viewModel.CreateCourseEvent
import com.example.passpoint.presentation.viewModel.CreateCourseViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: CreateCourseViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CreateCourseEvent.Success -> {
                    controller.navigate(NavigationRoutes.COURSES) {
                        popUpTo(NavigationRoutes.MAIN) { inclusive = false }
                    }
                }
            }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        disabledTextColor = MaterialTheme.colorScheme.onBackground,
        focusedBorderColor = BrandColor,
        unfocusedBorderColor = Gray600,
        disabledBorderColor = Gray600,
        focusedLabelColor = BrandColor,
        unfocusedLabelColor = Gray600,
        disabledLabelColor = Gray600,
        unfocusedSupportingTextColor = Gray600,
        disabledSupportingTextColor = Gray600,
        focusedSupportingTextColor = BrandColor
    )

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    val isFormValid = state.name.isNotBlank() && state.description.isNotBlank() &&
            state.date.isNotBlank() && state.place.isNotBlank() &&
            state.selectedCurator != null &&
            (state.capacity ?: 0) in 1..150

    Box(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (!viewModel.isEditMode) {
                SpacerHeight(8)

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { controller.navigate(NavigationRoutes.COURSES) },
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Посмотреть текущие курсы",
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
            }
            SpacerHeight(8)

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Курс", style = MaterialTheme.typography.headlineSmall)
                    SpacerHeight(16)

                    // Название
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = {
                            Text(
                                "Название курса",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors
                    )
                    SpacerHeight(12)

                    // Описание
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = {
                            Text(
                                "Описание курса",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors
                    )
                    SpacerHeight(12)

                    // Дата (календарь открывается при любом клике)
                    val dateDisplay = remember(state.date) {
                        if (state.date.isNotBlank()) {
                            try {
                                val localDate = LocalDate.parse(state.date)
                                localDate.format(dateFormatter)
                            } catch (e: Exception) {
                                state.date
                            }
                        } else ""
                    }
                    Box(modifier = Modifier.clickable { viewModel.showDatePicker(true) }) {
                        OutlinedTextField(
                            value = dateDisplay,
                            onValueChange = {},
                            enabled = false,                 // пропускаем клики
                            label = {
                                Text(
                                    "Дата проведения",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.calendar_add_on_24dp),
                                    contentDescription = "Выбрать дату"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            colors = textFieldColors
                        )
                    }
                    if (state.showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = System.currentTimeMillis()
                        )
                        DatePickerDialog(
                            onDismissRequest = { viewModel.showDatePicker(false) },
                            confirmButton = {
                                TextButton(onClick = {
                                    val selectedMillis = datePickerState.selectedDateMillis
                                    if (selectedMillis != null) {
                                        val selectedDate = Instant.ofEpochMilli(selectedMillis)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                        if (selectedDate.isBefore(LocalDate.now())) {
                                            return@TextButton
                                        }
                                        viewModel.onDateSelected(selectedDate.toString())
                                    }
                                }) { Text("OK") }
                            },
                            dismissButton = {
                                TextButton(onClick = { viewModel.showDatePicker(false) }) { Text("Отмена") }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                    SpacerHeight(12)

                    // Место
                    OutlinedTextField(
                        value = state.place,
                        onValueChange = { viewModel.updatePlace(it) },
                        label = {
                            Text(
                                "Место проведения",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors
                    )
                    SpacerHeight(12)

                    // Куратор
                    var showCuratorMenu by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = state.curators.isNotEmpty() && showCuratorMenu,
                        onExpandedChange = { showCuratorMenu = it }
                    ) {
                        val curatorLabel = if (state.isLoadingCurators) {
                            "Загрузка кураторов..."
                        } else if (state.curators.isEmpty()) {
                            "Нет доступных кураторов"
                        } else {
                            state.selectedCurator?.let { "${it.name} ${it.surname}" }
                                ?: "Выберите куратора"
                        }
                        OutlinedTextField(
                            value = curatorLabel,
                            onValueChange = {},
                            readOnly = true,
                            enabled = !state.isLoadingCurators && state.curators.isNotEmpty(),
                            label = { Text("Куратор", style = MaterialTheme.typography.bodyLarge) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCuratorMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            colors = textFieldColors
                        )
                        if (state.curators.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = showCuratorMenu,
                                onDismissRequest = { showCuratorMenu = false },
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ) {
                                state.curators.forEach { curator ->
                                    DropdownMenuItem(
                                        text = { Text("${curator.name} ${curator.surname}") },
                                        onClick = {
                                            viewModel.selectCurator(curator)
                                            showCuratorMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (state.isLoadingCurators) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.padding(start = 8.dp))
                            Text(
                                "Загрузка кураторов...",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    SpacerHeight(12)

                    // Количество мест
                    // Количество мест
                    OutlinedTextField(
                        value = state.capacity?.toString() ?: "",
                        onValueChange = { viewModel.updateCapacity(it) },
                        label = {
                            Text(
                                "Количество мест",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        supportingText = {
                            Text(
                                "Ограничение до 150 мест",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = RobotoRegular),
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = state.capacity != null && (state.capacity > 150 || state.capacity == 0),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors
                    )
                    SpacerHeight(16)

                    // Ошибка
                    if (state.error != null) {
                        WarningMessage(text = state.error)
                        SpacerHeight(16)
                    }

                    Button(
                        onClick = { viewModel.saveCourse() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ButtonHeight),
                        enabled = !state.isSending && isFormValid,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandColor)
                    ) {
                        if (state.isSending) {
                            CircularProgressIndicator(
                                color = White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                if (viewModel.isEditMode) "Сохранить" else "Создать",
                                style = MaterialTheme.typography.displaySmall,
                                color = White
                            )
                        }
                    }
                    SpacerHeight(16)
                }
            }
        }
    }
}