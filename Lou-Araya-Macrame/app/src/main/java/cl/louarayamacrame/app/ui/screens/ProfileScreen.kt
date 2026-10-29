@file:OptIn(ExperimentalMaterial3Api::class)

package cl.louarayamacrame.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.louarayamacrame.app.ui.components.AppearAnimated
import cl.louarayamacrame.app.ui.components.NoInsets
import cl.louarayamacrame.app.ui.components.rememberNotificationPermissionRequest
import cl.louarayamacrame.app.util.Intents
import cl.louarayamacrame.app.viewmodel.ProfileViewModel
import cl.louarayamacrame.app.viewmodel.appViewModel

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    vm: ProfileViewModel = appViewModel { ProfileViewModel(it.prefs, it.authRepo) }
) {
    val user by vm.user.collectAsStateWithLifecycle()
    val darkPref by vm.darkMode.collectAsStateWithLifecycle()
    val notificationsOn by vm.notifications.collectAsStateWithLifecycle()
    val isDark = darkPref ?: isSystemInDarkTheme()
    val context = LocalContext.current
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    val requestNotifications = rememberNotificationPermissionRequest { granted ->
        vm.setNotifications(granted)
    }

    Scaffold(
        contentWindowInsets = NoInsets,
        topBar = { CenterAlignedTopAppBar(title = { Text("Perfil", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppearAnimated {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.name?.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(user?.name.orEmpty(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        user?.email.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(Modifier.fillMaxWidth()) {
                SwitchRow(Icons.Rounded.Brightness4, "Modo oscuro", isDark) { vm.setDarkMode(it) }
                HorizontalDivider()
                SwitchRow(Icons.Rounded.Notifications, "Recordatorios", notificationsOn) { enabled ->
                    if (enabled) requestNotifications() else vm.setNotifications(false)
                }
            }

            OutlinedButton(
                onClick = { Intents.openNearbyVets(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.LocalHospital, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Veterinarias cercanas")
            }

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres cerrar tu sesión? Tus datos quedan guardados en el dispositivo.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    vm.logout(onLoggedOut)
                }) { Text("Cerrar sesión") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun SwitchRow(icon: ImageVector, label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
