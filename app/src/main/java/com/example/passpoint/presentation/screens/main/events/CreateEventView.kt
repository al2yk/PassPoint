package com.example.passpoint.presentation.screens.main.events

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.passpoint.R
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.WarningMessage
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.White
import com.example.passpoint.presentation.viewModel.CreateEventEvent
import com.example.passpoint.presentation.viewModel.CreateEventViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    val state = viewModel.state

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onPhotoPicked(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CreateEventEvent.Success -> {
                    controller.navigate(NavigationRoutes.EVENTS) {
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
        disabledLabelColor = Gray600
    )

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    val isFormValid = state.name.isNotBlank() && state.date.isNotBlank() && state.place.isNotBlank()

    Box(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            if (!viewModel.isEditMode) {
                SpacerHeight(8)
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { controller.navigate(NavigationRoutes.EVENTS) },
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Посмотреть мероприятия",
                                    color = BrandColor,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    painter = painterResource(R.drawable.arrow_outward_24dp),
                                    contentDescription = null,
                                    tint = BrandColor
                                )
                            }
                        }
                    }
                }
            }

            SpacerHeight(8)

            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Мероприятие", style = MaterialTheme.typography.headlineSmall)
                    SpacerHeight(16)

                    // Название
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = {
                            Text(
                                "Название мероприятия",
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

                    // Дата
                    val dateDisplay = remember(state.date) {
                        if (state.date.isNotBlank()) {
                            try {
                                LocalDate.parse(state.date).format(dateFormatter)
                            } catch (e: Exception) {
                                state.date
                            }
                        } else ""
                    }
                    Box(modifier = Modifier.clickable { viewModel.showDatePicker(true) }) {
                        OutlinedTextField(
                            value = dateDisplay,
                            onValueChange = {},
                            enabled = false,
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
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val selectedDate = Instant.ofEpochMilli(millis)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                        if (selectedDate.isBefore(LocalDate.now())) return@TextButton
                                        viewModel.onDateSelected(selectedDate.toString())
                                    }
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { viewModel.showDatePicker(false) }) {
                                    Text("Отмена")
                                }
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
                            imeAction = ImeAction.Done
                        ),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors
                    )
                    SpacerHeight(12)
                    OutlinedButton(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ButtonHeight),
                        shape = RoundedCornerShape(8.dp)

                    ) {
                        Icon(
                            painter = if (state.imageUrl == null) painterResource(R.drawable.add_24dp) else painterResource(
                                R.drawable.cached_24dp
                            ),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (state.imageUrl == null) "Добавить фото" else "Изменить фото")
                    }

                    if (state.isUploadingImage) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else if (state.imageUrl != null) {
                        AsyncImage(
                            model = state.imageUrl,
                            contentDescription = "Загруженное фото",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop

                        )
                    }

                    SpacerHeight(16)

                    if (state.error != null) {
                        WarningMessage(text = state.error!!)
                        SpacerHeight(8)
                    }

                    Button(
                        onClick = { viewModel.saveEvent() },
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