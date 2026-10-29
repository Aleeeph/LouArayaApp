package cl.louarayamacrame.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.louarayamacrame.app.ui.components.AppTextField
import cl.louarayamacrame.app.ui.components.AppearAnimated
import cl.louarayamacrame.app.ui.components.ErrorBanner
import cl.louarayamacrame.app.ui.components.PasswordField
import cl.louarayamacrame.app.viewmodel.LoginViewModel
import cl.louarayamacrame.app.viewmodel.appViewModel

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onGoRegister: () -> Unit,
    vm: LoginViewModel = appViewModel { LoginViewModel(it.authRepo) }
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.success) { if (state.success) onLoggedIn() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.background)
                )
            )
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(32.dp))
        AppearAnimated {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, modifier = Modifier.size(88.dp)) {
                    Icon(
                        Icons.Rounded.Pets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(22.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text("Bienvenido a Lou Araya Macramé", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Inicia sesión para cuidar a tus mascotas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        AnimatedVisibility(visible = state.formError != null) {
            ErrorBanner(state.formError ?: "", Modifier.fillMaxWidth())
        }

        AppTextField(
            value = state.email,
            onValueChange = vm::onEmailChange,
            label = "Correo electrónico",
            leadingIcon = Icons.Rounded.Email,
            error = state.emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )
        PasswordField(
            value = state.password,
            onValueChange = vm::onPasswordChange,
            label = "Contraseña",
            error = state.passwordError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { vm.submit() })
        )

        Button(
            onClick = vm::submit,
            enabled = state.canSubmit && !state.loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (state.loading) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Iniciar sesión")
            }
        }
        TextButton(onClick = onGoRegister) { Text("¿No tienes cuenta? Regístrate") }
    }
}
