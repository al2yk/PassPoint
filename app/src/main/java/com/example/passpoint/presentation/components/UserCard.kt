package com.example.passpoint.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import coil.size.Size
import com.example.passpoint.R
import com.example.passpoint.data.dto.User
import com.example.passpoint.presentation.screens.main.admin.roleToString
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray350
import com.example.passpoint.presentation.ui.theme.Gray500
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.White
import com.example.passpoint.presentation.viewModel.UsersViewModel

@Composable
fun UserCard(
    user: User,
    viewModel: UsersViewModel,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Фото и имя
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Gray350)
                ) {
                    val context = LocalContext.current
                    val imageRequest = remember(user.photo) {
                        ImageRequest.Builder(context)
                            .data(user.photo)
                            .size(Size.ORIGINAL)
                            .build()
                    }
                    SubcomposeAsyncImage(
                        model = imageRequest,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(30.dp))
                                }
                            }
                            is AsyncImagePainter.State.Error -> {
                                Icon(
                                    painter = painterResource(R.drawable.person_24dp),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(0.6f).align(Alignment.Center),
                                    tint = Gray600
                                )
                            }
                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${user.name} ${user.surname}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        user.email,
                        style = MaterialTheme.typography.displaySmall,
                        color = Gray600
                    )
                }
            }

            // Роль
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Роль", style = MaterialTheme.typography.displaySmall, color = Gray500)
                Text(
                    roleToString(user.role),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Телефон
            if (!user.phone.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Мобильный телефон", style = MaterialTheme.typography.displaySmall, color = Gray500)
                    Text(user.phone, style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Организация
            if (user.role == 1 && !user.organization.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Организация", style = MaterialTheme.typography.displaySmall, color = Gray500)
                    Text(user.organization, style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Кнопки
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.showDeleteDialog(user.id.toString()) },
                    modifier = Modifier.width(120.dp).height(ButtonHeight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Удалить", style = MaterialTheme.typography.displaySmall)
                }
                Button(
                    onClick = { viewModel.showRoleDialog(user.id.toString()) },
                    modifier = Modifier.weight(1f).height(ButtonHeight),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Сменить роль", color = White, style = MaterialTheme.typography.displaySmall)
                }
            }
        }
    }
}