package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.remote.dto.CreatePlayerDto
import com.example.athlodynamis.data.remote.dto.CreateUserDto
import com.example.athlodynamis.data.repository.PlayerRepository
import com.example.athlodynamis.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val accountPending: Boolean = false,
    val error: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val userPassword: String? = null,
    val userRole: String? = null,
    val playerTeamId: Int? = null,
    val photoUrl: String? = null,
    val approvalStatus: String? = null,
    val organizerRequestMessage: String? = null
)

class AuthViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val playerRepository: PlayerRepository = PlayerRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun registerUser(
        name: String,
        email: String,
        password: String,
        accountType: String,
        shirtNumber: Int?,
        position: String?,
        organizerRequestMessage: String?
    ) {
        val cleanName = name.trim()
        val cleanEmail = email.trim()
        val cleanPosition = position?.trim().orEmpty()
        val cleanMessage = organizerRequestMessage?.trim().orEmpty()
        val cleanAccountType = accountType.uppercase()

        if (cleanName.isBlank()) {
            _uiState.value = AuthUiState(error = "Nome obrigatório.")
            return
        }

        if (cleanEmail.isBlank()) {
            _uiState.value = AuthUiState(error = "Email obrigatório.")
            return
        }

        if (!cleanEmail.contains("@")) {
            _uiState.value = AuthUiState(error = "Email inválido. O email deve conter @.")
            return
        }

        if (password.isBlank()) {
            _uiState.value = AuthUiState(error = "Password obrigatória.")
            return
        }

        if (password.length < 6) {
            _uiState.value = AuthUiState(error = "Password mínimo 6 caracteres.")
            return
        }

        if (cleanAccountType != "PLAYER" && cleanAccountType != "ORGANIZER") {
            _uiState.value = AuthUiState(error = "Escolhe o tipo de conta.")
            return
        }

        if (cleanAccountType == "PLAYER") {
            if (shirtNumber == null || shirtNumber !in 1..99) {
                _uiState.value = AuthUiState(error = "Número da camisola deve estar entre 1 e 99.")
                return
            }

            if (cleanPosition.isBlank()) {
                _uiState.value = AuthUiState(error = "Posição obrigatória.")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                val existingUser = userRepository.getUserByEmail(cleanEmail)

                if (existingUser != null) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        error = "Já existe uma conta com este email."
                    )
                    return@launch
                }

                val userId = UUID.randomUUID().toString()

                val approvalStatus = if (cleanAccountType == "ORGANIZER") {
                    "PENDING"
                } else {
                    "APPROVED"
                }

                userRepository.createUser(
                    CreateUserDto(
                        id = userId,
                        name = cleanName,
                        email = cleanEmail,
                        password = password,
                        role = cleanAccountType,
                        approvalStatus = approvalStatus,
                        organizerRequestMessage = if (cleanAccountType == "ORGANIZER") {
                            cleanMessage.ifBlank { null }
                        } else {
                            null
                        }
                    )
                )

                if (cleanAccountType == "PLAYER") {
                    playerRepository.createPlayer(
                        CreatePlayerDto(
                            userId = userId,
                            teamId = null,
                            name = cleanName,
                            position = cleanPosition,
                            number = shirtNumber ?: 0,
                            goals = 0,
                            assists = 0,
                            yellowCards = 0
                        )
                    )
                }

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    error = e.message ?: "Erro ao criar conta."
                )
            }
        }
    }

    fun registerPlayer(
        name: String,
        email: String,
        password: String,
        shirtNumber: Int,
        position: String
    ) {
        registerUser(
            name = name,
            email = email,
            password = password,
            accountType = "PLAYER",
            shirtNumber = shirtNumber,
            position = position,
            organizerRequestMessage = null
        )
    }

    fun clearAuthState() {
        _uiState.value = AuthUiState()
    }

    fun logout() {
        _uiState.value = AuthUiState()
    }

    fun login(
        email: String,
        password: String
    ) {
        val cleanEmail = email.trim()

        if (cleanEmail.isBlank()) {
            _uiState.value = AuthUiState(error = "Email obrigatório.")
            return
        }

        if (!cleanEmail.contains("@")) {
            _uiState.value = AuthUiState(error = "Email inválido.")
            return
        }

        if (password.isBlank()) {
            _uiState.value = AuthUiState(error = "Password obrigatória.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                val user = userRepository.getUserByEmail(cleanEmail)

                if (user == null) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        error = "Não existe conta com este email."
                    )
                    return@launch
                }

                if (user.password != password) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        error = "Password incorreta."
                    )
                    return@launch
                }

                if (
                    user.role.equals("ORGANIZER", ignoreCase = true) &&
                    user.approvalStatus.equals("REJECTED", ignoreCase = true)
                ) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        error = "O teu pedido de organizador foi rejeitado."
                    )
                    return@launch
                }

                val player = if (user.role.equals("PLAYER", ignoreCase = true)) {
                    playerRepository.getPlayerByUserId(user.id)
                } else {
                    null
                }

                if (
                    user.role.equals("ORGANIZER", ignoreCase = true) &&
                    user.approvalStatus.equals("PENDING", ignoreCase = true)
                ) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isSuccess = false,
                        accountPending = true,
                        userId = user.id,
                        userName = user.name,
                        userEmail = user.email,
                        userPassword = user.password,
                        userRole = user.role,
                        approvalStatus = user.approvalStatus,
                        organizerRequestMessage = user.organizerRequestMessage,
                        photoUrl = user.photoUrl
                    )
                    return@launch
                }

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isSuccess = true,
                    accountPending = false,
                    userId = user.id,
                    userName = user.name,
                    userEmail = user.email,
                    userPassword = user.password,
                    userRole = user.role,
                    playerTeamId = player?.teamId,
                    photoUrl = user.photoUrl,
                    approvalStatus = user.approvalStatus,
                    organizerRequestMessage = user.organizerRequestMessage
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    error = e.message ?: "Erro ao iniciar sessão."
                )
            }
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        password: String
    ) {
        val userId = _uiState.value.userId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                userRepository.updateUser(
                    userId = userId,
                    name = name,
                    email = email,
                    password = password
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userName = name,
                    userEmail = email,
                    userPassword = password,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao atualizar perfil."
                )
            }
        }
    }

    fun updateProfileLocally(
        name: String,
        email: String,
        password: String
    ) {
        _uiState.value = _uiState.value.copy(
            userName = name,
            userEmail = email,
            userPassword = password,
            error = null
        )
    }

    fun uploadProfilePhoto(
        userId: String,
        imageBytes: ByteArray,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val photoUrl = userRepository.uploadProfilePhoto(
                    userId = userId,
                    bytes = imageBytes
                )

                userRepository.updateUserPhoto(
                    userId = userId,
                    photoUrl = photoUrl
                )

                _uiState.value = _uiState.value.copy(
                    photoUrl = photoUrl
                )

                onSuccess(photoUrl)
            } catch (e: Exception) {
                onError(
                    e.message ?: "Erro ao carregar foto."
                )
            }
        }
    }
}