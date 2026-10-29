package cl.louarayamacrame.app.data.repository

import cl.louarayamacrame.app.data.local.CareTaskDao
import cl.louarayamacrame.app.data.local.CareTaskEntity
import cl.louarayamacrame.app.data.local.PetDao
import cl.louarayamacrame.app.data.local.PetEntity
import cl.louarayamacrame.app.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import java.io.File

class PetRepository(
    private val petDao: PetDao,
    private val taskDao: CareTaskDao,
    private val scheduler: ReminderScheduler
) {
    fun observePets(ownerId: Long): Flow<List<PetEntity>> = petDao.observePets(ownerId)
    fun observePet(id: Long): Flow<PetEntity?> = petDao.observePet(id)
    suspend fun getPet(id: Long): PetEntity? = petDao.getPet(id)

    suspend fun savePet(pet: PetEntity) {
        petDao.upsert(pet)
    }

    suspend fun deletePet(pet: PetEntity) {
        taskDao.getByPet(pet.id).forEach { scheduler.cancel(it.id) }
        petDao.delete(pet) // las tareas se eliminan en cascada
        pet.photoPath?.let { File(it).delete() }
    }

    fun observeTasksByPet(petId: Long): Flow<List<CareTaskEntity>> = taskDao.observeByPet(petId)
    fun observeTasksByOwner(ownerId: Long): Flow<List<CareTaskEntity>> = taskDao.observeByOwner(ownerId)

    suspend fun addTask(petName: String, task: CareTaskEntity) {
        val id = taskDao.upsert(task)
        scheduler.schedule(id, petName, task.type, task.dueMillis)
    }

    suspend fun setTaskDone(task: CareTaskEntity, petName: String, done: Boolean) {
        taskDao.setDone(task.id, done)
        if (done) {
            scheduler.cancel(task.id)
        } else if (task.dueMillis > System.currentTimeMillis()) {
            scheduler.schedule(task.id, petName, task.type, task.dueMillis)
        }
    }

    suspend fun deleteTask(task: CareTaskEntity) {
        taskDao.delete(task)
        scheduler.cancel(task.id)
    }
}
