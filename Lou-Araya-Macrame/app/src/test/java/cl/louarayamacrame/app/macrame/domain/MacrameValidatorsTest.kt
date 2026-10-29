package cl.louarayamacrame.app.macrame.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MacrameValidatorsTest {
    @Test fun aliasBlankIsRejected() { assertEquals("Este campo es obligatorio.", MacrameValidators.validateAlias("  ")) }
    @Test fun aliasNeedsTwoCharacters() { assertEquals("Ingresa al menos 2 caracteres.", MacrameValidators.validateAlias("A")) }
    @Test fun validAliasPasses() { assertNull(MacrameValidators.validateAlias("Cliente demo")) }
    @Test fun notesLimitIsValidated() { assertEquals("Usa un máximo de 300 caracteres.", MacrameValidators.validateNotes("x".repeat(301))) }
    @Test fun notesWithinLimitPass() { assertNull(MacrameValidators.validateNotes("Evento familiar")) }
}
