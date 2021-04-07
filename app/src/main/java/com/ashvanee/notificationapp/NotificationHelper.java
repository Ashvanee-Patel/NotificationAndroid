package com.ashvanee.notificationapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.ashvanee.notificationapp.App.CHANNEL_ID;

public class NotificationHelper {
    public static void displayNotification(Context context, String title, String text){

        Intent activityIntent = new Intent(context, ProfileActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0, activityIntent,0);

        Intent broadCostIntent = new Intent(context,NotificationReceiver.class);
        broadCostIntent.putExtra("toastMessage","Hello Ashvanee ");

        PendingIntent actionIntent = PendingIntent.getBroadcast(context,0,broadCostIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setColor(Color.RED)
                .addAction(R.drawable.ic_notifications_24,"Toast",actionIntent)
                .setContentIntent(contentIntent);

        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(1, mBuilder.build());
    }
}
