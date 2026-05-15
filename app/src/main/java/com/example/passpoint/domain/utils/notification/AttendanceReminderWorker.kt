package com.example.passpoint.domain.utils.notification

import android.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.passpoint.presentation.MainActivity

class AttendanceReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("open_curator_courses", true)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            applicationContext, 200, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        NotificationCompat.Builder(applicationContext, NotificationChannels.ATTENDANCE)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("Не забудьте отметить посещаемость")
            .setContentText("У вас сегодня есть курсы. Проверьте список участников.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
            .also { NotificationManagerCompat.from(applicationContext).notify(200, it) }
        return Result.success()
    }
}