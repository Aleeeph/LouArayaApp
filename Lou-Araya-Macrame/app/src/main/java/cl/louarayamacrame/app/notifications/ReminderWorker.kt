package cl.louarayamacrame.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.louarayamacrame.app.data.local.AppDatabase
import cl.louarayamacrame.app.data.prefs.UserPreferences
import kotlinx.coroutines.flow.first

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        val petName = inputData.getString(KEY_PET_NAME).orEmpty()
        val type = inputData.getString(KEY_TYPE).orEmpty()

        val task = AppDatabase.get(applicationContext).careTaskDao().getById(taskId)
            ?: return Result.success() // la tarea fue eliminada
        if (task.done) return Result.success()
        if (!UserPreferences(applicationContext).notificationsEnabled.first()) return Result.success()

        NotificationHelper.show(
            context = applicationContext,
            id = taskId.toInt(),
            title = "$type · $petName",
            text = task.notes.ifBlank { "Es hora de este cuidado para $petName" }
        )
        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_PET_NAME = "pet_name"
        const val KEY_TYPE = "type"
    }
}
