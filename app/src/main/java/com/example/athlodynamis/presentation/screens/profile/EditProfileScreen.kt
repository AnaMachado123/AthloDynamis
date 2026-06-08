package com.example.athlodynamis.presentation.screens.profile

import android.R.attr.password
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.athlodynamis.presentation.components.AthloBackButton

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
    onPhotoSelected: (
        userId: String,
        imageBytes: ByteArray
    ) -> Unit
){
    var name by remember { mutableStateOf(userName) }
    var email by remember { mutableStateOf(userEmail) }
    var password by remember { mutableStateOf(userPassword) }
    val context = LocalContext.current

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
                onPhotoSelected(userId, bytes)
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
                userName = userName,
                onBackClick = {
                    navController.popBackStack()
                }
            )

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
                        onValueChange = { name = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = profileTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel("Email")

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = profileTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel("Password")

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
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
                }
            }

            Button(
                onClick = {
                    onSaveClick(
                        name.trim(),
                        email.trim(),
                        password
                    )
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
                    text = "Atualizar Perfil",
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