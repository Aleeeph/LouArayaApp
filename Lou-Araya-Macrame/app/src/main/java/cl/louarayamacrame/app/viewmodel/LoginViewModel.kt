package cl.louarayamacrame.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.louarayamacrame.app.data.repository.AuthRepository
import cl.louarayamacrame.app.data.repository.AuthResult
import cl.louarayamacrame.app.domain.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val loading: Boolean = false,
    val formError: String? = null,
    val success: Boolean = false
) {
    val canSubmit: Boolean
        get() = Validators.email(email) == null && Validators.loginPassword(password) == null
}

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update {
        it.copy(email = value, emailError = Validators.email(value), formError = null)
    }

    fun onPasswordChange(value: String) = _state.update {
        it.copy(password = value, passwordError = Validators.loginPassword(value), formError = null)
    }

    fun submit() {
        val s = _state.value
        if (!s.canSubmit || s.loading) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, formError = null) }
            when (val result = repo.login(s.email, s.password)) {
                is AuthResult.Success -> _state.update { it.copy(loading = false, success = true) }
                is AuthResult.Error -> _state.update { it.copy(loading = false, formError = result.message) }
            }
        }
    }
}
