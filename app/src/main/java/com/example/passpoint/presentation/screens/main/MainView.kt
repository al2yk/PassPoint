package com.example.passpoint.presentation.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.viewModel.MainViewModel

@Composable
fun MainView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Box(
        modifier = Modifier
            .padding(innerPadding)
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
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    SpacerHeight(8)
                    val new = state.news.lastOrNull()
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
                                            .fillMaxWidth(1f)
                                            .height(220.dp)
                                            .clip(RoundedCornerShape(15.dp)),
                                        painter = imgState.painter,
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                SpacerHeight(5)
                                Button(
                                    onClick = { controller.navigate(NavigationRoutes.NEWS) },
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    shape = RoundedCornerShape(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    )
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "Посмотреть все новости",
                                            modifier = Modifier.weight(1f),
                                            color = BrandColor
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
                            }
                        }
                    }
                }
            }
        }
    }
}