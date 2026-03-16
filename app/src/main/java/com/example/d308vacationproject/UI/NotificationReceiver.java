package com.example.d308vacationproject.UI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.d308vacationproject.R;

// BroadcastReceiver that handles scheduled alarm intents and displays notifications.
// Triggered by AlarmManager when a vacation start/end date or excursion date arrives.
public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "vacation_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the notification message passed from the scheduling activity
        String message = intent.getStringExtra("message");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel (required for Android 8.0+ / API 26+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Vacation Alerts", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
        }

        // Build the notification with title, message, and app icon
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Vacation Planner Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Use a unique notification ID so multiple notifications don't overwrite each other
        int notificationId = intent.getIntExtra("notification_id", 0);
        nm.notify(notificationId, builder.build());
    }
}
