package com.example.passpoint.presentation.screens.main

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.passpoint.R
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.utils.formatDateRu
import com.example.passpoint.presentation.components.CourseCard
import com.example.passpoint.presentation.components.CuratorItem
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.screens.main.admin.AdminMainView
import com.example.passpoint.presentation.screens.main.curator.CuratorMainView
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.theme.Gray800
import com.example.passpoint.presentation.theme.White
import com.example.passpoint.presentation.viewModel.MainViewModel
import java.time.LocalDate

@Composable
fun MainView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val lifecycleOwner = LocalLifecycleOwner.current
    val isAdmin = UserRepository.role == 3
    val isCurator = UserRepository.role == 2
    // Автоматическое обновление регистраций при возврате на экран
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshRegistrations()
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
        if (isAdmin) {
            AdminMainView(controller = controller)
        } else if (isCurator) {
            CuratorMainView(controller)
        } else {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
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
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        SpacerHeight(16)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.retry() },

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(ButtonHeight),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Повторить", style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                            }
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        SpacerHeight(8)
                        val new = state.news
                            .filter { it.create.isNotBlank() }
                            .maxByOrNull { runCatching { LocalDate.parse(it.create) }.getOrDefault(LocalDate.MIN) }
                        // NEWS
                        ElevatedCard {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                if (new != null) {
                                    Text(new.title, style = MaterialTheme.typography.headlineSmall)
                                    SpacerHeight(10)
                                    Text(
                                        new.new_text,
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray600
                                    )
                                    SpacerHeight(10)

                                    if (new.photo.isNotBlank()) {
                                        val imgState = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(new.photo)
                                                .size(Size.ORIGINAL).build()
                                        ).state

                                        if (imgState is AsyncImagePainter.State.Error) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                        if (imgState is AsyncImagePainter.State.Success) {
                                            Image(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(220.dp)
                                                    .clip(RoundedCornerShape(15.dp)),
                                                painter = imgState.painter,
                                                contentDescription = "",
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        SpacerHeight(5)
                                    }

                                    // Кнопка "Посмотреть все новости" (остаётся без изменений)
                                    Button(
                                        onClick = { controller.navigate(NavigationRoutes.NEWS) },
                                        contentPadding = PaddingValues(horizontal = 0.dp),
                                        shape = RoundedCornerShape(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "Посмотреть все новости",
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
                                } else {
                                    Text(
                                        "Новостей пока нет",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    SpacerHeight(10)
                                    Text(
                                        "Следите за появлением новостей в мобильном приложении, чтобы быть в курсе всех событий",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray600
                                    )
                                    SpacerHeight(16)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                    )
                                    SpacerHeight(10)
                                    Button(
                                        onClick = { controller.navigate(NavigationRoutes.NEWS) },
                                        contentPadding = PaddingValues(horizontal = 0.dp),
                                        shape = RoundedCornerShape(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "Посмотреть все новости",
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
                        // EVENTS
                        ElevatedCard {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                val today = LocalDate.now()
                                val upcomingEvents = state.events.filter {
                                    try {
                                        LocalDate.parse(it.date) >= today
                                    } catch (e: Exception) {
                                        false
                                    }
                                }
                                val event = upcomingEvents.minByOrNull { LocalDate.parse(it.date) }

                                if (event != null) {
                                    Text(event.name, style = MaterialTheme.typography.headlineSmall)
                                    SpacerHeight(14)
                                    Text(
                                        "Дата проведения",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray600
                                    )
                                    Text(
                                        text = formatDateRu(event.date),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    SpacerHeight(7)
                                    Text(
                                        "Место проведения",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray600
                                    )
                                    Text(
                                        event.place,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    SpacerHeight(16)
                                    val registered =
                                        state.registrations.any { it.event == event.id }
                                    if (registered) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { viewModel.showUnregisterConfirm(event.id) }, // было viewModel.unregisterForEvent
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(ButtonHeight),
                                                shape = RoundedCornerShape(8.dp),
                                                enabled = !state.isRegistrationLoading
                                            ) {
                                                Text(
                                                    "Отменить",
                                                    style = MaterialTheme.typography.displaySmall
                                                )
                                            }
                                            Button(
                                                onClick = { controller.navigate("qr/${UserRepository.ID}") },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(ButtonHeight),
                                                enabled = !state.isRegistrationLoading,
                                                shape = MaterialTheme.shapes.small,
                                                colors = ButtonDefaults.buttonColors(containerColor = BrandColor)
                                            ) {
                                                Text(
                                                    "Показать QR",
                                                    color = White,
                                                    style = MaterialTheme.typography.displaySmall
                                                )
                                            }
                                        }
                                    } else {
                                        OutlinedButton(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(ButtonHeight),
                                            enabled = !state.isRegistrationLoading,
                                            onClick = { viewModel.showRegisterConfirm(event.id) },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                "Участвовать",
                                                style = MaterialTheme.typography.displaySmall,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    }
                                    SpacerHeight(10)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                    )
                                    Button(
                                        onClick = {
                                            controller.navigate(NavigationRoutes.EVENTS)
                                        },
                                        contentPadding = PaddingValues(0.dp),
                                        shape = RoundedCornerShape(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "Посмотреть все мероприятия",
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
                                } else {
                                    Text(
                                        "Пока ничего не запланировано",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    SpacerHeight(5)
                                    Text(
                                        "Следите за новостями — скоро что-то появится",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray600
                                    )
                                    SpacerHeight(16)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                    )
                                    Button(
                                        onClick = { controller.navigate(NavigationRoutes.PAST_EVENTS) },
                                        contentPadding = PaddingValues(0.dp),
                                        shape = RoundedCornerShape(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "Посмотреть все прошедшие мероприятия",
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
                        // COURSES
                        ElevatedCard {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                val today = LocalDate.now()
                                val upcomingEvents = state.course.filter {
                                    try {
                                        LocalDate.parse(it.date) >= today
                                    } catch (e: Exception) {
                                        false
                                    }
                                }
                                val course = upcomingEvents.minByOrNull { LocalDate.parse(it.date) }

                                if (course != null) {

                                    // внутри ElevatedCard курсов
                                    val isRegistered =
                                        state.courseRegistrations.any { it.course == course.id }
                                    val curatorName = state.curators.find { it.id.toString() == course.curator }?.let { "${it.name} ${it.surname}" } ?: "—"
                                    CourseCard(
                                        course = course,
                                        isRegistered = isRegistered,
                                        isRegistrationLoading = state.isRegistrationLoading,
                                        onRegisterClick = {
                                            viewModel.showRegisterCourseConfirm(
                                                course.id
                                            )
                                        },
                                        onUnregisterClick = {
                                            viewModel.showUnregisterCourseConfirm(
                                                course.id
                                            )
                                        },
                                        showButtons = true,
                                        onQrClick = { controller.navigate("qr/${UserRepository.ID}") },
                                        curatorName = curatorName
                                    )
                                    SpacerHeight(10)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                    )
                                    Button(
                                        onClick = {
                                            controller.navigate(NavigationRoutes.COURSES)
                                        },
                                        contentPadding = PaddingValues(0.dp),
                                        shape = RoundedCornerShape(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "Посмотреть все курсы",
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
                                } else {
                                    Text(
                                        "Ни одного курса не запланированоо",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    SpacerHeight(5)
                                    Text(
                                        "Новые группы появятся здесь, как только будут добавлены",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray600
                                    )
                                    SpacerHeight(16)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                    )
                                    Button(
                                        onClick = { controller.navigate(NavigationRoutes.PAST_COURSES) },
                                        contentPadding = PaddingValues(0.dp),
                                        shape = RoundedCornerShape(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "Посмотреть прошедшие курсы",
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
                        if (state.curators.isNotEmpty()) {
                            // CURATORS
                            ElevatedCard {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        "Наши кураторы",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    SpacerHeight(16)


                                    val rows = state.curators.chunked(2)
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rows.forEachIndexed { index, row ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                row.forEach { curator ->
                                                    Box(modifier = Modifier.weight(1f)) {
                                                        CuratorItem(curator = curator)
                                                    }
                                                }
                                                if (row.size == 1) {
                                                    Box(modifier = Modifier.weight(1f))
                                                }
                                            }
                                            if (index < rows.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(vertical = 8.dp),
                                                    thickness = 1.dp,
                                                    color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                        alpha = 0.2f
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        SpacerHeight(8)
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
            // Диалог подтверждения для КУРСОВ
            if (state.courseConfirmDialog != null) {
                val isRegister = state.courseConfirmDialog.action == ConfirmAction.REGISTER
                AlertDialog(
                    onDismissRequest = { viewModel.hideCourseDialog() },
                    title = { Text(if (isRegister) "Подтверждение участия" else "Отмена участия на курсе") },
                    text = {
                        Text(
                            if (isRegister) "Вы уверены, что хотите записаться на курс?" else "Вы уверены, что хотите отменить запись на курс?",
                            color = Gray800
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.confirmCourseAction() }) {
                            Text(if (isRegister) "Записаться" else "Отменить запись")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.hideCourseDialog() }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}
