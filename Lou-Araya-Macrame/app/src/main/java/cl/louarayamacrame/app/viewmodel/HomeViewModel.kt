package cl.louarayamacrame.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.louarayamacrame.app.data.local.CareTaskEntity
import cl.louarayamacrame.app.data.local.PetEntity
import cl.louarayamacrame.app.data.prefs.UserPreferences
import cl.louarayamacrame.app.data.repository.PetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(prefs: UserPreferences, repo: PetRepository) : ViewModel() {
    private val ownerId = prefs.userId.filterNotNull()

    /** null mientras se carga la primera vez. */
    val pets: StateFlow<List<PetEntity>?> = ownerId
        .flatMapLatest { repo.observePets(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val tasks: StateFlow<List<CareTaskEntity>> = ownerId
        .flatMapLatest { repo.observeTasksByOwner(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
