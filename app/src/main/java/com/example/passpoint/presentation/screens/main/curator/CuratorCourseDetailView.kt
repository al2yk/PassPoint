package com.example.passpoint.presentation.screens.main.curator

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.passpoint.R
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.screens.main.admin.roleToString
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray350
import com.example.passpoint.presentation.theme.Gray500
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.theme.Red800
import com.example.passpoint.presentation.theme.White
import com.example.passpoint.presentation.viewModel.CuratorCourseDetailViewModel

@Composable
fun CuratorCourseDetailView(
    courseId: Int,
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: CuratorCourseDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(courseId) {
        viewModel.loadData(courseId)
    }
    val state = viewModel.state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding())
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        when {
            state.value.isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.value.error != null -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.value.error ?: "Ошибка", style = MaterialTheme.typography.bodyLarge)
                    SpacerHeight(16)
                    OutlinedButton(
                        onClick = { viewModel.loadData(courseId) },
                        modifier = Modifier
                            .padding(horizontal = 40.dp)
                            .height(48.dp)
                    ) { Text("Повторить") }
                }
            }

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.value.participants) { participant ->
                    SpacerHeight(8)
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Фото
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Gray350)
                                ) {
                                    val imgState = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(participant.user.photo)
                                            .size(Size.ORIGINAL).build()
                                    ).state
                                    if (imgState is AsyncImagePainter.State.Error) {
                                        Icon(
                                            painter = painterResource(R.drawable.person_24dp),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxSize(0.6f)
                                                .align(Alignment.Center),
                                            tint = Gray600
                                        )
                                    } else if (imgState is AsyncImagePainter.State.Success) {
                                        androidx.compose.foundation.Image(
                                            painter = imgState.painter,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                                SpacerWidth(12)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${participant.user.name} ${participant.user.surname}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        participant.user.email,
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray600
                                    )
                                }
                            }
                            SpacerHeight(8)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Роль",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Gray500
                                )
                                Text(
                                    roleToString(participant.user.role),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (!participant.user.phone.isNullOrBlank()) {
                                SpacerHeight(4)
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Мобильный телефон",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray500
                                    )
                                    Text(
                                        participant.user.phone,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            if (participant.user.role == 1 && !participant.user.organization.isNullOrBlank()) {
                                SpacerHeight(4)
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Организация",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray500
                                    )
                                    Text(
                                        participant.user.organization,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            SpacerHeight(16)
                            // Проверяем, можно ли отмечать посещаемость (статус "Участвует" и курс сегодня)
                            val canMarkAttendance =
                                participant.status == 1 || participant.status == null
                            val isToday =
                                state.value.courseDate == java.time.LocalDate.now().toString()

                            if (canMarkAttendance && isToday) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.showConfirm(
                                                participant.attendanceId,
                                                "${participant.user.name} ${participant.user.surname}",
                                                3
                                            )
                                        },
                                        modifier = Modifier
                                            .width(140.dp)
                                            .height(ButtonHeight),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            "Отсутствовал",
                                            style = MaterialTheme.typography.displaySmall
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.showConfirm(
                                                participant.attendanceId,
                                                "${participant.user.name} ${participant.user.surname}",
                                                2
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(ButtonHeight),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            "Присутствовал",
                                            color = White,
                                            style = MaterialTheme.typography.displaySmall
                                        )
                                    }
                                }
                            } else {
                                // Если курс не сегодня или статус уже изменён, показываем статус текстом
                                when (participant.status) {
                                    2 -> Text(
                                        "Присутствовал",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = BrandColor
                                    )

                                    3 -> Text(
                                        "Отсутствовал",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Red800
                                    )

                                    else -> Text(
                                        "Участвует",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Gray500
                                    )
                                }
                            }
                        }
                    }
                }
                item { SpacerHeight(8) }
            }
        }

        // Диалог подтверждения
        if (state.value.confirmDialog != null) {
            val dialog = state.value.confirmDialog!!
            AlertDialog(
                onDismissRequest = { viewModel.hideConfirm() },
                title = { Text(if (dialog.newStatus == 2) "Подтверждение присутствия" else "Подтверждение отсутствия") },
                text = { Text("${dialog.userName} ${if (dialog.newStatus == 2) "присутствовал" else "отсутствовал"} на курсе «${dialog.courseName}»?") },
                confirmButton = { TextButton(onClick = { viewModel.confirmAttendance() }) { Text("Подтвердить") } },
                dismissButton = { TextButton(onClick = { viewModel.hideConfirm() }) { Text("Отмена") } }
            )
        }
    }
}