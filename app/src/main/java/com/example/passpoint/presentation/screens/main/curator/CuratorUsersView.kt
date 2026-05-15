package com.example.passpoint.presentation.screens.main.curator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.passpoint.presentation.screens.main.admin.StatItem
import com.example.passpoint.presentation.screens.main.admin.roleToString
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray350
import com.example.passpoint.presentation.ui.theme.Gray500
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.viewModel.CuratorUsersViewModel

@Composable
fun CuratorUsersView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: CuratorUsersViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> ErrorView(state.error, viewModel::retry)
            else -> Column(modifier = Modifier.fillMaxSize()) {
                SpacerHeight(8)
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Твои участники", style = MaterialTheme.typography.headlineSmall)
                        SpacerHeight(12)
                        Row(verticalAlignment = Alignment.Bottom) {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                label = { Text("Поиск") },
                                trailingIcon = {
                                    if (state.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                            Icon(painter = painterResource(R.drawable.close_24dp), contentDescription = "Очистить")
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.small,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandColor,
                                    unfocusedBorderColor = Gray600
                                )
                            )
                            SpacerWidth(12)
                            OutlinedButton(
                                onClick = { viewModel.toggleSort() },
                                modifier = Modifier.width(100.dp).height(56.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    if (state.sortAscending) "А-Я" else "Я-А",
                                    style = MaterialTheme.typography.displaySmall
                                )
                            }
                        }
                    }
                }
                SpacerHeight(8)

                if (state.filteredUsers.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Нет пользователей", style = MaterialTheme.typography.bodyLarge, color = Gray600)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.filteredUsers) { userInfo ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Фото
                                        Box(
                                            modifier = Modifier
                                                .size(70.dp)
                                                .clip(CircleShape)
                                                .background(Gray350)
                                        ) {
                                            val imgState = rememberAsyncImagePainter(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(userInfo.user.photo)
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
                                                "${userInfo.user.name} ${userInfo.user.surname}",
                                                style = MaterialTheme.typography.headlineSmall
                                            )
                                            Text(
                                                userInfo.user.email,
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
                                            roleToString(userInfo.user.role),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    if (!userInfo.user.phone.isNullOrBlank()) {
                                        SpacerHeight(4)
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                "Мобильный телефон",
                                                style = MaterialTheme.typography.displaySmall,
                                                color = Gray500
                                            )
                                            Text(
                                                userInfo.user.phone,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    if (userInfo.user.role == 1 && !userInfo.user.organization.isNullOrBlank()) {
                                        SpacerHeight(4)
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                "Организация",
                                                style = MaterialTheme.typography.displaySmall,
                                                color = Gray500
                                            )
                                            Text(
                                                userInfo.user.organization,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    SpacerHeight(16)

                                    // Статистика
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                        StatItem(label = "Всего", value = userInfo.totalEnrollments.toString())
                                        StatItem(label = "Посещено", value = userInfo.attended.toString())
                                        StatItem(label = "Пропущено", value = userInfo.missed.toString())
                                    }
                                    SpacerHeight(10)
                                }
                            }
                        }
                        item { SpacerHeight(8) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorView(error: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(error ?: "Ошибка", style = MaterialTheme.typography.bodyLarge)
        SpacerHeight(16)
        OutlinedButton(onClick = onRetry, modifier = Modifier.padding(horizontal = 40.dp).height(ButtonHeight)) {
            Text("Повторить")
        }
    }
}