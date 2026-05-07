package com.example.passpoint.presentation.screens.main.admin

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.theme.Brand50
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray500
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.theme.White
import com.example.passpoint.presentation.viewModel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: UsersViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
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
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SpacerHeight(8)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Все пользователи системы", style = MaterialTheme.typography.headlineSmall)
                    SpacerHeight(16)
                    Button(
                        onClick = { showBottomSheet = true },
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Посмотреть статистику",
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
                    SpacerHeight(12)

                    // Фильтр роли
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = state.selectedRoleFilter == null,
                                onClick = { viewModel.setRoleFilter(null) },
                                label = { Text("Все") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Brand50,
                                    selectedLabelColor = BrandColor
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.selectedRoleFilter == 1,
                                onClick = { viewModel.setRoleFilter(1) },
                                label = { Text("Участники") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Brand50,
                                    selectedLabelColor = BrandColor
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.selectedRoleFilter == 2,
                                onClick = { viewModel.setRoleFilter(2) },
                                label = { Text("Кураторы") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Brand50,
                                    selectedLabelColor = BrandColor
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.selectedRoleFilter == 3,
                                onClick = { viewModel.setRoleFilter(3) },
                                label = { Text("Администраторы") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Brand50,
                                    selectedLabelColor = BrandColor
                                )
                            )
                        }
                    }
                    SpacerHeight(12)
                    Row(verticalAlignment = Alignment.Bottom) {
                        // Поиск
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            label = { Text("Поиск") },
                            trailingIcon = {
                                if (state.searchQuery.isNotEmpty()) {
                                    IconButton (onClick = { viewModel.updateSearchQuery("") }) {
                                        Icon(
                                            painter = painterResource(R.drawable.close_24dp),
                                            contentDescription = "Очистить поиск"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f),
                            shape = MaterialTheme.shapes.small,
                            colors = textFieldColors
                        )
                        SpacerWidth(12)
                        OutlinedButton(
                            onClick = { viewModel.toggleSort() },
                            modifier = Modifier.width(100.dp).height(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (state.sortAlphabetically) "А-Я" else "По дате",
                                style = MaterialTheme.typography.displaySmall
                            )
                        }
                    }
                }
            }
            val listState = rememberLazyListState()
            LaunchedEffect(state.sortAlphabetically) {
                listState.animateScrollToItem(0)
            }
            SpacerHeight(4)
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                items(state.filteredUsers, key = { it.id.toString() }) { user ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        val dateOnly = user.created_at?.substringBefore("T")
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row {
                                Text(
                                    "${user.name} ${user.surname}",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp),
                                    modifier = Modifier.weight(1f)
                                )
                                dateOnly?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
                                        color = Gray600
                                    )
                                }
                            }
                            Text(
                                user.email,
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
                                color = Gray600
                            )
                            SpacerHeight(8)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Роль",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Gray500
                                )
                                Text(
                                    roleToString(user.role),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (!user.phone.isNullOrBlank()) {
                                SpacerHeight(4)
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Мобильный телефон",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray500
                                    )
                                    Text(
                                        user.phone,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            if (user.role == 1 && !user.organization.isNullOrBlank()) {
                                SpacerHeight(4)
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Организация",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray500
                                    )
                                    Text(
                                        user.organization,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            SpacerHeight(16)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.showDeleteDialog(user.id.toString()) },
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(ButtonHeight),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Удалить", style = MaterialTheme.typography.displaySmall)
                                }
                                Button(
                                    onClick = { viewModel.showRoleDialog(user.id.toString()) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(ButtonHeight),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Сменить роль",
                                        color = White,
                                        style = MaterialTheme.typography.displaySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Модальный лист со статистикой
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                StatsSheetContent(state)
            }
        }

        // Диалоги удаления и смены роли (без изменений)
        if (state.deleteDialogUserId != null) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteDialog() },
                title = { Text("Удаление пользователя") },
                text = { Text("Вы уверены?") },
                confirmButton = { TextButton(onClick = { viewModel.confirmDeleteUser() }) { Text("Удалить") } },
                dismissButton = { TextButton(onClick = { viewModel.hideDeleteDialog() }) { Text("Отмена") } }
            )
        }
        if (state.roleDialogUserId != null) {
            AlertDialog(
                onDismissRequest = { viewModel.hideRoleDialog() },
                title = { Text("Назначить роль") },
                text = {
                    Column {
                        listOf(
                            1 to "Участник",
                            2 to "Куратор",
                            3 to "Администратор"
                        ).forEach { (role, name) ->
                            SpacerHeight(12)
                            Row(
                                Modifier.selectable(
                                    selected = state.selectedRoleForDialog == role,
                                    onClick = { viewModel.selectRoleForDialog(role) }
                                ),
                                verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = state.selectedRoleForDialog == role,
                                    onClick = null,
                                    modifier = Modifier.size(30.dp)
                                )
                                SpacerWidth(8)
                                Text(name, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.confirmRoleChange() },
                        enabled = state.selectedRoleForDialog != null
                    ) { Text("Назначить") }
                },
                dismissButton = { TextButton(onClick = { viewModel.hideRoleDialog() }) { Text("Отмена") } }
            )
        }
    }
}

fun roleToString(role: Int): String = when (role) {
    1 -> "Участник"
    2 -> "Куратор"
    3 -> "Администратор"
    else -> "Неизв."
}

@Composable
fun StatsSheetContent(state: UsersState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("Общая статистика", style = MaterialTheme.typography.headlineSmall)
        SpacerHeight(16)
        Text("Всего пользователей: ${state.statsTotal}")
        SpacerHeight(8)
        Text("Участников: ${state.statsByRole[1] ?: 0}")
        SpacerHeight(8)
        Text("Кураторов: ${state.statsByRole[2] ?: 0}")
        SpacerHeight(8)
        Text("Администраторов: ${state.statsByRole[3] ?: 0}")
        SpacerHeight(12)
        Text("Новых за последнюю неделю: ${state.statsNewThisWeek}")
        SpacerHeight(24)
    }
}