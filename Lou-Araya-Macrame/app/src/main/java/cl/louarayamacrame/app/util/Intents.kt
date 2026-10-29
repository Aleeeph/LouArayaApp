package cl.louarayamacrame.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

object Intents {
    /** Abre una app de mapas buscando veterinarias cercanas (con respaldo en el navegador). */
    fun openNearbyVets(context: Context) {
        val attempts = listOf(
            Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=veterinaria")),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/veterinaria+cerca"))
        )
        for (intent in attempts) {
            try {
                context.startActivity(intent)
                return
            } catch (e: ActivityNotFoundException) {
                // se intenta con la siguiente opción
            }
        }
        Toast.makeText(context, "No se encontró una app para abrir mapas", Toast.LENGTH_SHORT).show()
    }

    fun openAppSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
        )
    }
}
