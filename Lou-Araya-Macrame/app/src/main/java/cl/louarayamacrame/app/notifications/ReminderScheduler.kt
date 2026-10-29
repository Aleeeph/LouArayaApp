package cl.louarayamacrame.app.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

/** Programa (y cancela) los recordatorios usando WorkManager. */
class ReminderScheduler(private val context: Context) {

    fun schedule(taskId: Long, petName: String, type: String, dueMillis: Long) {
        val delay = (dueMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    ReminderWorker.KEY_TASK_ID to taskId,
                    ReminderWorker.KEY_PET_NAME to petName,
                    ReminderWorker.KEY_TYPE to type
                )
            )
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork("task_$taskId", ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(taskId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("task_$taskId")
    }
}
