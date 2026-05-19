package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.utils.formatDateRu
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.White

@Composable
fun CourseCard(
    course: CourseWithEnrollment,
    isRegistered: Boolean,
    isRegistrationLoading: Boolean,
    onRegisterClick: () -> Unit,
    onUnregisterClick: () -> Unit,
    showButtons: Boolean = true,
    showCapacity: Boolean = true,
    onQrClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    curatorName: String = "",
    modifier: Modifier = Modifier
) {
    Text(course.name, style = MaterialTheme.typography.headlineSmall)
    SpacerHeight(14)
    Text(course.description, style = MaterialTheme.typography.displaySmall, color = Gray600)
    SpacerHeight(14)
    Text("Дата проведения", style = MaterialTheme.typography.displaySmall, color = Gray600)
    Text(text = formatDateRu(course.date), style = MaterialTheme.typography.bodyLarge)
    SpacerHeight(7)
    Text("Место проведения", style = MaterialTheme.typography.displaySmall, color = Gray600)
    Text(course.place, style = MaterialTheme.typography.bodyLarge)

    if (showCapacity) {
        SpacerHeight(7)
        Text("Количество мест", style = MaterialTheme.typography.displaySmall, color = Gray600)
        Text(
            "Осталось: ${course.capacity - course.enrolled_count}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    if (curatorName.isNotEmpty()) {
        SpacerHeight(7)
        Text("Куратор", style = MaterialTheme.typography.displaySmall, color = Gray600)
        Text(
            text = curatorName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    if (course.photo != null) {
        SpacerHeight(12)
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                .data(course.photo)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(15.dp)),
            contentScale = ContentScale.Crop
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is AsyncImagePainter.State.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ошибка загрузки")
                    }
                }

                else -> SubcomposeAsyncImageContent()
            }
        }
    }
    if (showButtons && UserRepository.role == 1) {
        if (isRegistered) {
            SpacerHeight(16)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onUnregisterClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(ButtonHeight),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isRegistrationLoading
                ) {
                    Text("Отменить", style = MaterialTheme.typography.displaySmall)
                }
                Button(
                    onClick = { onQrClick?.invoke() },
                    modifier = Modifier
                        .weight(1f)
                        .height(ButtonHeight),
                    enabled = !isRegistrationLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Показать QR",
                        color = White,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }
        } else {
            SpacerHeight(16)
            val noPlacesLeft = (course.capacity - course.enrolled_count) <= 0
            if (noPlacesLeft) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ButtonHeight),
                    enabled = false,
                    onClick = {},
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Нет мест",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ButtonHeight),
                    enabled = !isRegistrationLoading,
                    onClick = onRegisterClick,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Участвовать",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
    if (showButtons && UserRepository.role == 3) {
        SpacerHeight(16)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onDeleteClick?.invoke() },
                modifier = Modifier
                    .weight(1f)
                    .height(ButtonHeight),
                shape = RoundedCornerShape(8.dp),
                enabled = !isRegistrationLoading
            ) {
                Text("Удалить", style = MaterialTheme.typography.displaySmall)
            }
            Button(
                onClick = { onEditClick?.invoke() },
                modifier = Modifier
                    .weight(1f)
                    .height(ButtonHeight),
                enabled = !isRegistrationLoading,
                colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Редактировать",
                    color = White,
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }
}
