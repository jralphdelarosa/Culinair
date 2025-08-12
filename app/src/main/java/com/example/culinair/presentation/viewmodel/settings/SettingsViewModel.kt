package com.example.culinair.presentation.viewmodel.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
* Created by John Ralph Dela Rosa on 8/9/2025.
*/
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    enum class LogoutState { Loading, Success, Error, Navigate }

    private val _logoutState = MutableSharedFlow<LogoutState>()
    val logoutState = _logoutState.asSharedFlow()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun logout() {
        viewModelScope.launch {
            try {
                _logoutState.emit(LogoutState.Loading)

                val result = signOutUseCase()

                if (result.isSuccess) {
                    _logoutState.emit(LogoutState.Success)
                    delay(500) // Brief delay for UX
                    _logoutState.emit(LogoutState.Navigate)
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Sign out failed"
                    _errorMessage.value = errorMessage
                    _logoutState.emit(LogoutState.Error)
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
                _logoutState.emit(LogoutState.Error)
            }
        }
    }
}