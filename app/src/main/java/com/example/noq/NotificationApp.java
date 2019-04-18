package com.example.noq;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationApp extends Application {

    public static final String channel_ID = "channelID";

    private NotificationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channel_ID,
                    "notification channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setName("NoQ Notification Channel");
            notificationChannel.setDescription("This is NoQ Notification channel");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            NotificationManager notificationManager = (NotificationManager ) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

      }

    }


}
