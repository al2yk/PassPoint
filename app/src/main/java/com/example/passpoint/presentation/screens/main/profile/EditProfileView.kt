package com.example.passpoint.presentation.screens.main.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.passpoint.R
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.components.WarningMessage
import com.example.passpoint.presentation.ui.theme.BrandColor
import com.example.passpoint.presentation.ui.theme.ButtonHeight
import com.example.passpoint.presentation.ui.theme.Gray350
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.Gray800
import com.example.passpoint.presentation.ui.theme.White
import com.example.passpoint.presentation.viewModel.EditProfileEvent
import com.example.passpoint.presentation.viewModel.EditProfileViewModel

@Composable
fun EditProfileView(
    controller: NavHostController,
    innerPadding: PaddingValues,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> viewModel.onPhotoPicked(uri) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is EditProfileEvent.Success -> {
                    controller.navigateUp()
                }
            }
        }
    }
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        disabledTextColor = MaterialTheme.colorScheme.onBackground,
        focusedBorderColor = BrandColor,
        unfocusedBorderColor = Gray600,
        disabledBorderColor = Gray600,
        focusedLabelColor = BrandColor,
        unfocusedLabelColor = Gray600,
        disabledLabelColor = Gray600,
        unfocusedSupportingTextColor = Gray600,
        disabledSupportingTextColor = Gray600,
        focusedSupportingTextColor = BrandColor
    )
    val isFormValid = state.name.isNotBlank() && state.surname.isNotBlank() &&
            state.phoneError == null
    Column(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        SpacerHeight(8)
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                SpacerHeight(16)
                // Фото и кнопка смены
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Gray350)
                    ) {
                        val imageModel = state.newPhotoUri ?: state.photoUrl
                        if (imageModel != null) {
                            AsyncImage(
                                model = imageModel,
                                contentDescription = "Фото профиля",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.sentiment_very_satisfied_24dp),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(0.7f)
                                    .align(Alignment.Center),
                                tint = Gray800
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest.Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    .build()
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit_24dp),
                            contentDescription = "Сменить фото",
                            tint = BrandColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                SpacerHeight(8)
                if (state.photoUrl != null || state.newPhotoUri != null) {
                    TextButton(
                        onClick = { viewModel.removePhoto() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Убрать фото", color = BrandColor)
                    }
                }

                SpacerHeight(16)

                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Имя") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = MaterialTheme.shapes.small,
                    colors = textFieldColors
                )
                SpacerHeight(12)
                OutlinedTextField(
                    value = state.surname,
                    onValueChange = { viewModel.updateSurname(it) },
                    label = { Text("Фамилия") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    shape = MaterialTheme.shapes.small,
                    colors = textFieldColors

                )
                SpacerHeight(12)
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = { viewModel.updatePhone(it) },
                    label = { Text("Телефон") },
                    isError = state.phoneError != null,
                    supportingText = state.phoneError?.let { { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    shape = MaterialTheme.shapes.small,
                    colors = textFieldColors
                )
                if (state.role == 1) {
                    SpacerHeight(12)
                    OutlinedTextField(
                        value = state.organization,
                        onValueChange = { viewModel.updateOrganization(it) },
                        label = { Text("Организация") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        shape = MaterialTheme.shapes.small,
                        colors = textFieldColors
                    )
                }
                SpacerHeight(24)

                if (state.error != null) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        WarningMessage(text = state.error)
                    }
                    SpacerHeight (8)
                }

                Button(
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(ButtonHeight),
                    enabled = !state.isSaving && isFormValid,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColor)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "Сохранить",
                            style = MaterialTheme.typography.displaySmall,
                            color = White
                        )
                    }
                }
                SpacerHeight(16)
            }
        }
    }
}