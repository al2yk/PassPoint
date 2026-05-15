package com.example.passpoint.domain.utils.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.passpoint.R
import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.data.dto.Event
import com.example.passpoint.domain.UserRepository
import com.example.passpoint.domain.repository.Repository
import com.example.passpoint.presentation.ui.theme.AppTheme
import com.example.passpoint.presentation.ui.theme.ThemeManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import com.example.passpoint.domain.model.Result as DomainResult

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: Repository,
    private val themeManager: ThemeManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): ListenableWorker.Result {
        return withContext(Dispatchers.IO) {
            UserRepository.init(applicationContext)

            val token = UserRepository.user_token
            if (token.isNullOrEmpty()) {
                updateWidgetsWithError("Войдите в приложение")
                return@withContext ListenableWorker.Result.success()
            }

            // Определяем текущую тему
            val theme = themeManager.themeFlow.first()
            val isDark = when (theme) {
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
                AppTheme.SYSTEM -> isSystemInDarkTheme(applicationContext)
            }

            val coursesResult = repository.getCourse()
            val eventsResult = repository.getEvent()

            var upcomingCourse: CourseWithEnrollment? = null
            var upcomingEvent: Event? = null

            if (coursesResult is DomainResult.Success) {
                val now = LocalDate.now()
                val filtered = coursesResult.data.filter { course ->
                    parseDate(course.date)?.let { !it.isBefore(now) } == true
                }
                upcomingCourse = filtered.minByOrNull { parseDate(it.date) ?: LocalDate.MAX }
            }

            if (eventsResult is DomainResult.Success) {
                val now = LocalDate.now()
                val filtered = eventsResult.data.filter { event ->
                    parseDate(event.date)?.let { !it.isBefore(now) } == true
                }
                upcomingEvent = filtered.minByOrNull { parseDate(it.date) ?: LocalDate.MAX }
            }

            if (upcomingCourse == null && upcomingEvent == null) {
                updateWidgetsWithError("Нет ближайших событий", isDark)
                return@withContext ListenableWorker.Result.success()
            }

            updateWidgets(upcomingEvent, upcomingCourse, isDark)
            ListenableWorker.Result.success()
        }
    }

    private fun isSystemInDarkTheme(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun parseDate(dateStr: String): LocalDate? {
        return try {
            LocalDate.parse(dateStr)
        } catch (e: DateTimeParseException) {
            Log.e("WidgetUpdateWorker", "Failed to parse date: $dateStr", e)
            null
        }
    }

    private fun updateWidgets(event: Event?, course: CourseWithEnrollment?, isDark: Boolean) {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(applicationContext, WidgetProvider::class.java)
        )
        if (appWidgetIds.isEmpty()) return

        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        // Выбираем ресурсы и цвета в зависимости от темы
        val bgRes = if (isDark) R.drawable.widget_bg_dark else R.drawable.widget_bg_light
        val cardBgRes = if (isDark) R.drawable.widget_card_bg_dark else R.drawable.widget_card_bg_light
        val primaryTextColor = if (isDark) Color.WHITE else Color.BLACK
        val secondaryTextColor = if (isDark) Color.parseColor("#CCCCCC") else Color.parseColor("#666666")
        val errorTextColor = Color.parseColor("#FF8888")

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(applicationContext.packageName, R.layout.widget_layout)

            // Устанавливаем фон всего виджета
            views.setInt(R.id.widget_layout, "setBackgroundResource", bgRes)

            // Настройка заголовка
            views.setTextColor(R.id.widget_title, primaryTextColor)

            // Настройка блока мероприятия
            if (event != null) {
                views.setInt(R.id.widget_event_container, "setBackgroundResource", cardBgRes)
                views.setTextViewText(R.id.widget_event_name, event.name)
                views.setTextColor(R.id.widget_event_name, primaryTextColor)
                views.setTextColor(R.id.widget_event_label, secondaryTextColor)
                val eventDate = parseDate(event.date)?.format(dateFormatter) ?: event.date
                views.setTextViewText(R.id.widget_event_date, eventDate)
                views.setTextColor(R.id.widget_event_date, secondaryTextColor)
                views.setViewVisibility(R.id.widget_event_container, View.VISIBLE)
                val eventIntent = WidgetProvider.getPendingIntent(applicationContext, "events")
                views.setOnClickPendingIntent(R.id.widget_event_container, eventIntent)
            } else {
                views.setViewVisibility(R.id.widget_event_container, View.GONE)
            }

            // Настройка блока курса
            if (course != null) {
                views.setInt(R.id.widget_course_container, "setBackgroundResource", cardBgRes)
                views.setTextViewText(R.id.widget_course_name, course.name)
                views.setTextColor(R.id.widget_course_name, primaryTextColor)
                views.setTextColor(R.id.widget_course_label, secondaryTextColor)
                val courseDate = parseDate(course.date)?.format(dateFormatter) ?: course.date
                views.setTextViewText(R.id.widget_course_date, courseDate)
                views.setTextColor(R.id.widget_course_date, secondaryTextColor)
                views.setViewVisibility(R.id.widget_course_container, View.VISIBLE)
                val courseIntent = WidgetProvider.getPendingIntent(applicationContext, "courses")
                views.setOnClickPendingIntent(R.id.widget_course_container, courseIntent)
            } else {
                views.setViewVisibility(R.id.widget_course_container, View.GONE)
            }

            views.setViewVisibility(R.id.widget_error, View.GONE)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun updateWidgetsWithError(errorText: String, isDark: Boolean = false) {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(applicationContext, WidgetProvider::class.java)
        )
        if (appWidgetIds.isEmpty()) return

        val bgRes = if (isDark) R.drawable.widget_bg_dark else R.drawable.widget_bg_light
        val primaryTextColor = if (isDark) Color.WHITE else Color.BLACK
        val errorTextColor = Color.parseColor("#FF8888")

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(applicationContext.packageName, R.layout.widget_layout)
            views.setInt(R.id.widget_layout, "setBackgroundResource", bgRes)
            views.setTextColor(R.id.widget_title, primaryTextColor)
            views.setTextViewText(R.id.widget_error, errorText)
            views.setTextColor(R.id.widget_error, errorTextColor)
            views.setViewVisibility(R.id.widget_error, View.VISIBLE)
            views.setViewVisibility(R.id.widget_event_container, View.GONE)
            views.setViewVisibility(R.id.widget_course_container, View.GONE)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}