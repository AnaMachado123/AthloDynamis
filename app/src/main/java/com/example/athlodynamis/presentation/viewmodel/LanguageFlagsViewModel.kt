package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.repository.CountriesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LanguageFlagsUiState(
    val portugalFlagUrl: String? = null,
    val englishFlagUrl: String? = null,
    val isLoading: Boolean = false
)

class LanguageFlagsViewModel : ViewModel() {

    private val repository = CountriesRepository()

    private val _uiState = MutableStateFlow(LanguageFlagsUiState())
    val uiState: StateFlow<LanguageFlagsUiState> = _uiState

    init {
        loadFlags()
    }

    private fun loadFlags() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val (ptFlag, enFlag) = repository.getLanguageFlags()

                _uiState.value = LanguageFlagsUiState(
                    portugalFlagUrl = ptFlag,
                    englishFlagUrl = enFlag,
                    isLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = LanguageFlagsUiState(
                    portugalFlagUrl = null,
                    englishFlagUrl = null,
                    isLoading = false
                )
            }
        }
    }
}