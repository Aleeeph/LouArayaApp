package cl.louarayamacrame.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.louarayamacrame.app.data.local.PetEntity
import cl.louarayamacrame.app.data.prefs.UserPreferences
import cl.louarayamacrame.app.data.repository.PetRepository
import cl.louarayamacrame.app.domain.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PetFormState(
    val isEditing: Boolean = false,
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val birthMillis: Long? = null,
    val weight: String = "",
    val photoPath: String? = null,
    val nameError: String? = null,
    val speciesError: String? = null,
    val breedError: String? = null,
    val birthError: String? = null,
    val weightError: String? = null,
    val saving: Boolean = false,
    val saved: Boolean = false
) {
    val isValid: Boolean
        get() = Validators.petName(name) == null &&
            Validators.species(species) == null &&
            Validators.breed(breed) == null &&
            Validators.birthDate(birthMillis) == null &&
            Validators.weight(weight) == null
}

class PetFormViewModel(
    private val prefs: UserPreferences,
    private val repo: PetRepository,
    private val petId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(PetFormState(isEditing = petId != NEW))
    val state: StateFlow<PetFormState> = _state.asStateFlow()

    init {
        if (petId != NEW) {
            viewModelScope.launch {
                repo.getPet(petId)?.let { pet ->
                    _state.update {
                        it.copy(
                            name = pet.name,
                            species = pet.species,
                            breed = pet.breed,
                            birthMillis = pet.birthDateMillis,
                            weight = pet.weightKg.toString().removeSuffix(".0"),
                            photoPath = pet.photoPath
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v, nameError = Validators.petName(v)) }
    fun onSpeciesChange(v: String) = _state.update { it.copy(species = v, speciesError = Validators.species(v)) }
    fun onBreedChange(v: String) = _state.update { it.copy(breed = v, breedError = Validators.breed(v)) }
    fun onBirthChange(v: Long) = _state.update { it.copy(birthMillis = v, birthError = Validators.birthDate(v)) }
    fun onWeightChange(v: String) = _state.update { it.copy(weight = v, weightError = Validators.weight(v)) }
    fun onPhotoChange(path: String) = _state.update { it.copy(photoPath = path) }

    fun save() {
        val s = _state.value
        if (s.saving) return
        if (!s.isValid) {
            // muestra todos los errores pendientes
            _state.value = s.copy(
                nameError = Validators.petName(s.name),
                speciesError = Validators.species(s.species),
                breedError = Validators.breed(s.breed),
                birthError = Validators.birthDate(s.birthMillis),
                weightError = Validators.weight(s.weight)
            )
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            val owner = prefs.userId.first() ?: return@launch
            repo.savePet(
                PetEntity(
                    id = if (petId == NEW) 0 else petId,
                    ownerId = owner,
                    name = s.name.trim(),
                    species = s.species,
                    breed = s.breed.trim(),
                    birthDateMillis = s.birthMillis!!,
                    weightKg = Validators.parseWeight(s.weight)!!,
                    photoPath = s.photoPath
                )
            )
            _state.update { it.copy(saving = false, saved = true) }
        }
    }

    companion object {
        const val NEW = -1L
    }
}
