package com.example.passpoint.presentation.screens.main.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
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
import com.example.passpoint.presentation.components.AboutProgramDialog
import com.example.passpoint.presentation.components.LogOutDialog
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.SpacerWidth
import com.example.passpoint.presentation.components.WarningMessage
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.AppTheme
import com.example.passpoint.presentation.theme.Brand50
import com.example.passpoint.presentation.theme.BrandColor
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray350
import com.example.passpoint.presentation.theme.Gray400
import com.example.passpoint.presentation.theme.Gray500
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.theme.Gray800
import com.example.passpoint.presentation.viewModel.ProfileViewModel

@Composable
fun ProfileView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val currentTheme by viewModel.currentTheme.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val role = when (state.role) {
        1 -> "Участник"
        2 -> "Куратор"
        3 -> "Администратор"
        else -> "Участник"
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onScreenResumed()
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
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    Text(text = state.error ?: "Ошибка", style = MaterialTheme.typography.bodyLarge)
                    SpacerHeight(16)
                    OutlinedButton(
                        onClick = { viewModel.retry() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .height(ButtonHeight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Повторить")
                    }
                }
            }

            else -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .verticalScroll(rememberScrollState())
                ) {
                    SpacerHeight(0)
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        //Фото
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 18.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Gray350)
                                ) {
                                    Log.e("photo", state.photo)
                                    // Фото пользователя
                                    val imgState = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(state.photo)
                                            .size(Size.ORIGINAL).build()
                                    ).state
                                    if (imgState is AsyncImagePainter.State.Error) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.sentiment_very_satisfied_24dp),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .fillMaxSize(0.85f)
                                                .align(Alignment.Center),
                                            tint = Gray800
                                        )
                                    }
                                    if (imgState is AsyncImagePainter.State.Success) {
                                        Image(
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .clip(RoundedCornerShape(15.dp)),
                                            painter = imgState.painter,
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { controller.navigate(NavigationRoutes.EDIT_PROFILE) },
                                    modifier = Modifier
                                        .size(30.dp)
                                        .align(Alignment.TopEnd),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Brand50
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.edit_24dp),
                                        contentDescription = null,
                                        tint = BrandColor,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                            SpacerHeight(6)
                            Text(
                                text = "${state.name} ${state.surname}",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                            if (state.phone.isBlank() || (state.organization.isBlank() && role == "Участник")) {
                                SpacerHeight(12)
                                WarningMessage(text = "Заполните все поля в профиле")
                            }
                        }

                        Row(modifier = Modifier.padding(start = 16.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.badge_24dp),
                                contentDescription = null,
                                tint = Gray500,
                                modifier = Modifier.size(20.dp)
                            )

                            SpacerWidth(12)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Статус",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Gray500
                                )
                                Text(
                                    role,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                        SpacerHeight(15)
                        Row(modifier = Modifier.padding(start = 16.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.mail_24dp),
                                contentDescription = null,
                                tint = Gray500,
                                modifier = Modifier.size(20.dp)
                            )

                            SpacerWidth(12)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Email",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Gray500
                                )
                                Text(
                                    state.email,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        if (role == "Участник") {
                            SpacerHeight(15)
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable { controller.navigate(NavigationRoutes.CERTIFICATES) },
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Icon(
                                        painter = painterResource(R.drawable.article_24dp),
                                        contentDescription = null,
                                        tint = Gray500,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    SpacerWidth(12)
                                    Column {
                                        Text(
                                            "Мои сертификаты",
                                            style = MaterialTheme.typography.displaySmall,
                                            color = Gray500
                                        )
                                        Text(
                                            "${state.certificatesCount}",
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                }
                                Icon(
                                    painter = painterResource(R.drawable.arrow_right_24dp),
                                    contentDescription = "",
                                    tint = Gray600,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        if (role == "Куратор") {
                            SpacerHeight(15)
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable { controller.navigate(NavigationRoutes.CURATOR_PAST_COURSES) },
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Icon(
                                        painter = painterResource(R.drawable.person_book_24dp),
                                        contentDescription = null,
                                        tint = Gray500,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    SpacerWidth(12)
                                    Column {
                                        Text(
                                            "Мои прошедшие курсы",
                                            style = MaterialTheme.typography.displaySmall,
                                            color = Gray500
                                        )
                                        Text(
                                            state.pastCoursesCount.toString(),
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                }
                                Icon(
                                    painter = painterResource(R.drawable.arrow_right_24dp),
                                    contentDescription = "",
                                    tint = Gray600,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        if (state.phone.isNotEmpty()) {
                            SpacerHeight(15)
                            Row(modifier = Modifier.padding(start = 16.dp)) {
                                Icon(
                                    painter = painterResource(R.drawable.contact_phone_24dp),
                                    contentDescription = null,
                                    tint = Gray500,
                                    modifier = Modifier.size(20.dp)
                                )

                                SpacerWidth(12)
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Мобильный телефон",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray500
                                    )
                                    Text(
                                        state.phone,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                        if (state.organization.isNotEmpty() && role == "Участник") {
                            SpacerHeight(15)
                            Row(modifier = Modifier.padding(start = 16.dp)) {
                                Icon(
                                    painter = painterResource(R.drawable.home_work_24dp),
                                    contentDescription = null,
                                    tint = Gray500,
                                    modifier = Modifier.size(20.dp)
                                )

                                SpacerWidth(12)
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Организация",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Gray500
                                    )
                                    Text(
                                        state.organization,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                        SpacerHeight(16)
                    }
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Тема", style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 10.dp, start = 20.dp)
                        )
                        SpacerHeight(16)
                        AppTheme.entries.forEach { themeOption ->
                            val title = when (themeOption) {
                                AppTheme.SYSTEM -> "Как в системе"
                                AppTheme.LIGHT -> "Светлая"
                                AppTheme.DARK -> "Тёмная"
                            }
                            val interactionSource = remember { MutableInteractionSource() }
                            val context = LocalContext.current
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(ButtonHeight)
                                    .selectable(
                                        selected = (currentTheme == themeOption),
                                        onClick = { viewModel.onThemeSelected(themeOption,context) },
                                        role = Role.RadioButton,
                                        interactionSource = interactionSource,
                                        indication = ripple(color = MaterialTheme.colorScheme.primary)
                                    )
                                    .padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (currentTheme == themeOption),
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        unselectedColor = Gray400
                                    )
                                )
                                SpacerWidth(10)
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        SpacerHeight(10)
                    }

                    ElevatedCard(
                        modifier = Modifier.height(100.dp),
                    ) {
                        Button(
                            onClick = { showAboutDialog = true }, shape = RectangleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ButtonHeight),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp)

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.info_24dp),
                                    contentDescription = "",
                                    modifier = Modifier.size(20.dp),
                                    tint = Gray500
                                )
                                SpacerWidth(12)
                                Text(
                                    text = "О программе",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Button(
                            onClick = { showLogoutDialog = true }, shape = RectangleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ButtonHeight),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp)

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.logout_24dp),
                                    contentDescription = "",
                                    modifier = Modifier.size(20.dp),
                                    tint = BrandColor
                                )
                                SpacerWidth(12)
                                Text(
                                    text = "Выйти",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BrandColor
                                )
                            }
                        }
                    }
                    SpacerHeight(8)
                    if (showAboutDialog) {
                        AboutProgramDialog(onDismiss = { showAboutDialog = false })
                    }
                    if (showLogoutDialog) {
                        LogOutDialog(
                            onDismiss = { showLogoutDialog = false },
                            onConfirm = {
                                showLogoutDialog = false
                                UserRepository.act = 1
                                controller.navigate(NavigationRoutes.SIGNIN) { popUpTo(0) }
                            }
                        )
                    }
                }
            }
        }
    }
}
