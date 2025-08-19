package com.smartgrocery.pantry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.smartgrocery.pantry.ui.AppRoot
import com.smartgrocery.pantry.ui.theme.AppTheme
import com.smartgrocery.pantry.work.BackupWorker
import java.util.concurrent.TimeUnit
import com.smartgrocery.pantry.AppLocator
import com.smartgrocery.pantry.ui.rememberAppState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleBackup()
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val state = rememberAppState()
                    AppLocator.appStateProvider = { state }
                    AppRoot()
                }
            }
        }
    }

    private fun scheduleBackup() {
        val work = PeriodicWorkRequestBuilder<BackupWorker>(12, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "pantry-backup",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }
}

