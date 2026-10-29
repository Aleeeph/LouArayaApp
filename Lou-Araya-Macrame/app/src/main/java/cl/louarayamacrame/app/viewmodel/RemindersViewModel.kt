package cl.louarayamacrame.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.louarayamacrame.app.data.local.CareTaskEntity
import cl.louarayamacrame.app.data.prefs.UserPreferences
import cl.louarayamacrame.app.data.repository.PetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReminderItem(val task: CareTaskEntity, val petName: String)

@OptIn(ExperimentalCoroutinesApi::class)
class RemindersViewModel(prefs: UserPreferences, private val repo: PetRepository) : ViewModel() {

    val items: StateFlow<List<ReminderItem>> = prefs.userId.filterNotNull()
        .flatMapLatest { ownerId ->
            combine(repo.observePets(ownerId), repo.observeTasksByOwner(ownerId)) { pets, tasks ->
                val names = pets.associate { it.id to it.name }
                tasks.map { ReminderItem(it, names[it.petId].orEmpty()) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggle(item: ReminderItem) {
        viewModelScope.launch { repo.setTaskDone(item.task, item.petName, !item.task.done) }
    }
}
