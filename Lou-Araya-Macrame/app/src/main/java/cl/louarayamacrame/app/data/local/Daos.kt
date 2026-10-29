package cl.louarayamacrame.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<UserEntity?>
}

@Dao
interface PetDao {
    @Query("SELECT * FROM pets WHERE ownerId = :ownerId ORDER BY name COLLATE NOCASE")
    fun observePets(ownerId: Long): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :id LIMIT 1")
    fun observePet(id: Long): Flow<PetEntity?>

    @Query("SELECT * FROM pets WHERE id = :id LIMIT 1")
    suspend fun getPet(id: Long): PetEntity?

    @Upsert
    suspend fun upsert(pet: PetEntity): Long

    @Delete
    suspend fun delete(pet: PetEntity)
}

@Dao
interface CareTaskDao {
    @Query("SELECT * FROM care_tasks WHERE petId = :petId ORDER BY dueMillis ASC")
    fun observeByPet(petId: Long): Flow<List<CareTaskEntity>>

    @Query("SELECT * FROM care_tasks WHERE petId = :petId")
    suspend fun getByPet(petId: Long): List<CareTaskEntity>

    @Query(
        "SELECT c.* FROM care_tasks c INNER JOIN pets p ON p.id = c.petId " +
            "WHERE p.ownerId = :ownerId ORDER BY c.dueMillis ASC"
    )
    fun observeByOwner(ownerId: Long): Flow<List<CareTaskEntity>>

    @Query("SELECT * FROM care_tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CareTaskEntity?

    @Upsert
    suspend fun upsert(task: CareTaskEntity): Long

    @Delete
    suspend fun delete(task: CareTaskEntity)

    @Query("UPDATE care_tasks SET done = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)
}
