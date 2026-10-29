@file:OptIn(ExperimentalMaterial3Api::class)

package cl.louarayamacrame.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.louarayamacrame.app.domain.Catalog
import cl.louarayamacrame.app.ui.components.AppDatePickerDialog
import cl.louarayamacrame.app.ui.components.AppTextField
import cl.louarayamacrame.app.ui.components.ClickableField
import cl.louarayamacrame.app.ui.components.DropdownField
import cl.louarayamacrame.app.ui.components.PastOrToday
import cl.louarayamacrame.app.ui.components.PetAvatar
import cl.louarayamacrame.app.ui.components.SuccessOverlay
import cl.louarayamacrame.app.util.Formatters
import cl.louarayamacrame.app.util.Intents
import cl.louarayamacrame.app.util.PhotoStorage
import cl.louarayamacrame.app.viewmodel.PetFormViewModel
import cl.louarayamacrame.app.viewmodel.appViewModel
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun PetFormScreen(
    petId: Long,
    onBack: () -> Unit,
    vm: PetFormViewModel = appViewModel(key = "pet_form_$petId") {
        PetFormViewModel(it.prefs, it.petRepo, petId)
    }
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showRationale by rememberSaveable { mutableStateOf(false) }
    var showDenied by rememberSaveable { mutableStateOf(false) }
    var pendingPhotoPath by rememberSaveable { mutableStateOf<String?>(null) }

    // --- Recurso nativo 1: cámara ---
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val path = pendingPhotoPath
        if (success && path != null) vm.onPhotoChange(path) else path?.let { File(it).delete() }
        pendingPhotoPath = null
    }

    fun launchCamera() {
        val file = PhotoStorage.newPhotoFile(context)
        pendingPhotoPath = file.absolutePath
        takePicture.launch(PhotoStorage.uriFor(context, file))
    }

    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera() else showDenied = true
    }

    fun onCameraClick() {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) launchCamera() else showRationale = true
    }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            delay(1000)
            onBack()
        }
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (state.isEditing) "Editar mascota" else "Nueva mascota") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    PetAvatar(
                        photoPath = state.photoPath,
                        size = 128.dp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onCameraClick() }
                    )
                    FilledIconButton(
                        onClick = { onCameraClick() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Rounded.PhotoCamera, contentDescription = "Tomar foto")
                    }
                }
                Text(
                    "Toca la foto para usar la cámara",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                AppTextField(
                    value = state.name,
                    onValueChange = vm::onNameChange,
                    label = "Nombre",
                    leadingIcon = Icons.Rounded.Pets,
                    error = state.nameError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                DropdownField(
                    value = state.species,
                    label = "Especie",
                    options = Catalog.species,
                    icon = Icons.Rounded.Category,
                    onSelect = vm::onSpeciesChange,
                    error = state.speciesError
                )
                AppTextField(
                    value = state.breed,
                    onValueChange = vm::onBreedChange,
                    label = "Raza (opcional)",
                    leadingIcon = Icons.Rounded.Favorite,
                    error = state.breedError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                ClickableField(
                    value = state.birthMillis?.let { Formatters.formatUtcDate(it) }.orEmpty(),
                    label = "Fecha de nacimiento",
                    leadingIcon = Icons.Rounded.Cake,
                    onClick = { showDatePicker = true },
                    error = state.birthError
                )
                AppTextField(
                    value = state.weight,
                    onValueChange = vm::onWeightChange,
                    label = "Peso",
                    leadingIcon = Icons.Rounded.FitnessCenter,
                    error = state.weightError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    trailingIcon = { Text("kg", modifier = Modifier.padding(end = 12.dp)) }
                )

                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = vm::save,
                    enabled = state.isValid && !state.saving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (state.saving) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Rounded.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
        SuccessOverlay(visible = state.saved, message = "¡Mascota guardada!")
    }

    if (showDatePicker) {
        AppDatePickerDialog(
            initialMillis = state.birthMillis,
            selectableDates = PastOrToday,
            onConfirm = {
                vm.onBirthChange(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            icon = { Icon(Icons.Rounded.PhotoCamera, contentDescription = null) },
            title = { Text("Permiso de cámara") },
            text = { Text("Lou Araya Macramé usa la cámara solo para tomar la foto de tu mascota. La imagen se guarda únicamente en tu dispositivo.") },
            confirmButton = {
                TextButton(onClick = {
                    showRationale = false
                    cameraPermission.launch(Manifest.permission.CAMERA)
                }) { Text("Continuar") }
            },
            dismissButton = { TextButton(onClick = { showRationale = false }) { Text("Ahora no") } }
        )
    }

    if (showDenied) {
        AlertDialog(
            onDismissRequest = { showDenied = false },
            title = { Text("Sin acceso a la cámara") },
            text = { Text("Para tomar fotos debes permitir el acceso a la cámara desde los ajustes de la aplicación.") },
            confirmButton = {
                TextButton(onClick = {
                    showDenied = false
                    Intents.openAppSettings(context)
                }) { Text("Abrir ajustes") }
            },
            dismissButton = { TextButton(onClick = { showDenied = false }) { Text("Cerrar") } }
        )
    }
}
