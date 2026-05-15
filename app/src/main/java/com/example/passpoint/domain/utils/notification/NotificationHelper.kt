package com.example.passpoint.domain.utils.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.passpoint.R

object NotificationHelper {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun show(
        context: Context,
        channelId: String,
        title: String,
        body: String,
        intent: PendingIntent? = null
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.pass_point)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        intent?.let { builder.setContentIntent(it) }
        NotificationManagerCompat.from(context).notify(
            System.currentTimeMillis().toInt(), builder.build()
        )
    }
}
