package cl.louarayamacrame.app.macrame.domain

/** Reglas de negocio independientes de Jetpack Compose. */
object MacrameValidators {
    fun validateAlias(value: String): String? = when {
        value.isBlank() -> "Este campo es obligatorio."
        value.trim().length < 2 -> "Ingresa al menos 2 caracteres."
        value.trim().length > 50 -> "Usa un máximo de 50 caracteres."
        else -> null
    }

    fun validateNotes(value: String): String? = if (value.length > 300) "Usa un máximo de 300 caracteres." else null
}
