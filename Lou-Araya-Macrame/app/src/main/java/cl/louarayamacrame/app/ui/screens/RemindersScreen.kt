@file:OptIn(ExperimentalMaterial3Api::class)

package cl.louarayamacrame.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.louarayamacrame.app.ui.components.EmptyState
import cl.louarayamacrame.app.ui.components.NoInsets
import cl.louarayamacrame.app.ui.components.TaskItem
import cl.louarayamacrame.app.viewmodel.RemindersViewModel
import cl.louarayamacrame.app.viewmodel.appViewModel

@Composable
fun RemindersScreen(
    vm: RemindersViewModel = appViewModel { RemindersViewModel(it.prefs, it.petRepo) }
) {
    val items by vm.items.collectAsStateWithLifecycle()
    val pending = items.filter { !it.task.done }.sortedBy { it.task.dueMillis }
    val done = items.filter { it.task.done }.sortedByDescending { it.task.dueMillis }

    Scaffold(
        contentWindowInsets = NoInsets,
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Recordatorios", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        if (items.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Notifications,
                title = "Sin recordatorios",
                subtitle = "Agrega cuidados desde el detalle de una mascota y aparecerán aquí.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (pending.isNotEmpty()) {
                    item(key = "header_pending") {
                        Text("Pendientes", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    items(pending, key = { it.task.id }) { item ->
                        TaskItem(
                            task = item.task,
                            petName = item.petName,
                            onToggle = { vm.toggle(item) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
                if (done.isNotEmpty()) {
                    item(key = "header_done") {
                        Text("Completados", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    items(done, key = { it.task.id }) { item ->
                        TaskItem(
                            task = item.task,
                            petName = item.petName,
                            onToggle = { vm.toggle(item) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
