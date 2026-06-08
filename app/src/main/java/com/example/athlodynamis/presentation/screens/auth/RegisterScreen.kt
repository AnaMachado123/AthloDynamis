package com.example.athlodynamis.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius

private const val ACCOUNT_PLAYER = "PLAYER"
private const val ACCOUNT_ORGANIZER = "ORGANIZER"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRegisterClick: (
        name: String,
        email: String,
        password: String,
        accountType: String,
        shirtNumber: Int?,
        position: String?,
        organizerRequestMessage: String?
    ) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var selectedAccountType by remember { mutableStateOf(ACCOUNT_PLAYER) }

    var shirtNumber by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var organizerRequestMessage by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var shirtNumberError by remember { mutableStateOf<String?>(null) }
    var positionError by remember { mutableStateOf<String?>(null) }

    val footballPositions = listOf(
        "Guarda-redes",
        "Defesa central",
        "Lateral direito",
        "Lateral esquerdo",
        "Médio defensivo",
        "Médio centro",
        "Médio ofensivo",
        "Extremo direito",
        "Extremo esquerdo",
        "Avançado",
        "Ponta de lança",
        "Segundo avançado"
    )

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

        if (selectedAccountType == ACCOUNT_PLAYER) {
            shirtNumberError = when {
                shirtNumber.isBlank() -> "Número da camisola obrigatório"
                (shirtNumber.toIntOrNull() ?: 0) !in 1..99 -> "Número entre 1 e 99"
                else -> null
            }

            positionError = if (position.isBlank()) {
                "Escolhe uma posição"
            } else {
                null
            }
        } else {
            shirtNumberError = null
            positionError = null
        }

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
                        text = if (selectedAccountType == ACCOUNT_PLAYER) {
                            "Cria a tua conta de jogador"
                        } else {
                            "Envia um pedido para seres organizador"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = AthloColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RegisterFieldLabel("TIPO DE CONTA")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AccountTypeCard(
                            title = "Jogador",
                            subtitle = "Entrar numa equipa",
                            selected = selectedAccountType == ACCOUNT_PLAYER,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedAccountType = ACCOUNT_PLAYER
                                shirtNumberError = null
                                positionError = null
                            }
                        )

                        AccountTypeCard(
                            title = "Organizador",
                            subtitle = "Criar eventos",
                            selected = selectedAccountType == ACCOUNT_ORGANIZER,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedAccountType = ACCOUNT_ORGANIZER
                                shirtNumberError = null
                                positionError = null
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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

                    if (selectedAccountType == ACCOUNT_PLAYER) {
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
                        FootballPositionDropdown(
                            selectedPosition = position,
                            positions = footballPositions,
                            isError = positionError != null,
                            errorText = positionError,
                            onPositionSelected = {
                                position = it
                                positionError = null
                            }
                        )
                    } else {
                        RegisterFieldLabel("DESCRIÇÃO DO PEDIDO")
                        OutlinedTextField(
                            value = organizerRequestMessage,
                            onValueChange = {
                                organizerRequestMessage = it
                            },
                            placeholder = {
                                Text("Escreve uma breve descrição do teu pedido, se quiseres")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = registerTextFieldColors(),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "O teu pedido será enviado para aprovação do administrador.",
                            color = AthloColors.TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

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
                                    selectedAccountType,
                                    if (selectedAccountType == ACCOUNT_PLAYER) {
                                        shirtNumber.toIntOrNull()
                                    } else {
                                        null
                                    },
                                    if (selectedAccountType == ACCOUNT_PLAYER) {
                                        position.trim()
                                    } else {
                                        null
                                    },
                                    if (selectedAccountType == ACCOUNT_ORGANIZER) {
                                        organizerRequestMessage.trim().ifBlank { null }
                                    } else {
                                        null
                                    }
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
                                text = if (selectedAccountType == ACCOUNT_PLAYER) {
                                    "Criar Conta"
                                } else {
                                    "Enviar Pedido"
                                },
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
private fun AccountTypeCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val background = if (selected) {
        AthloColors.Navy
    } else {
        AthloColors.NeutralBg
    }

    val titleColor = if (selected) {
        Color.White
    } else {
        AthloColors.TextPrimary
    }

    val subtitleColor = if (selected) {
        Color(0xFFC8DCEF)
    } else {
        AthloColors.TextMuted
    }

    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = titleColor,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = subtitleColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FootballPositionDropdown(
    selectedPosition: String,
    positions: List<String>,
    isError: Boolean,
    errorText: String?,
    onPositionSelected: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedPosition,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Escolhe a tua posição") },
            isError = isError,
            supportingText = {
                errorText?.let {
                    Text(text = it, color = Color(0xFFC83755))
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = registerTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            positions.forEach { position ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = position,
                            color = AthloColors.TextPrimary
                        )
                    },
                    onClick = {
                        onPositionSelected(position)
                        expanded = false
                    }
                )
            }
        }
    }
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
                text = "Cria a tua conta e entra no jogo",
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