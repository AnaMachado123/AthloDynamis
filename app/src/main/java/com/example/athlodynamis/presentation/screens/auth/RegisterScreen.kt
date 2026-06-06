package com.example.athlodynamis.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius

@Composable
fun RegisterScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRegisterClick: (
        name: String,
        email: String,
        password: String,
        shirtNumber: Int,
        position: String
    ) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var shirtNumber by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var shirtNumberError by remember { mutableStateOf<String?>(null) }
    var positionError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        nameError = if (name.isBlank()) "Nome obrigatório" else null

        emailError = when {
            email.isBlank() -> "Email obrigatório"
            !email.contains("@") -> "Email inválido. O email deve conter @"
            else -> null
        }

        passwordError = when {
            password.isBlank() -> "Password obrigatória"
            password.length < 6 -> "Password mínimo 6 caracteres"
            else -> null
        }

        shirtNumberError = when {
            shirtNumber.isBlank() -> "Número da camisola obrigatório"
            (shirtNumber.toIntOrNull() ?: 0) !in 1..99 -> "Número entre 1 e 99"
            else -> null
        }

        positionError = if (position.isBlank()) "Posição obrigatória" else null

        return nameError == null &&
                emailError == null &&
                passwordError == null &&
                shirtNumberError == null &&
                positionError == null
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AthloColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegisterHeroCard()

            Spacer(modifier = Modifier.height(26.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AthloRadius.ExtraLarge),
                colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Criar conta",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AthloColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Cria a tua conta de jogador",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AthloColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    RegisterFieldLabel("NOME")
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                        },
                        placeholder = { Text("Insira o seu nome") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Nome",
                                tint = AthloColors.TextMuted
                            )
                        },
                        isError = nameError != null,
                        supportingText = {
                            nameError?.let {
                                Text(text = it, color = Color(0xFFC83755))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RegisterFieldLabel("EMAIL")
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        placeholder = { Text("Insira o seu email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Mail,
                                contentDescription = "Email",
                                tint = AthloColors.TextMuted
                            )
                        },
                        isError = emailError != null,
                        supportingText = {
                            emailError?.let {
                                Text(text = it, color = Color(0xFFC83755))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RegisterFieldLabel("PASSWORD")
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        placeholder = { Text("Insira a sua palavra-passe") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = AthloColors.TextMuted
                            )
                        },
                        isError = passwordError != null,
                        supportingText = {
                            passwordError?.let {
                                Text(text = it, color = Color(0xFFC83755))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(18.dp),
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RegisterFieldLabel("NÚMERO DA CAMISOLA")
                    OutlinedTextField(
                        value = shirtNumber,
                        onValueChange = { value ->
                            shirtNumber = value.filter { it.isDigit() }
                            shirtNumberError = null
                        },
                        placeholder = { Text("Insira o número da camisola") },
                        isError = shirtNumberError != null,
                        supportingText = {
                            shirtNumberError?.let {
                                Text(text = it, color = Color(0xFFC83755))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = registerTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RegisterFieldLabel("POSIÇÃO")
                    OutlinedTextField(
                        value = position,
                        onValueChange = {
                            position = it
                            positionError = null
                        },
                        placeholder = { Text("Insira a sua posição") },
                        isError = positionError != null,
                        supportingText = {
                            positionError?.let {
                                Text(text = it, color = Color(0xFFC83755))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = registerTextFieldColors()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = errorMessage,
                            color = Color(0xFFC83755),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            if (validateForm()) {
                                onRegisterClick(
                                    name.trim(),
                                    email.trim(),
                                    password,
                                    shirtNumber.toIntOrNull() ?: 0,
                                    position.trim()
                                )
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AthloColors.Blue,
                            disabledContainerColor = Color(0xFF9CA3AF)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Criar Conta",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Já tens conta? Iniciar sessão",
                            color = AthloColors.Blue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisterFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = AthloColors.TextSecondary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun RegisterHeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(AthloColors.Blue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AthloDynamis",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Cria a tua conta e começa a competir",
                color = Color(0xFFC8DCEF),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun registerTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AthloColors.Blue,
    unfocusedBorderColor = Color(0xFFE5E7EB),
    errorBorderColor = Color(0xFFC83755),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorContainerColor = Color.White,
    cursorColor = AthloColors.Blue,
    focusedTextColor = AthloColors.TextPrimary,
    unfocusedTextColor = AthloColors.TextPrimary,
    focusedPlaceholderColor = AthloColors.TextMuted,
    unfocusedPlaceholderColor = AthloColors.TextMuted
)