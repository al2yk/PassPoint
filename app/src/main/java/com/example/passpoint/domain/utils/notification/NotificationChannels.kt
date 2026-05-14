package com.example.passpoint.domain.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat

object NotificationChannels {
    const val NEWS = "news_channel"
    const val REMINDER = "reminder_channel"
    const val CERTIFICATE = "certificate_channel"
    const val ATTENDANCE = "attendance_channel"

    fun create(context: Context) {
        val manager = NotificationManagerCompat.from(context)

        manager.createNotificationChannels(listOf(
            NotificationChannel(NEWS, "Новости", NotificationManager.IMPORTANCE_HIGH),
            NotificationChannel(REMINDER, "Напоминания", NotificationManager.IMPORTANCE_HIGH),
            NotificationChannel(CERTIFICATE, "Сертификаты", NotificationManager.IMPORTANCE_HIGH),
            NotificationChannel(ATTENDANCE, "Посещаемость", NotificationManager.IMPORTANCE_HIGH)
        ))
    }
}