package com.codepath.watertracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (NotificationHelper.areNotificationsEnabled(context)) {
                val hour = NotificationHelper.getNotificationHour(context)
                val minute = NotificationHelper.getNotificationMinute(context)
                NotificationHelper.scheduleDailyNotification(context, hour, minute)
            }
        }
    }
}
