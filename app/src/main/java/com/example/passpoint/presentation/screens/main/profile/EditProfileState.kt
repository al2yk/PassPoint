package com.example.passpoint.presentation.screens.main.profile

import android.net.Uri

data class EditProfileState(
    val userId: String = "",
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val organization: String = "",
    val role: Int = 1,
    val photoUrl: String? = null,
    val newPhotoUri: Uri? = null,
    val isUploadingPhoto: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val phoneError: String? = null
)