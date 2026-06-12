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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
                    localError =
                        "A alteração de foto precisa de internet. Para já, só o nome/email/password podem ser guardados offline."
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
                        text = "Editar Perfil",
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Altera as tuas informações",
                        color = AthloColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    FieldLabel("Nome")

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

                    FieldLabel("Email")

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

                    FieldLabel("Password")

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

                    FieldLabel("Foto")

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

            Button(
                onClick = {
                    val newName = name.trim()
                    val newEmail = email.trim()
                    val newPassword = password

                    localMessage = null
                    localError = null

                    if (newName.isBlank()) {
                        localError = "O nome não pode estar vazio."
                        return@Button
                    }

                    if (newEmail.isBlank() || !newEmail.contains("@")) {
                        localError = "Insere um email válido."
                        return@Button
                    }

                    if (newPassword.isBlank() || newPassword.length < 6) {
                        localError = "A password deve ter pelo menos 6 caracteres."
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

                                localMessage =
                                    "Alteração guardada offline. Será sincronizada quando voltares a ter internet."
                            } catch (e: Exception) {
                                localError =
                                    e.message ?: "Erro ao guardar alteração offline."
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
                    contentDescription = "Atualizar perfil",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isOnline) {
                        "Atualizar Perfil"
                    } else {
                        "Guardar offline"
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
                    text = "Cancelar",
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
                    text = "Perfil",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Editar informações pessoais",
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
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SignalWifiOff,
                contentDescription = "Sem internet",
                tint = Color(0xFF7A5B00),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "SEM LIGAÇÃO À INTERNET",
                    color = Color(0xFF7A5B00),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "As alterações serão guardadas no dispositivo e sincronizadas quando a internet voltar.",
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
    androidx.compose.foundation.layout.Row(
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
                contentDescription = "Foto",
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
                text = "Carregar nova foto",
                color = AthloColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Funcionalidade preparada para implementação futura",
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Icon(
            imageVector = Icons.Default.Upload,
            contentDescription = "Carregar foto",
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