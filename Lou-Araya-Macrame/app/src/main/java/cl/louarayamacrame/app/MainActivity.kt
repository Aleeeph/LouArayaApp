package cl.louarayamacrame.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.louarayamacrame.app.macrame.ui.MacrameScreen
import cl.louarayamacrame.app.ui.theme.LouArayaMacrameTheme
import cl.louarayamacrame.app.viewmodel.SessionViewModel
import cl.louarayamacrame.app.viewmodel.appViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sessionVm: SessionViewModel = appViewModel { SessionViewModel(it.prefs) }
            val darkPref by sessionVm.darkMode.collectAsStateWithLifecycle()
            LouArayaMacrameTheme(darkTheme = darkPref ?: isSystemInDarkTheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MacrameScreen()
                }
            }
        }
    }
}
