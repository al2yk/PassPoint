package com.example.passpoint.presentation.screens.main.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.passpoint.R
import com.example.passpoint.data.dto.NewsCategory
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.utils.formatNewsDate
import com.example.passpoint.presentation.components.CategoryBlock
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.Gray800
import com.example.passpoint.presentation.ui.theme.White
import com.example.passpoint.presentation.viewModel.NewsViewModel

@Composable
fun NewsView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val state = viewModel.state

    val displayCategories = listOf(NewsCategory(0, "Все")) + state.category

    val filteredNews = if (state.selectedCategoryId == 0) {
        state.news
    } else {
        state.news.filter { it.news_category == state.selectedCategoryId }
    }

    Box(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
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
                                "Повторить",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SpacerHeight(8)
                    ElevatedCard(modifier = Modifier.padding(horizontal = 4.dp),shape = (RoundedCornerShape(20.dp))) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(displayCategories,key = { it.id }) { category ->
                                    val isSelected = category.id == state.selectedCategoryId
                                    CategoryBlock(
                                        isSelected = isSelected,
                                        title = category.name
                                    ) {
                                        viewModel.selectCategory(category.id)
                                    }
                                }
                            }
                        }
                    }
                    SpacerHeight(8)
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )  {
                        if (filteredNews.isEmpty()) {
                            Text(
                                text = "Новостей в этой категории пока нет",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray600,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        } else {
                            filteredNews.forEach { news ->
                                ElevatedCard(
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.padding(horizontal = 4.dp).clickable {
                                        controller.navigate("${NavigationRoutes.NEWS_DETAIL}?newsId=${news.id}")
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .height(120.dp)
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(news.photo)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(120.dp)
                                                    .clip(RoundedCornerShape(20.dp)),
                                                contentScale = ContentScale.Crop,
                                                placeholder = painterResource(R.drawable.nophoto),
                                                error = painterResource(R.drawable.nophoto)
                                            )
                                        }
                                        SpacerWidth(12)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                        ) {
                                            Text(
                                                text = news.title,
                                                style = MaterialTheme.typography.titleLarge,
                                                maxLines = 3,
                                                softWrap = true,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = formatNewsDate(news.create),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Gray600,
                                                modifier = Modifier.align(Alignment.BottomStart)
                                            )
                                        }
                                    }
                                    SpacerHeight(8)
                                    if (UserRepository.role == 3) {
                                        SpacerHeight(8)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { viewModel.showDeleteConfirm(news.id) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(ButtonHeight),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    "Удалить",
                                                    style = MaterialTheme.typography.displaySmall
                                                )
                                            }
                                            Button(
                                                onClick = {
                                                    controller.navigate("edit_news/${news.id}")
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(ButtonHeight),
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
                                        SpacerHeight(8)
                                    }
                                }
                                SpacerHeight(8)
                            }
                        }
                    }
                }
            }
        }
        if (state.deleteDialog != null) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteDialog() },
                title = { Text("Удаление новости") },
                text = { Text("Вы уверены, что хотите удалить новость?", color = Gray800) },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmDeleteAction() }) { Text("Удалить") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideDeleteDialog() }) { Text("Отмена") }
                }
            )
        }
    }
}