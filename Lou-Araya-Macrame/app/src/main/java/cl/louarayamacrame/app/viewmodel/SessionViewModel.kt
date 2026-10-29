package cl.louarayamacrame.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.louarayamacrame.app.data.prefs.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val userId: Long) : SessionState
}

class SessionViewModel(prefs: UserPreferences) : ViewModel() {
    private val sessionFlow: Flow<SessionState> = prefs.userId.map<Long?, SessionState> { id ->
        if (id == null) SessionState.LoggedOut else SessionState.LoggedIn(id)
    }

    val session: StateFlow<SessionState> =
        sessionFlow.stateIn(viewModelScope, SharingStarted.Eagerly, SessionState.Loading)

    val darkMode: StateFlow<Boolean?> =
        prefs.darkMode.stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
