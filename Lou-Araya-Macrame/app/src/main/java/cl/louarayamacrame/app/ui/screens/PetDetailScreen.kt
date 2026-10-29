@file:OptIn(ExperimentalMaterial3Api::class)

package cl.louarayamacrame.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.louarayamacrame.app.data.local.PetEntity
import cl.louarayamacrame.app.domain.Catalog
import cl.louarayamacrame.app.ui.components.AppDatePickerDialog
import cl.louarayamacrame.app.ui.components.AppTextField
import cl.louarayamacrame.app.ui.components.AppTimePickerDialog
import cl.louarayamacrame.app.ui.components.AppearAnimated
import cl.louarayamacrame.app.ui.components.ClickableField
import cl.louarayamacrame.app.ui.components.DropdownField
import cl.louarayamacrame.app.ui.components.PetAvatar
import cl.louarayamacrame.app.ui.components.TaskItem
import cl.louarayamacrame.app.ui.components.TodayOrFuture
import cl.louarayamacrame.app.ui.components.rememberNotificationPermissionRequest
import cl.louarayamacrame.app.ui.components.taskIcon
import cl.louarayamacrame.app.util.Formatters
import cl.louarayamacrame.app.util.Intents
import cl.louarayamacrame.app.viewmodel.PetDetailViewModel
import cl.louarayamacrame.app.viewmodel.TaskFormState
import cl.louarayamacrame.app.viewmodel.appViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun PetDetailScreen(
    petId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    vm: PetDetailViewModel = appViewModel(key = "pet_detail_$petId") {
        PetDetailViewModel(it.petRepo, petId)
    }
) {
    val petState by vm.pet.collectAsStateWithLifecycle()
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val form by vm.form.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showSheet by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableIntStateOf(0) }

    // --- Recurso nativo 2: notificaciones (permiso en Android 13+) ---
    val requestNotifications = rememberNotificationPermissionRequest()

    val pet = petState
    val pending = tasks.filter { !it.done }.sortedBy { it.dueMillis }
    val history = tasks.filter { it.done }.sortedByDescending { it.dueMillis }
    val shown = if (tab == 0) pending else history

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(pet?.name.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(petId) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Editar mascota")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Eliminar mascota")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showSheet = true },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Agregar cuidado") }
            )
        }
    ) { padding ->
        if (pet == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { PetHeader(pet) }
                item {
                    OutlinedButton(
                        onClick = { Intents.openNearbyVets(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.LocalHospital, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Buscar veterinarias cercanas")
                    }
                }
                item {
                    TabRow(selectedTabIndex = tab) {
                        Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Pendientes (${pending.size})") })
                        Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Historial (${history.size})") })
                    }
                }
                if (shown.isEmpty()) {
                    item {
                        Text(
                            text = if (tab == 0) "No hay cuidados pendientes. Toca «Agregar cuidado»." else "Aún no has completado cuidados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    }
                } else {
                    items(shown, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { vm.toggleDone(task) },
                            onDelete = { vm.deleteTask(task) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                vm.resetForm()
            },
            sheetState = sheetState
        ) {
            TaskFormSheet(
                form = form,
                vm = vm,
                onSave = {
                    vm.saveTask {
                        requestNotifications()
                        scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                        scope.launch { snackbar.showSnackbar("Cuidado agregado") }
                    }
                }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar mascota") },
            text = { Text("Se eliminarán también todos sus cuidados y recordatorios. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    vm.deletePet(onBack)
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun PetHeader(pet: PetEntity) {
    AppearAnimated {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PetAvatar(photoPath = pet.photoPath, size = 120.dp)
            Spacer(Modifier.height(12.dp))
            Text(pet.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            val breed = if (pet.breed.isNotBlank()) " · ${pet.breed}" else ""
            Text(
                pet.species + breed,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoTile(Modifier.weight(1f), Icons.Rounded.Cake, "Edad", Formatters.ageText(pet.birthDateMillis))
                InfoTile(Modifier.weight(1f), Icons.Rounded.FitnessCenter, "Peso", Formatters.weightText(pet.weightKg))
                InfoTile(Modifier.weight(1f), Icons.Rounded.CalendarToday, "Nació", Formatters.formatUtcDate(pet.birthDateMillis))
            }
        }
    }
}

@Composable
private fun InfoTile(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(value, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
private fun TaskFormSheet(form: TaskFormState, vm: PetDetailViewModel, onSave: () -> Unit) {
    var showDate by rememberSaveable { mutableStateOf(false) }
    var showTime by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nuevo cuidado", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        DropdownField(
            value = form.type,
            label = "Tipo de cuidado",
            options = Catalog.taskTypes,
            icon = taskIcon(form.type),
            onSelect = vm::onTypeChange,
            error = form.typeError
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ClickableField(
                value = form.dateMillis?.let { Formatters.formatUtcDate(it) }.orEmpty(),
                label = "Fecha",
                leadingIcon = Icons.Rounded.CalendarToday,
                onClick = { showDate = true },
                error = form.dateError,
                modifier = Modifier.weight(1f)
            )
            ClickableField(
                value = if (form.hour != null && form.minute != null) Formatters.formatTime(form.hour, form.minute) else "",
                label = "Hora",
                leadingIcon = Icons.Rounded.Schedule,
                onClick = { showTime = true },
                error = form.timeError,
                modifier = Modifier.weight(1f)
            )
        }
        AppTextField(
            value = form.notes,
            onValueChange = vm::onNotesChange,
            label = "Notas (opcional)",
            leadingIcon = Icons.Rounded.Edit,
            error = form.notesError,
            singleLine = false,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Done)
        )
        Button(
            onClick = onSave,
            enabled = form.isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) { Text("Guardar cuidado") }
    }

    if (showDate) {
        AppDatePickerDialog(
            initialMillis = form.dateMillis,
            selectableDates = TodayOrFuture,
            onConfirm = {
                vm.onDateChange(it)
                showDate = false
            },
            onDismiss = { showDate = false }
        )
    }
    if (showTime) {
        val now = LocalTime.now()
        AppTimePickerDialog(
            initialHour = form.hour ?: now.hour,
            initialMinute = form.minute ?: now.minute,
            onConfirm = { h, m ->
                vm.onTimeChange(h, m)
                showTime = false
            },
            onDismiss = { showTime = false }
        )
    }
}
