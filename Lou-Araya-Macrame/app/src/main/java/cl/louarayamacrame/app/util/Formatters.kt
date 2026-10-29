package cl.louarayamacrame.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {
    private val locale: Locale = Locale.forLanguageTag("es-CL")
    private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", locale)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("dd MMM, HH:mm", locale)

    /** El DatePicker de Material 3 entrega fechas como milisegundos en UTC. */
    fun utcDate(millis: Long): LocalDate =
        Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

    fun formatUtcDate(millis: Long): String = dateFormat.format(utcDate(millis))

    fun formatDateTime(millis: Long): String =
        dateTimeFormat.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

    fun formatTime(hour: Int, minute: Int): String = "%02d:%02d".format(hour, minute)

    fun combineDateTime(dateUtcMillis: Long, hour: Int, minute: Int): Long =
        LocalDateTime.of(utcDate(dateUtcMillis), LocalTime.of(hour, minute))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    fun ageText(birthMillis: Long): String {
        val p = Period.between(utcDate(birthMillis), LocalDate.now())
        return when {
            p.years >= 1 -> if (p.years == 1) "1 año" else "${p.years} años"
            p.months >= 1 -> if (p.months == 1) "1 mes" else "${p.months} meses"
            else -> "Menos de 1 mes"
        }
    }

    fun weightText(kg: Double): String =
        if (kg % 1.0 == 0.0) "${kg.toInt()} kg" else "$kg kg"
}
