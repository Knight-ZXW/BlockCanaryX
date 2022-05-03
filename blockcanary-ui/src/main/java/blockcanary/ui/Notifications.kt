package blockcanary.ui

import android.app.*
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build

internal object Notifications {

    var canShowNotification = true

    @Suppress("LongParameterList")
    fun showNotification(
        context: Context,
        contentTitle: CharSequence,
        contentText: CharSequence,
        pendingIntent: PendingIntent?,
        notificationId: Int,
        type: NotificationType
    ) {
        if (!canShowNotification) {
            return
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, type.name)
        } else Notification.Builder(context)

        builder
            .setContentText(contentText)
            .setContentTitle(contentTitle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notification =
            buildNotification(context, builder, type)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }


     fun buildNotification(
        context: Context,
        builder: Notification.Builder,
        type: NotificationType
    ): Notification {
        builder
            .setWhen(System.currentTimeMillis())
         builder.setSmallIcon(R.drawable.block_canary)

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notificationChannel: NotificationChannel? =
                notificationManager.getNotificationChannel(type.name)
            if (notificationChannel == null) {
                val channelName = context.getString(type.nameResId)
                notificationChannel =
                    NotificationChannel(type.name, channelName, type.importance)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            builder.setChannelId(type.name)
            builder.setGroup(type.name)
        }

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            @Suppress("DEPRECATION")
            builder.notification
        } else {
            builder.build()
        }
    }
}