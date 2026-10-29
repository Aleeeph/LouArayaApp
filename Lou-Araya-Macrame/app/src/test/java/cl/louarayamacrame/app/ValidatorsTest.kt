package cl.louarayamacrame.app

import cl.louarayamacrame.app.domain.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test
    fun email_valido_no_retorna_error() {
        assertNull(Validators.email("ana@correo.cl"))
    }

    @Test
    fun email_invalido_retorna_mensaje() {
        assertEquals("Ingresa un correo válido", Validators.email("ana@correo"))
        assertEquals("El correo es obligatorio", Validators.email("  "))
    }

    @Test
    fun password_requiere_8_caracteres_y_un_numero() {
        assertNotNull(Validators.password("abc123"))
        assertNotNull(Validators.password("abcdefghi"))
        assertNull(Validators.password("abcdefg1"))
    }

    @Test
    fun confirmacion_debe_coincidir() {
        assertEquals("Las contraseñas no coinciden", Validators.confirmPassword("abcdefg1", "abcdefg2"))
        assertNull(Validators.confirmPassword("abcdefg1", "abcdefg1"))
    }

    @Test
    fun nombre_de_mascota_valida_largo_y_caracteres() {
        assertNotNull(Validators.petName(""))
        assertNotNull(Validators.petName("A"))
        assertNotNull(Validators.petName("Nombre demasiado largo para mascota"))
        assertNull(Validators.petName("Luna"))
    }

    @Test
    fun peso_acepta_coma_decimal_y_rechaza_valores_invalidos() {
        assertNull(Validators.weight("12,5"))
        assertNotNull(Validators.weight("0"))
        assertNotNull(Validators.weight("abc"))
        assertNotNull(Validators.weight("500"))
    }

    @Test
    fun fecha_de_nacimiento_no_puede_ser_futura() {
        val now = 1_000_000L
        assertNull(Validators.birthDate(500_000L, now))
        assertEquals("La fecha no puede ser futura", Validators.birthDate(2_000_000L, now))
        assertNotNull(Validators.birthDate(null, now))
    }
}
