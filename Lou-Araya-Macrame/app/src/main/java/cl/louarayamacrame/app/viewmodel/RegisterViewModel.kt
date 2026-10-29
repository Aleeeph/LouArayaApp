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

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val loading: Boolean = false,
    val formError: String? = null,
    val success: Boolean = false
) {
    val canSubmit: Boolean
        get() = Validators.personName(name) == null &&
            Validators.email(email) == null &&
            Validators.password(password) == null &&
            Validators.confirmPassword(password, confirm) == null
}

class RegisterViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update {
        it.copy(name = value, nameError = Validators.personName(value), formError = null)
    }

    fun onEmailChange(value: String) = _state.update {
        it.copy(email = value, emailError = Validators.email(value), formError = null)
    }

    fun onPasswordChange(value: String) = _state.update {
        it.copy(
            password = value,
            passwordError = Validators.password(value),
            // si ya se escribió la confirmación, se revalida contra la nueva contraseña
            confirmError = if (it.confirm.isEmpty()) null else Validators.confirmPassword(value, it.confirm),
            formError = null
        )
    }

    fun onConfirmChange(value: String) = _state.update {
        it.copy(confirm = value, confirmError = Validators.confirmPassword(it.password, value), formError = null)
    }

    fun submit() {
        val s = _state.value
        if (!s.canSubmit || s.loading) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, formError = null) }
            when (val result = repo.register(s.name, s.email, s.password)) {
                is AuthResult.Success -> _state.update { it.copy(loading = false, success = true) }
                is AuthResult.Error -> _state.update { it.copy(loading = false, formError = result.message) }
            }
        }
    }
}
