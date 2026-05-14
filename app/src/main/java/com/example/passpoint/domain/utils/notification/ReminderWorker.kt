package com.example.passpoint.domain.utils.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.passpoint.presentation.MainActivity

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val type = inputData.getString("type") ?: return Result.failure()
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("open_$type", true)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            applicationContext, title.hashCode(), intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        NotificationCompat.Builder(applicationContext, NotificationChannels.REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Напоминание")
            .setContentText("«$title» начнётся совсем скоро!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
            .also { NotificationManagerCompat.from(applicationContext).notify(title.hashCode(), it) }
        return Result.success()
    }
}