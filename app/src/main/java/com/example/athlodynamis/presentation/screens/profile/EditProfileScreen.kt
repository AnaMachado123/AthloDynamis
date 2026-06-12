package com.example.athlodynamis.presentation.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.data.repository.OfflineProfileUpdatePayload
import com.example.athlodynamis.data.repository.OfflineSyncRepository
import com.example.athlodynamis.presentation.components.AthloBackButton
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.viewmodel.OfflineViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun EditProfileScreen(
    navController: NavController,
    userId: String,
    userName: String,
    userEmail: String,
    userPassword: String,
    onSaveClick: (
        name: String,
        email: String,
        password: String
    ) -> Unit,
    onOfflineSaveClick: (
        name: String,
        email: String,
        password: String
    ) -> Unit,
    onPhotoSelected: (
        userId: String,
        imageBytes: ByteArray
    ) -> Unit
) {
    var name by remember { mutableStateOf(userName) }
    var email by remember { mutableStateOf(userEmail) }
    var password by remember { mutableStateOf(userPassword) }

    var localMessage by remember { mutableStateOf<String?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val offlineViewModel: OfflineViewModel = viewModel()
    val isOnline by offlineViewModel.isOnline.collectAsState()

    val offlineSyncRepository = remember {
        OfflineSyncRepository(context)
    }

    val json = remember {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    val photoOfflineError = stringResource(R.string.edit_photo_offline_error)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        uri?.let {
            val bytes = context.contentResolver
                .openInputStream(it)
                ?.use { inputStream ->
                    inputStream.readBytes()
                }

            if (bytes != null) {
                if (isOnline) {
                    onPhotoSelected(userId, bytes)
                } else {
                    localError = photoOfflineError
                    localMessage = null
                }
            }
        }
    }

    Scaffold(
        containerColor = AthloColors.Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            EditProfileHeader(
                userName = name,
                onBackClick = {
                    navController.popBackStack()
                }
            )

            if (!isOnline) {
                OfflineEditProfileCard()
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AthloRadius.ExtraLarge),
                colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.edit_profile_title),
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.edit_profile_subtitle),
                        color = AthloColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    FieldLabel(stringResource(R.string.edit_name_label))

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            localMessage = null
                            localError = null
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = profileTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.edit_email_label))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            localMessage = null
                            localError = null
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = profileTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.edit_password_label))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            localMessage = null
                            localError = null
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = profileTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    FieldLabel(stringResource(R.string.edit_photo_label))

                    UploadPhotoButton(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        }
                    )

                    localMessage?.let { message ->
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = message,
                            color = Color(0xFF3F7A28),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    localError?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = error,
                            color = Color(0xFFC83755),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            val nameEmptyError = stringResource(R.string.edit_name_empty_error)
            val emailInvalidError = stringResource(R.string.edit_email_invalid_error)
            val passwordMinError = stringResource(R.string.edit_password_min_error)
            val savedOfflineMessage = stringResource(R.string.edit_saved_offline)
            val saveErrorMessage = stringResource(R.string.edit_save_error)

            Button(
                onClick = {
                    val newName = name.trim()
                    val newEmail = email.trim()
                    val newPassword = password

                    localMessage = null
                    localError = null

                    if (newName.isBlank()) {
                        localError = nameEmptyError
                        return@Button
                    }

                    if (newEmail.isBlank() || !newEmail.contains("@")) {
                        localError = emailInvalidError
                        return@Button
                    }

                    if (newPassword.isBlank() || newPassword.length < 6) {
                        localError = passwordMinError
                        return@Button
                    }

                    if (isOnline) {
                        onSaveClick(
                            newName,
                            newEmail,
                            newPassword
                        )
                    } else {
                        coroutineScope.launch {
                            try {
                                val payload = OfflineProfileUpdatePayload(
                                    userId = userId,
                                    name = newName,
                                    email = newEmail,
                                    password = newPassword
                                )

                                offlineSyncRepository.savePendingOperation(
                                    operationType = "UPDATE_USER_PROFILE",
                                    entityName = "USER_PROFILE",
                                    payloadJson = json.encodeToString(payload)
                                )

                                onOfflineSaveClick(
                                    newName,
                                    newEmail,
                                    newPassword
                                )

                                offlineViewModel.refreshPendingOperationsCount()

                                localMessage = savedOfflineMessage
                            } catch (e: Exception) {
                                localError = e.message ?: saveErrorMessage
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.Blue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(R.string.edit_update_cd),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isOnline) {
                        stringResource(R.string.edit_update_button)
                    } else {
                        stringResource(R.string.edit_save_offline_button)
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = stringResource(R.string.edit_cancel),
                    color = AthloColors.TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditProfileHeader(
    userName: String,
    onBackClick: () -> Unit
) {
    val initials = userName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthloColors.Navy)
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Column {
                AthloBackButton(
                    onClick = {
                        onBackClick()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.profile_title),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.edit_profile_header_subtitle),
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(AthloColors.Blue, CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials.ifBlank { "U" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OfflineEditProfileCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7CC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SignalWifiOff,
                contentDescription = stringResource(R.string.cd_offline),
                tint = Color(0xFF7A5B00),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.home_offline),
                    color = Color(0xFF7A5B00),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.edit_offline_desc),
                    color = Color(0xFF9A7800),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun UploadPhotoButton(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AthloColors.SoftBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        RowContent(
            onClick = onClick
        )
    }
}

@Composable
private fun RowContent(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(AthloColors.Blue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.edit_photo_cd),
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                text = stringResource(R.string.edit_upload_photo),
                color = AthloColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.edit_upload_photo_subtitle),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Icon(
            imageVector = Icons.Default.Upload,
            contentDescription = stringResource(R.string.edit_upload_photo_cd),
            tint = AthloColors.Blue
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = AthloColors.TextPrimary,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AthloColors.Blue,
    unfocusedBorderColor = Color(0xFFE5E7EB),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = AthloColors.Blue,
    focusedTextColor = AthloColors.TextPrimary,
    unfocusedTextColor = AthloColors.TextPrimary
)