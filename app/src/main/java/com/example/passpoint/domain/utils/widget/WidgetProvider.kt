package com.example.passpoint.domain.utils.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.passpoint.presentation.MainActivity

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Запускаем Worker для обновления данных
        val workRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun onEnabled(context: Context) {
        // При первом добавлении виджета
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }

    companion object {
        fun updateWidget(context: Context, appWidgetId: Int, views: RemoteViews) {
            val manager = AppWidgetManager.getInstance(context)
            manager.updateAppWidget(appWidgetId, views)
        }

        fun getPendingIntent(context: Context, screen: String): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("open_screen", screen)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            return PendingIntent.getActivity(context, screen.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}