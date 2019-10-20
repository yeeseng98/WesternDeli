package com.yeeseng.westerndeli.presenter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.view.MainActivity;

public class NotificationIntentService extends JobIntentService {
    private static final int NOTIFICATION_ID = 3;
    private NotificationManager mNotificationManager;

    static final int SERVICE_JOB_ID = 50;

    // Enqueuing work in to this service.
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationIntentService.class, SERVICE_JOB_ID, work);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        onHandleIntent(intent);
    }

    private void onHandleIntent(Intent intent) {
        if (Build.VERSION.SDK_INT >= 26){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), "notify_001");
            Intent ii = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText("Western Deli");
            bigText.setBigContentTitle("Are you ready for authentic western food?");
            bigText.setSummaryText("Reminder");

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.mipmap.my_launcher);
            mBuilder.setContentTitle("Title");
            mBuilder.setContentText("Text");
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigText);

            mNotificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "Your_channel_id";
            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel(channelId,
                        "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);

                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);

                mNotificationManager.notify(0, mBuilder.build());
            }


        } else {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle("My Title");
            builder.setContentText("This is the Body");
            builder.setSmallIcon(R.mipmap.my_launcher);
            Intent notifyIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //to be able to launch your activity from the notification
            builder.setContentIntent(pendingIntent);
            Notification notificationCompat = builder.build();
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(NOTIFICATION_ID, notificationCompat);
        }    }
}
