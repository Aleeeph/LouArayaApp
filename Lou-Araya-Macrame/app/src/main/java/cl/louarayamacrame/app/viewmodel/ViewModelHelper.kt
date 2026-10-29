package cl.louarayamacrame.app.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.louarayamacrame.app.MacrameApp
import cl.louarayamacrame.app.di.AppContainer

/** Crea un ViewModel inyectándole las dependencias del [AppContainer]. */
@Composable
inline fun <reified VM : ViewModel> appViewModel(
    key: String? = null,
    crossinline builder: (AppContainer) -> VM
): VM {
    val container = (LocalContext.current.applicationContext as MacrameApp).container
    return viewModel<VM>(
        key = key,
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = builder(container) as T
        }
    )
}
