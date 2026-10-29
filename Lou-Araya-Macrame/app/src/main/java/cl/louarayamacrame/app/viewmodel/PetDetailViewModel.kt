package cl.louarayamacrame.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.louarayamacrame.app.data.local.CareTaskEntity
import cl.louarayamacrame.app.data.local.PetEntity
import cl.louarayamacrame.app.data.repository.PetRepository
import cl.louarayamacrame.app.domain.Validators
import cl.louarayamacrame.app.util.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskFormState(
    val type: String = "",
    val dateMillis: Long? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val notes: String = "",
    val typeError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null,
    val notesError: String? = null
) {
    val isValid: Boolean
        get() = Validators.taskType(type) == null &&
            Validators.taskDate(dateMillis) == null &&
            Validators.taskTime(hour, minute) == null &&
            Validators.taskDue(dateMillis, hour, minute) == null &&
            Validators.notes(notes) == null
}

class PetDetailViewModel(
    private val repo: PetRepository,
    private val petId: Long
) : ViewModel() {

    val pet: StateFlow<PetEntity?> = repo.observePet(petId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val tasks: StateFlow<List<CareTaskEntity>> = repo.observeTasksByPet(petId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _form = MutableStateFlow(TaskFormState())
    val form: StateFlow<TaskFormState> = _form.asStateFlow()

    fun onTypeChange(v: String) = _form.update { it.copy(type = v, typeError = Validators.taskType(v)) }

    fun onDateChange(v: Long) = _form.update {
        it.copy(
            dateMillis = v,
            dateError = Validators.taskDate(v),
            timeError = Validators.taskDue(v, it.hour, it.minute)
        )
    }

    fun onTimeChange(h: Int, m: Int) = _form.update {
        it.copy(
            hour = h,
            minute = m,
            timeError = Validators.taskTime(h, m) ?: Validators.taskDue(it.dateMillis, h, m)
        )
    }

    fun onNotesChange(v: String) = _form.update { it.copy(notes = v, notesError = Validators.notes(v)) }

    fun resetForm() {
        _form.value = TaskFormState()
    }

    fun saveTask(onSaved: () -> Unit) {
        val f = _form.value
        if (!f.isValid) {
            _form.value = f.copy(
                typeError = Validators.taskType(f.type),
                dateError = Validators.taskDate(f.dateMillis),
                timeError = Validators.taskTime(f.hour, f.minute)
                    ?: Validators.taskDue(f.dateMillis, f.hour, f.minute),
                notesError = Validators.notes(f.notes)
            )
            return
        }
        val petName = pet.value?.name ?: return
        viewModelScope.launch {
            repo.addTask(
                petName,
                CareTaskEntity(
                    petId = petId,
                    type = f.type,
                    dueMillis = Formatters.combineDateTime(f.dateMillis!!, f.hour!!, f.minute!!),
                    notes = f.notes.trim()
                )
            )
            _form.value = TaskFormState()
            onSaved()
        }
    }

    fun toggleDone(task: CareTaskEntity) {
        viewModelScope.launch { repo.setTaskDone(task, pet.value?.name.orEmpty(), !task.done) }
    }

    fun deleteTask(task: CareTaskEntity) {
        viewModelScope.launch { repo.deleteTask(task) }
    }

    fun deletePet(onDeleted: () -> Unit) {
        val current = pet.value ?: return
        viewModelScope.launch {
            repo.deletePet(current)
            onDeleted()
        }
    }
}
