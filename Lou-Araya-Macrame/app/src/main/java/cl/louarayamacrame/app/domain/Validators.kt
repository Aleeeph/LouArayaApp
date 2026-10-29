package cl.louarayamacrame.app.domain

import cl.louarayamacrame.app.util.Formatters

/**
 * Reglas de validación de todos los formularios.
 * Cada función devuelve el mensaje de error, o null si el valor es válido.
 * Viven fuera de la UI para poder probarlas con tests unitarios.
 */
object Validators {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun personName(value: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "El nombre es obligatorio"
            v.length < 2 -> "Mínimo 2 caracteres"
            v.length > 30 -> "Máximo 30 caracteres"
            !v.all { it.isLetter() || it == ' ' } -> "Solo se permiten letras y espacios"
            else -> null
        }
    }

    fun email(value: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "El correo es obligatorio"
            !EMAIL_REGEX.matches(v) -> "Ingresa un correo válido"
            else -> null
        }
    }

    fun password(value: String): String? = when {
        value.isEmpty() -> "La contraseña es obligatoria"
        value.length < 8 -> "Mínimo 8 caracteres"
        value.none { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun loginPassword(value: String): String? =
        if (value.isEmpty()) "La contraseña es obligatoria" else null

    fun confirmPassword(password: String, confirm: String): String? = when {
        confirm.isEmpty() -> "Confirma tu contraseña"
        confirm != password -> "Las contraseñas no coinciden"
        else -> null
    }

    fun petName(value: String): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "El nombre es obligatorio"
            v.length < 2 -> "Mínimo 2 caracteres"
            v.length > 20 -> "Máximo 20 caracteres"
            !v.all { it.isLetterOrDigit() || it == ' ' || it == '-' } -> "Usa solo letras, números o espacios"
            else -> null
        }
    }

    fun species(value: String): String? =
        if (value.isBlank()) "Selecciona una especie" else null

    fun breed(value: String): String? =
        if (value.trim().length > 30) "Máximo 30 caracteres" else null

    fun birthDate(millis: Long?, now: Long = System.currentTimeMillis()): String? = when {
        millis == null -> "Selecciona la fecha de nacimiento"
        millis > now -> "La fecha no puede ser futura"
        else -> null
    }

    fun parseWeight(value: String): Double? =
        value.trim().replace(',', '.').toDoubleOrNull()

    fun weight(value: String): String? {
        if (value.isBlank()) return "El peso es obligatorio"
        val w = parseWeight(value) ?: return "Ingresa un peso válido"
        return when {
            w <= 0.0 -> "El peso debe ser mayor a 0"
            w > 120.0 -> "El peso máximo es 120 kg"
            else -> null
        }
    }

    fun taskType(value: String): String? =
        if (value.isBlank()) "Selecciona el tipo de cuidado" else null

    fun taskDate(millis: Long?): String? =
        if (millis == null) "Selecciona la fecha" else null

    fun taskTime(hour: Int?, minute: Int?): String? =
        if (hour == null || minute == null) "Selecciona la hora" else null

    /** Solo valida cuando fecha y hora ya fueron elegidas. */
    fun taskDue(dateMillis: Long?, hour: Int?, minute: Int?, now: Long = System.currentTimeMillis()): String? {
        if (dateMillis == null || hour == null || minute == null) return null
        return if (Formatters.combineDateTime(dateMillis, hour, minute) <= now) {
            "La fecha y hora deben ser futuras"
        } else {
            null
        }
    }

    fun notes(value: String): String? =
        if (value.trim().length > 100) "Máximo 100 caracteres" else null
}
