package cl.louarayamacrame.app.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/** Gestiona los archivos de fotos tomadas con la cámara (almacenamiento interno de la app). */
object PhotoStorage {
    fun newPhotoFile(context: Context): File {
        val dir = File(context.filesDir, "pet_photos").apply { mkdirs() }
        return File(dir, "pet_${System.currentTimeMillis()}.jpg")
    }

    fun uriFor(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
