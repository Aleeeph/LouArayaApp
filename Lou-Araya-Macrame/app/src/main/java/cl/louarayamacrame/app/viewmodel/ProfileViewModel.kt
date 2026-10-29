package cl.louarayamacrame.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.louarayamacrame.app.data.local.UserEntity
import cl.louarayamacrame.app.data.prefs.UserPreferences
import cl.louarayamacrame.app.data.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val prefs: UserPreferences,
    private val authRepo: AuthRepository
) : ViewModel() {

    val user: StateFlow<UserEntity?> = prefs.userId.filterNotNull()
        .flatMapLatest { authRepo.observeUser(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val darkMode: StateFlow<Boolean?> =
        prefs.darkMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val notifications: StateFlow<Boolean> =
        prefs.notificationsEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefs.setDarkMode(enabled) }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepo.logout()
            onDone()
        }
    }
}
