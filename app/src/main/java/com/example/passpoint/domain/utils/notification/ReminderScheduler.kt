package com.example.passpoint.domain.worker

import android.content.Context
import androidx.work.*
import com.example.passpoint.domain.utils.notification.AttendanceReminderWorker
import com.example.passpoint.domain.utils.notification.ReminderWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Duration
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    /** Запланировать напоминание куратору на утро дня курса */
    fun scheduleAttendanceReminder(context: Context, courseDate: String) {
        try {
            val date = LocalDate.parse(courseDate)
            val now = LocalDate.now()
            if (date < now) return

            val reminderTime = date.atTime(8, 0) // утро в 8:00
            val delay = Duration.between(LocalDateTime.now(), reminderTime).toMillis()
            if (delay <= 0) return

            val work = OneTimeWorkRequestBuilder<AttendanceReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("attendance_reminder")
                .build()
            WorkManager.getInstance(context).enqueue(work)
        } catch (_: Exception) {}
    }

    /** Запланировать напоминание о мероприятии/курсе за день и за час */
    fun scheduleReminder(context: Context, title: String, date: String, type: String) {
        try {
            val eventDate = LocalDate.parse(date)
            val now = LocalDate.now()
            if (eventDate <= now) return

            val dayBefore = eventDate.atStartOfDay().minusDays(1)
            val hourBefore = eventDate.atStartOfDay().minusHours(1)

            listOf(dayBefore, hourBefore).forEach { remindAt ->
                val delay = Duration.between(LocalDateTime.now(), remindAt).toMillis()
                if (delay > 0) {
                    val work = OneTimeWorkRequestBuilder<ReminderWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(workDataOf("title" to title, "type" to type))
                        .addTag("event_reminder")
                        .build()
                    WorkManager.getInstance(context).enqueue(work)
                }
            }
        } catch (_: Exception) {}
    }
}