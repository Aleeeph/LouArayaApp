package cl.louarayamacrame.app.data.repository

import cl.louarayamacrame.app.data.local.UserDao
import cl.louarayamacrame.app.data.local.UserEntity
import cl.louarayamacrame.app.data.prefs.UserPreferences
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

sealed interface AuthResult {
    data class Success(val userId: Long) : AuthResult
    data class Error(val message: String) : AuthResult
}

class AuthRepository(
    private val userDao: UserDao,
    private val prefs: UserPreferences
) {
    suspend fun register(name: String, email: String, password: String): AuthResult {
        val mail = email.trim().lowercase()
        if (userDao.findByEmail(mail) != null) {
            return AuthResult.Error("Ya existe una cuenta con este correo")
        }
        val id = userDao.insert(
            UserEntity(name = name.trim(), email = mail, passwordHash = hash(mail, password))
        )
        prefs.setUserId(id)
        return AuthResult.Success(id)
    }

    suspend fun login(email: String, password: String): AuthResult {
        val mail = email.trim().lowercase()
        val user = userDao.findByEmail(mail)
        if (user == null || user.passwordHash != hash(mail, password)) {
            return AuthResult.Error("Correo o contraseña incorrectos")
        }
        prefs.setUserId(user.id)
        return AuthResult.Success(user.id)
    }

    fun observeUser(id: Long): Flow<UserEntity?> = userDao.observeById(id)

    suspend fun logout() = prefs.setUserId(null)

    /** Nunca se guarda la contraseña en texto plano: SHA-256 con el correo como sal. */
    private fun hash(email: String, password: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest("$email:$password".toByteArray())
            .joinToString("") { "%02x".format(it) }
}
