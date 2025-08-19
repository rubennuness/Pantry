package com.smartgrocery.pantry.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartgrocery.pantry.data.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val repo = RoomRepository.get(applicationContext)
            val json = repo.exportJson()
            // TODO: Save JSON to cloud/local backup target
            // For now, just log length
            android.util.Log.i("BackupWorker", "Exported ${json.length} chars")
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }
}

