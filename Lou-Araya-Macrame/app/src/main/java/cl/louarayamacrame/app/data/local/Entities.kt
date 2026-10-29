package cl.louarayamacrame.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String
)

@Entity(tableName = "pets", indices = [Index("ownerId")])
data class PetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val name: String,
    val species: String,
    val breed: String,
    val birthDateMillis: Long,
    val weightKg: Double,
    val photoPath: String?
)

@Entity(
    tableName = "care_tasks",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class CareTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    val type: String,
    val dueMillis: Long,
    val notes: String,
    val done: Boolean = false
)
