@file:OptIn(ExperimentalMaterial3Api::class)

package cl.louarayamacrame.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.louarayamacrame.app.ui.components.AppearAnimated
import cl.louarayamacrame.app.ui.components.EmptyState
import cl.louarayamacrame.app.ui.components.NoInsets
import cl.louarayamacrame.app.ui.components.PetCard
import cl.louarayamacrame.app.viewmodel.HomeViewModel
import cl.louarayamacrame.app.viewmodel.appViewModel

@Composable
fun HomeScreen(
    onAddPet: () -> Unit,
    onOpenPet: (Long) -> Unit,
    vm: HomeViewModel = appViewModel { HomeViewModel(it.prefs, it.petRepo) }
) {
    val petsState by vm.pets.collectAsStateWithLifecycle()
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val pets = petsState

    Scaffold(
        contentWindowInsets = NoInsets,
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mis mascotas", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddPet,
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Agregar mascota") }
            )
        }
    ) { padding ->
        when {
            pets == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            pets.isEmpty() -> EmptyState(
                icon = Icons.Rounded.Pets,
                title = "Aún no tienes mascotas",
                subtitle = "Toca «Agregar mascota» para registrar a tu primer compañero.",
                modifier = Modifier.padding(padding)
            )
            else -> {
                val pendingCount = tasks.count { !it.done }
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "${pets.size} ${if (pets.size == 1) "mascota" else "mascotas"} · " +
                                    "$pendingCount ${if (pendingCount == 1) "cuidado pendiente" else "cuidados pendientes"}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                            )
                        }
                    }
                    items(pets, key = { it.id }) { pet ->
                        val next = tasks.filter { it.petId == pet.id && !it.done }.minByOrNull { it.dueMillis }
                        Box(Modifier.animateItem()) {
                            AppearAnimated {
                                PetCard(pet = pet, nextTask = next, onClick = { onOpenPet(pet.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
