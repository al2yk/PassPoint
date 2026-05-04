package com.example.passpoint.presentation.screens.main.news

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.passpoint.R
import com.example.passpoint.data.dto.NewsCategory
import com.example.passpoint.domain.utils.formatNewsDate
import com.example.passpoint.presentation.components.CategoryBlock
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray600
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
                    ElevatedCard(shape = (RoundedCornerShape(20.dp))) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(displayCategories) { category ->
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
                    LazyColumn {
                        if (filteredNews.isEmpty()) {
                            item {
                                Text(
                                    text = "Новостей в этой категории пока нет",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Gray600,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        } else {
                            items(filteredNews) { news ->
                                ElevatedCard(
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.clickable {
                                        controller.navigate("${NavigationRoutes.NEWS_DETAIL}?newsId=${news.id}")
                                    }) {
                                    Row(
                                        modifier = Modifier
                                            .height(120.dp)
                                            .fillMaxWidth()
                                    ) {
                                        val painter = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(news.photo)
                                                .crossfade(true)
                                                .build(),
                                            placeholder = painterResource(R.drawable.nophoto),
                                            error = painterResource(R.drawable.nophoto)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                        ) {
                                            Image(
                                                modifier = Modifier.fillMaxSize(),
                                                painter = painter,
                                                contentDescription = "",
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .padding(vertical = 8.dp, horizontal = 12.dp)
                                        ) {
                                            Text(
                                                text = news.title,
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontSize = 14.sp
                                                ),
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
                                }
                                SpacerHeight(8)
                            }
                        }
                    }
                }
            }
        }
    }
}