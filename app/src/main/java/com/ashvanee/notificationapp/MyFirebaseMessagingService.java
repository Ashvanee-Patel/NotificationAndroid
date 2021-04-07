package com.ashvanee.notificationapp;

import android.annotation.SuppressLint;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String text = remoteMessage.getNotification().getBody();
            //calling method to display notification
            NotificationHelper.displayNotification(getApplicationContext(), title, text);
        }
    }
}
